package com.k_int.ill;

import com.k_int.directory.ServiceAccount;
import com.k_int.directory.ServiceAccountService;
import com.k_int.directory.Symbol;
import com.k_int.ill.constants.Directory;
import com.k_int.institution.Institution;

import groovy.json.JsonSlurper;

/**
 * This service takes responsibility for assembling/aggregating all the data needed
 * to build a ranked rota entry.
 */
public class StatisticsService {

    private static final String REQUEST_STATUS_COUNT_QUERY = '''
select pr.stateModel.shortcode, pr.state.code, count(pr.id)
from PatronRequest as pr
where pr.institution = :institution
group by pr.stateModel.shortcode, pr.state.code
order by pr.stateModel.shortcode, pr.state.code
''';

    private static final String REQUEST_STATUS_TAG_COUNT_QUERY = '''
select tag.value, count(pr.id)
from PatronRequest as pr
     join pr.state.tags as tag
where pr.institution = :institution
group by tag.value
order by tag.value
''';

    ServiceAccountService serviceAccountService;

    private static final long MAX_CACHE_AGE = 60 * 5 * 1000; // 5mins

    private Map<String, IllStatisticSymbol> stats_cache = [ : ]

    public IllStatistics generateStatistics(Institution institution, boolean includeByTag) {
        IllStatistics illStatistics = new IllStatistics();
        try {
            Counter.findAllByInstitution(institution).each { Counter counter ->
				illStatistics.addCurrent(counter.context, counter.value, counter.description);
            };
            generateRequestsByState(illStatistics, institution);

            // Do they want the statistics by tag
            if (includeByTag) {
                generateRequestsByStateTag(illStatistics, institution);
            }
        } catch(Exception e) {
            log.error("Exception thrown while generating statistics", e);
            illStatistics.setError(e.toString());
        }
        return(illStatistics);
    }

    /**
     * Given a symbol, try to retrieve the stats for a symbol - if needed, refresh the cache
     */
    public IllStatisticSymbol getStatsFor(Symbol symbol) {

        log.debug("StatisticsService::getStatsFor(${symbol})");

        IllStatisticSymbol result = null;
        try {
            result = refreshStatsFor(symbol);
        } catch (Exception e) {
            log.error("problem fetching stats for ${symbol}", e);
        }

        log.debug("getStatsFor(${symbol}) returns ${result}");
        return result;
    }

    private IllStatisticSymbol refreshStatsFor(Symbol symbol) {

        log.debug("StatisticsService::refreshStatsForrefreshStatsFor(${symbol})");
        IllStatisticSymbol result = null;

        if ( symbol != null ) {
            String symbol_str = "${symbol.authority.symbol}:${symbol.symbol}".toString()
            result = stats_cache[symbol_str]

            if ((result == null) ||
                (System.currentTimeMillis() - result.timestamp > MAX_CACHE_AGE)) {

                // symbol.owner.customProperties is a CustomPropertyContainer which means it's value is a list of custom properties
                try {
                    def ratio_custprop = symbol.owner.customProperties.value.find { it.definition?.name == Directory.KEY_ILL_POLICY_BORROW_RATIO };
                    String ratio = ratio_custprop?.value

                    if (ratio == null) {
                        log.warn('Unable to find ${Directory.KEY_ILL_POLICY_BORROW_RATIO} in custom properties, using 1:1 as a default');
                        ratio = "1:1"
                        symbol.owner.customProperties.value.each { cp ->
                            log.info("    custom properties that are present: ${cp.definition?.name} -> ${cp.value}");
                        }
                    }

                    ServiceAccount sa = symbol.owner.services.find { it.service.businessFunction?.value?.toUpperCase() == 'RS_STATS' };
                    String stats_url = null;
                    Map additionalHeaders = serviceAccountService.getAdditonalHeaders(sa);
                    stats_url = sa?.service?.address;

                    log.debug("URL for stats is : ${stats_url}, ratio is ${ratio}");

                    // If the url ends in = append the naming authority and symbol
                    if ((ratio != null) && stats_url && stats_url.size()> 1) {
                        if (stats_url.substring(stats_url.size() - 1) == '=') {
                            stats_url += "${symbol.authority.symbol}:${symbol.symbol}";
                        }
                        String stats_json = new java.net.URL(stats_url).getText([ requestProperties : additionalHeaders]);
                        result = processRatioInfo(stats_json, ratio);
                        stats_cache[symbol_str] = result;
                    } else {
                        log.warn("No stats service available for ${symbol}. Found the following services");
                        symbol.owner.services.each {
                            log.warn("    -> declared service: ${it.service.businessFunction}/${it.service.businessFunction?.value} != RS_STATS");
                        }

                        // No stats available so return data which will place this symbol at parity
                        result = new IllStatisticSymbol(
                            1,
                            1,
                            1,
                            1,
                            'No stats service available'
                        );
                    }
                } catch ( Exception e ) {
                    log.error("Exception processing stats",e);
                }
            }
            log.debug("Result of refreshStatsFor : ${result}");
        }
        return result;
    }

