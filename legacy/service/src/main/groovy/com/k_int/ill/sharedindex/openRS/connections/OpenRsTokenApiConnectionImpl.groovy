package com.k_int.ill.sharedindex.openRS.connections;

import com.k_int.ill.settings.SharedIndexSettings;;

import groovy.util.logging.Slf4j;
import groovyx.net.http.ApacheHttpBuilder;
import groovyx.net.http.FromServer;
import groovyx.net.http.HttpBuilder;

/**
 */
@Slf4j
public class OpenRsTokenApiConnectionImpl implements OpenRsTokenApiConnection {

    public String get(SharedIndexSettings sharedIndexSettings) {
        return(get(sharedIndexSettings, sharedIndexSettings.getTokenUser(), sharedIndexSettings.getTokenPassword()));
    }

    public String get(SharedIndexSettings sharedIndexSettings, String username, String password) {

        // The token they have requested
        String token = null;

        // Only try and get the token if it is configured
        String url = sharedIndexSettings.getTokenUrl();
        if (url && username && password) {

            // Post the query
            HttpBuilder httpClient = ApacheHttpBuilder.configure {

                request.uri = url;
                request.contentType = "application/x-www-form-urlencoded";
            }

            token = httpClient.post {
                // Did try and use a HttpEntity, but failed miserably, until I chanced upon somebody
                // setting the body with a map, I'm sure its documented somewhere, but my searches failed to fins it ...
                request.body = [
                    grant_type : "password",
                    client_id : sharedIndexSettings.getTokenclientId(),
                    client_secret : sharedIndexSettings.getTokenSecret(),
                    username : username,
                    password : password
                ];

                response.failure { FromServer fs ->
                    log.error("Error response from token server, status code: " + fs.getStatusCode() + ", message: " + fs.getMessage());
                    return(null);
                }

                response.success { FromServer fs, json ->
                    String respomseStatus = fs.getStatusCode().toString() + " " + fs.getMessage();
                    log.debug("Got OK response: ${fs}");
                    if (json == null) {
                        // We did not get a json response
                        log.error("Invalid response from token server, no json returned: " + fs.toString());
                        return(null);
                    } else {
                        // Pass back the token that has been returned
                        return(json.access_token);
                    }
                }
            }
        }

        // Return the token to the caller
        return(token);
    }
}
