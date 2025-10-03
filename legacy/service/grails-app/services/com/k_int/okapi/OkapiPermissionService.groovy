package com.k_int.okapi;

import groovy.util.logging.Slf4j;

@Slf4j
public class OkapiPermissionService {

    /** The url to the permissions module */
    private static final String URL_PERMISSIONS = "/perms/users";

    /** The number of records to request at a time */
    private static final int RECORDS_TO_REQUEST = 10;

    /** The query to perform to see who has access to the UI, must have the sortby, otherwise the offset will not work correctly */
    private static final String QUERY_ACCESS_TO_UI = "permissions=module.ill-ui.enabled sortby id";

    // injected by spring
    @Autowired
    OkapiClient okapiClient;

    /**
     * Returns users that have access to the ILL UI modules
     * @return A list of user identifiers that have access
     */
    public List<String> getUsersWithAccessToUI() {
        // The user ids that have been found
        List<String> userIds = new ArrayList<String>();

        try {
            Map queryParameters = [
                query: QUERY_ACCESS_TO_UI,
                limit: RECORDS_TO_REQUEST,
                length: RECORDS_TO_REQUEST, // This in theory has been superceeded by limit, so in theory not required
                offset: 0
            ];

            // Have we obtained results from the previous call,needsto be set to true the first time through
            boolean obtainedResults = true;

            // Just keep going until we do not receive anymore users
            while (obtainedResults) {
                // Reset obtainedResults
                obtainedResults = false;

                // Send the query
                def permissionResult = okapiClient.getSync(URL_PERMISSIONS, queryParameters);

                // We are expecting a map back
                if (permissionResult instanceof Map) {
                    Map mapPermissionResult = (Map)permissionResult;
                    if (mapPermissionResult.permissionUsers) {
                        // A good start, hopefully this is a list
                        obtainedResults = (mapPermissionResult.permissionUsers.size() > 0);
                        mapPermissionResult.permissionUsers.each { permissionUser ->
                            // If we have a userid add it to the list
                            if (permissionUser.userId) {
                                userIds.add(permissionUser.userId);
                            }
                        }

                        // Increment the offset
                        queryParameters.offset = queryParameters.offset + RECORDS_TO_REQUEST;
                    }
                } else {
                    log.error("Unexpected result from " + URL_PERMISSIONS + ": " + permissionResult.toString());
                }
            }
        } catch(Exception e) {
            log.error("Exception thrown getting users who access to the UI", e);
        }

        // Return the user ids to the caller
        return(userIds);
    }
}
