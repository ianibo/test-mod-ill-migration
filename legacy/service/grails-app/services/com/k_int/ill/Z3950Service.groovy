package com.k_int.ill;

/**
 * Send a query to a Z39.50 system. Currently this abstraction likely only supports doing so if a proxy is configured.
 */
import com.k_int.ill.logging.IHoldingLogDetails;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.settings.ISettings;
import com.k_int.institution.Institution;
import com.k_int.settings.SystemSettingsService;
import groovy.xml.XmlUtil;
import groovyx.net.http.HttpBuilder;

class Z3950Service {
	SystemSettingsService systemSettingsService;
	
    def query(
        Institution institution,
        ISettings settings,
        String query,
        int maximumHits,
        String schema,
        IHoldingLogDetails holdingLogDetails
    ) {
        String z3950Address = settings.getSettingValue(
            institution,
            SettingsData.SETTING_Z3950_SERVER_ADDRESS
        );
        if (!z3950Address) {
			throw new Exception('Unable to query Z39.50, no server configured');
        }
		return(query(
			z3950Address,
			query,
			maximumHits,
			schema,
			holdingLogDetails
		));
    }
	
    def query(
		String z3950Address,
        String query,
        int maximumHits,
        String schema,
        IHoldingLogDetails holdingLogDetails
    ) {
		def z3950Response = null;
		
		// Have we been supplied with a z3950 address
		if (z3950Address) {
			// The proxy server is held in the system settings
	        String z3950Proxy = systemSettingsService.getSettingValue(
	            SettingsData.SETTING_Z3950_PROXY_ADDRESS
	        );
	
			// Did we find a proxy server		
	        if (z3950Proxy) {
		        z3950Response = HttpBuilder.configure {
		            request.uri = z3950Proxy;
		        }.get {
		            request.uri.path = '/'
		            request.uri.query = [
						'x-target': z3950Address,
		                'x-pquery': query,
		                'maximumRecords': "$maximumHits"
					];
		
		            if (schema) {
		                request.uri.query['recordSchema'] = schema;
		            }
		
		            holdingLogDetails.newSearch(request.uri?.toURI().toString());
		            log.debug("Querying z server with URL ${request.uri?.toURI().toString()}");
		        }
		
		        log.trace("Got Z3950 response: ${XmlUtil.serialize(z3950Response)}");
		        holdingLogDetails.numberOfRecords(z3950Response?.numberOfRecords?.toLong());
		        holdingLogDetails.searchRequest(z3950Response?.echoedSearchRetrieveRequest);
	        } else {
				holdingLogDetails.newSearch("No Z3950 address supplied");
			} 
		} else {
            holdingLogDetails.newSearch("No Z3950 address supplied");
		}
        return(z3950Response);
    }
}
