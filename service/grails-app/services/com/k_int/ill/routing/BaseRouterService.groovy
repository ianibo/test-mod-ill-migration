package com.k_int.ill.routing;

import com.k_int.directory.DirectoryEntryService;
import com.k_int.directory.Symbol;
import com.k_int.ill.AvailabilityStatement;
import com.k_int.ill.IllStatisticSymbol;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.ProtocolType;
import com.k_int.ill.StatisticsService;
import com.k_int.ill.constants.Directory;
import com.k_int.ill.logging.IHoldingLogDetails;
import com.k_int.ill.logging.ProtocolAuditService;

/**
 * The base class used by request routing
 * It is expected that a derived class will override one of the findMoreSuppliers methods otherwise
 * we will end up with a stackoverflow as these 2 methods just call one another
 */
public abstract class BaseRouterService implements RequestRouter {

	DirectoryEntryService directoryEntryService;
	ProtocolAuditService protocolAuditService;
	StatisticsService statisticsService;

	private final RouterInformation routerInformation;
	private final ProtocolType protocolType;

	protected BaseRouterService(
		String name,
		String description,
		ProtocolType protocolType = ProtocolType.SHARED_INDEX
	) {
		// Setup the router information object
		routerInformation = new RouterInformation(name, description);
		this.protocolType = protocolType;
	}
		 
	public RouterInformation getRouterInfo() {
		return(routerInformation);
	}

	public List<RankedSupplier> findMoreSuppliers(
		PatronRequest patronRequest
	) {
		// Create ourselves a logger for the holdings
        IHoldingLogDetails holdingLogDetails = protocolAuditService.getHoldingLogDetails(
			patronRequest.institution,
			protocolType
		);
		
		// Now make the call
		List<RankedSupplier> rankedSuppliers = findMoreSuppliers(patronRequest, holdingLogDetails);
		
		// Save any auditing that may have happened
		protocolAuditService.save(patronRequest, holdingLogDetails);


		// Now return the results
		return(rankedSuppliers);
	}

	public List<RankedSupplier> findMoreSuppliers(
		PatronRequest patronRequest,
		IHoldingLogDetails overrideHoldingLogDetails
	) {
		// By default we do not pass on the overideHoldingLogDetails
		return(findMoreSuppliers(patronRequest));
	}

	/**
	 * Take a list of availability statements and turn it into a ranked rota
	 * @param sia - List of AvailabilityStatement
	 * @return [
	 *   [
	 *     symbol:
	 *   ]
	 * ]
	 */
	protected List<RankedSupplier> createRankedRota(List<AvailabilityStatement> sia) {
		log.debug("createRankedRota(${sia})");
		List<RankedSupplier> result = new ArrayList<RankedSupplier>()

		sia.each { av_stmt ->
			log.debug("Considering rota entry: ${av_stmt}");

			// 1. look up the directory entry for the symbol
			Symbol s = ( av_stmt.symbol != null ) ? directoryEntryService.resolveCombinedSymbol(av_stmt.symbol) : null;

			if ( s != null ) {
				log.debug("Refine availability statement ${av_stmt} for symbol ${s}");

				// 2. Is the directory entry lending
				def isLending = directoryEntryService.directoryEntryIsLending(s.owner);

				if ( isLending ) {
					IllStatisticSymbol peer_stats = statisticsService.getStatsFor(s);

					def loadBalancingScore = null;
					def loadBalancingReason = null;
					def ownerStatus = s.owner?.status?.value;
					log.debug("Found status of ${ownerStatus} for symbol ${s}");

					if ( ownerStatus == null ) {
						log.debug("Unable to get owner status for ${s}");
					}

					if ( ownerStatus != null && ( ownerStatus == "Managed" || ownerStatus == "managed" )) {
						loadBalancingScore = 10000;
						loadBalancingReason = "Local lending sources prioritized";
					} else if ( peer_stats != null ) {
						// 3. See if we can locate load balancing informaiton for the entry - if so, calculate a score, if not, set to 0
						double lbr = peer_stats.libraryLoanRatio / peer_stats.libraryBorrowRatio;
						long target_lending = peer_stats.currentBorrowingLevel * lbr;
						loadBalancingScore = target_lending - peer_stats.currentLoanLevel;
						loadBalancingReason = "LB Ratio ${peer_stats.libraryLoanRatio}:${peer_stats.libraryBorrowRatio}=${lbr}. Actual Borrowing=${peer_stats.currentBorrowingLevel}. Target loans=${target_lending} Actual loans=${peer_stats.currentLoanLevel} Distance/Score=${loadBalancingScore}";
					} else {
						loadBalancingScore = 0;
						loadBalancingReason = 'No load balancing information available for peer'
					}

					RankedSupplier rota_entry = new RankedSupplier(
						supplier_symbol: av_stmt.symbol,
                        instance_identifier: av_stmt.instanceIdentifier,
                        copy_identifier: av_stmt.copyIdentifier,
                        ill_policy: av_stmt.illPolicy,
                        rank: loadBalancingScore,
                        rankReason: loadBalancingReason
					);
					result.add(rota_entry)
				} else {
					def entry_loan_policy = directoryEntryService.parseCustomPropertyValue(s.owner, Directory.KEY_ILL_POLICY_LOAN);
					log.debug("Directory entry says not currently lending - ${av_stmt.symbol}/policy=${entry_loan_policy}");
				}
			} else {
				log.debug("Unable to locate symbol ${av_stmt.symbol}");
			}
		}

		def sorted_result = result.toSorted { a,b -> b.rank <=> a.rank }
		log.debug("createRankedRota returns ${sorted_result}");
		return(sorted_result);
	}
}