    public IllStatisticSymbol processRatioInfo(String stats_json, String ratio) {
        def current_stats = new JsonSlurper().parseText(stats_json)
        if ( current_stats.requestsByTag != null )
            return processDynamicRatioInfo(current_stats,ratio);
        else
            return processCounterBasedRatioInfo(current_stats,ratio);
    }

    // Extract into more testable lump
    private IllStatisticSymbol processDynamicRatioInfo(Map current_stats, String ratio) {
        log.debug("Loan to borrow ratio is : ${ratio}");
        log.debug("Stats output is : ${current_stats}");

        IllStatisticSymbol result = null;

        String[] parsed_ratio = ratio.split(':')
        long ratio_loan = Long.parseLong(parsed_ratio[0])
        long ratio_borrow = Long.parseLong(parsed_ratio[1])

        if (current_stats) {
            long current_loans = current_stats.requestsByTag.ACTIVE_LOAN ?: 0
            long current_borrowing = current_stats.requestsByTag.ACTIVE_BORROW ?: 0
            result = new IllStatisticSymbol(
                ratio_loan,
                ratio_borrow,
                current_loans,
                current_borrowing,
                'Statistics collected from stats service'
            );
        }
        return result;
    }

    private IllStatisticSymbol processCounterBasedRatioInfo(Map current_stats, String ratio) {

        log.debug("Loan to borrow ratio is : ${ratio}");
        log.debug("Stats output is : ${current_stats}");

        IllStatisticSymbol result = null;

        String[] parsed_ratio = ratio.split(':')
        long ratio_loan = Long.parseLong(parsed_ratio[0])
        long ratio_borrow = Long.parseLong(parsed_ratio[1])

        if (current_stats) {
            def activeContext = current_stats.current.find { it.context == constants.Counter.COUNTER_ACTIVE_LOANS };
            long current_loans = ((activeContext == null) ? 0 : activeContext.value);
            def borrowingContext = current_stats.current.find { it.context == constants.Counter.COUNTER_ACTIVE_BORROWING };
            long current_borrowing = ((borrowingContext == null) ? 0 : borrowingContext.value);
            result = new IllStatisticSymbol(
                ratio_loan,
                ratio_borrow,
                current_loans,
                current_borrowing,
                'Statistics collected from stats service'
            );
        }
        return result;
    }

    private void generateRequestsByState(IllStatistics illStatistics, Institution institution) {
        PatronRequest.executeQuery(REQUEST_STATUS_COUNT_QUERY, [ institution : institution ]).each { sl ->
            illStatistics.addState(sl[0] + ":" + sl[1], sl[2]);
        }
    }

    private void generateRequestsByStateTag(IllStatistics illStatistics, Institution institution) {
        PatronRequest.executeQuery(REQUEST_STATUS_TAG_COUNT_QUERY, [ institution : institution ]).each { sl ->
            illStatistics.addTag(sl[0], sl[1]);
        }
    }
}
