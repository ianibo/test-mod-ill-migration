package com.k_int.ill.sharedindex.openRS.connections;

import static groovyx.net.http.ApacheHttpBuilder.configure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.k_int.ill.settings.SharedIndexSettings;
import com.k_int.ill.sharedindex.openRS.clusterRecord.OpenRsClusterRecord;
import com.k_int.ill.sharedindex.openRS.clusterRecord.OpenRsClusterResult;

import groovy.util.logging.Slf4j;
import groovyx.net.http.FromServer;
import groovyx.net.http.HttpBuilder;

/**
 */
@Slf4j
public class OpenRsSharedIndexApiConnectionImpl implements OpenRsSharedIndexApiConnection {

    private static final String replaceFrom      = "#FROM#";
    private static final String replaceQuery     = "#QUERY#";
    private static final String replaceQueryTerm = "#QUERYTERM#";
    private static final String replaceSize      = "#SIZE#";

    private static final String termQuery = '''
{
	"track_total_hits": 1000000,
	"query": {
		"term": {
			"bibClusterId.keyword": {
				"value": "''' + replaceQueryTerm + '''"
			}
		}
	}
}
''';

   private static final String query = '''
{
    "track_total_hits": 1000000,
    "query": {
        "query_string": {
            "query": "''' + replaceQuery + '''"
        }
    },
    "size": ''' + replaceSize + ''',
    "from": ''' + replaceFrom + '''
}
''';

    public OpenRsClusterResult getId(SharedIndexSettings sharedIndexSettings, String id) {
        return(get(sharedIndexSettings, termQuery.replace(replaceQueryTerm, id)));
    }

    public OpenRsClusterResult getQuery(SharedIndexSettings sharedIndexSettings, String queryString, long from, long size) {
        // Ensure we have a sensible value for from
        if ((from == null) || (from < 0)) {
            from = 0;
        }

        // Ensure we have a sensible value for size (Note: A value of 0 means they just want the number of hits)
        if ((size == null) || (size > 100) || (size < 0)) {
            size = 100;
        }

        // Execute the search and return the records
        OpenRsClusterResult openRsClusterResult = get(sharedIndexSettings, query.replace(replaceQuery, queryString).replace(replaceFrom, from.toString()).replace(replaceSize, size.toString()));

		if (openRsClusterResult != null) {
		    // Set the start position
		    openRsClusterResult.startPosition = from;
		
		    // Set the number of hits requested
		    openRsClusterResult.requestedHits = size;
		}
		
        // Return the result to the caller
        return(openRsClusterResult);
    }

    private OpenRsClusterResult get(SharedIndexSettings sharedIndexSettings, String query) {

        OpenRsClusterResult openRsClusterResult = null;

		log.info("Posting DCB Index Query\n" + query + "\nto url " + sharedIndexSettings.getBaseUrl()); 
        // Configure the index
        HttpBuilder openRsSharedIndex = configure {
            request.uri = sharedIndexSettings.getBaseUrl();

            // Only set the authentication if we have a user and password
            String user = sharedIndexSettings.getUser();
            if (user) {
                String password = sharedIndexSettings.getPassword();
                if (password) {
                    request.auth.basic(sharedIndexSettings.getUser(), sharedIndexSettings.getPassword());
                }
            }
        }

		try {
	        // Post the query
	        Object result = openRsSharedIndex.post {
	            request.accept='application/json';
	            request.contentType = "application/json";
	            request.body = query;
	
	            response.success { FromServer fs, Object body ->
	                openRsClusterResult = processSharedIndexResults(body);
	                return(body);
	            }
	            response.failure { FromServer fs, Object body ->
	                log.error("Problem ${body} ${fs} ${fs.getStatusCode()}");
	                return(null);
	            }
	        }
		} catch (Exception e) {
			log.error("Exception thrown while searching shared server" , e);
		}

        // Return the result to the caller
        return(openRsClusterResult);
    }

    /**
     * Processes a returned body turning it into a OpenRsClusterResult record
     * @param body The body that was returned by the server
     * @return The result record or null if we did not get a valid result
     */
    private OpenRsClusterResult processSharedIndexResults(Object body) {
        OpenRsClusterResult openRsClusterResult = null;

        // Do we have an instance of a map
        if (body instanceof Map) {
            Map bodyMap = (Map)body;
			
            // Do we have hits and a total
            if (bodyMap.hits?.total?.value != null) {
                // Allocate a new result object
                openRsClusterResult = new OpenRsClusterResult();
                openRsClusterResult.totalHits = bodyMap.hits.total.value;

                if (bodyMap.hits?.hits != null) {
                    try {
                        // Loop through the actual hits, converting them to a cluster record
                        ObjectMapper objectMapper = new ObjectMapper();
                        bodyMap.hits.hits.each { Map record ->
                            // Convert it to our internal record
                            OpenRsClusterRecord openRsClusterRecord = objectMapper.convertValue(record._source, OpenRsClusterRecord.class);
                            openRsClusterResult.records.add(openRsClusterRecord);
                        }
                    } catch (Exception e) {
                        log.error("Exception thrown while processing the records from the Open RS shared index", e);
                    }
                }
            } else {
                log.error("Map is not of the correct format for a response from the Open Rs shared index: " + bodyMap.toString());
            }
        } else {
            log.error("Not a valid response from the Open Rs shared index: " + body.toString());
        }

        // Return the result to the caller
        return(openRsClusterResult);
    }
}
