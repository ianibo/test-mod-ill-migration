package com.k_int.directory

import grails.gorm.transactions.Transactional;

@Transactional
public class ServiceAccountService {

    public Map getAdditonalHeaders(ServiceAccount serviceAccount) {
        Map additionalHeaders = [ : ];

        // Do we have a service account
        if (serviceAccount != null) {
            // Do we have custom properties for this account
            if (serviceAccount.customProperties != null) {
                // We do so loop through them
                serviceAccount.customProperties.value.each { customProperties ->
                    // Does this one hold the additional headers
                    if (customProperties.definition?.name == 'AdditionalHeaders') {
                        // It does so we need to process each header which is separated by a comma
                        customProperties.value.split(',').each { String header ->
                            // The header itself is separted by a colon with the value after the colon
                            String[] headerParts = header.split(':');
                            if (headerParts && (headerParts.length == 2)) {
                                // We have a legitimate header and value
                                additionalHeaders[headerParts[0]] = headerParts[1];
                            }
                        }
                    }
                }
            }

            // Finally return the headers to the caller
            return(additionalHeaders);
        }
    }

	/**
	 * Finds the services being offered by the service filtered by the business function and protocol types 	
	 * @param symbol The symbol to be used to find the service accounts
	 * @param businessFunction The business function we are interested in
	 * @param protocolTypes The specific protocol types we are interested in
	 * @return The found service accounts
	 */
	public List<ServiceAccount> findServices(
		Symbol symbol,
		String businessFunction,
		List<String> protocolTypes
	) {
		List<ServiceAccount> result = ServiceAccount.executeQuery('''
select sa 
from ServiceAccount as sa
	join sa.accountHolder.symbols as symbol
where symbol.id = :symbolId and
      sa.service.businessFunction.label = :businessFunction and
      sa.service.type.label in (:protocolTypes)
''',
			[
				businessFunction: businessFunction,
				protocolTypes: protocolTypes,
				symbolId: symbol.id
			]
		);

		return(result);
	}
}
