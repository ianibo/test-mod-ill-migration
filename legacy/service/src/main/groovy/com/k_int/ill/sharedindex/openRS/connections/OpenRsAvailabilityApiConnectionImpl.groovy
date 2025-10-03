package com.k_int.ill.sharedindex.openRS.connections;

import static groovyx.net.http.ApacheHttpBuilder.configure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.k_int.ill.settings.SharedIndexSettings;
import com.k_int.ill.sharedindex.openRS.availability.OpenRsAvailabilityResult;

import groovy.util.logging.Slf4j;
import groovyx.net.http.FromServer;
import groovyx.net.http.HttpBuilder;

/**
 */
@Slf4j
public class OpenRsAvailabilityApiConnectionImpl implements OpenRsAvailabilityApiConnection {

    @Autowired
    OpenRsTokenApiConnection openRsTokenApiConnection;

    public OpenRsAvailabilityResult get(SharedIndexSettings sharedIndexSettings, String id) {

        // Need to check if we need to obtain a token first, for the time being we do not need a token ....
        String token = openRsTokenApiConnection.get(sharedIndexSettings);

        HttpBuilder openRsSharedIndex = configure {
            request.uri = sharedIndexSettings.getAvailabilityUrl();

            // If we have a token then we need to set it
            if (token) {
                // Do whatever to set the token
                request.headers['Authorization'] = "Bearer $token"
            }
        }

        // Execute the query
        Object json = openRsSharedIndex.get {
            request.accept='application/json';
            request.uri.query = [
                'clusteredBibId' : id,
				'filters' : 'none'
            ];

            response.success { FromServer fs, Object body ->
                log.debug("Result from availabilty service: " + body.toString());
                return(body);
            }
            response.failure { FromServer fs, Object body ->
                log.error("Problem ${body} ${fs} ${fs.getStatusCode()}");
                return(null);
            }
        }

        // If we did get a result, convert it into something sensible
        OpenRsAvailabilityResult availabilityResult = null;
        if (json != null) {
            try {
                // We have a message, so we need to generate the class instance
                ObjectMapper objectMapper = new ObjectMapper();
                availabilityResult = objectMapper.convertValue(json, OpenRsAvailabilityResult.class);
            } catch (Exception e) {
                log.error("Exception thrown while converting json to an OpenRsAvailabilityResult instance", e);
            }
        }

        // Return the result to the caller
        return(availabilityResult);
    }
}
