package com.k_int.okapi;

import com.k_int.institution.results.FolioUserLabelValue;

import groovy.util.logging.Slf4j;

@Slf4j
public class OkapiUserService {

    /** The url to the permissions module */
    private static final String URL_USERS = "/bl-users";

    /** The number of records to request at a time */
    private static final int RECORDS_TO_REQUEST = 10;

    /** The query query prefix */
    private static final String QUERY_PREFIX = "(";

    /** The keyword for ORing the ids together */
    private static final String QUERY_OR = " or ";

    /** The query query prefix */
    private static final String QUERY_POSTFIX = ") and active=true";

    /** The id field we want to use to lookup the users */
    private static final String FIELD_ID = "id";

    // injected by spring
    @Autowired
    OkapiClient okapiClient;

    /**
     * Returns The labels for the specified users
     * @param userIds The user that the labels are being requested for
     * @return A list of FolioUserLabel records that map the userid to a label
     */
    public List<FolioUserLabelValue> getLabelsForUserids(List<String> userIds) {
        // The user ids that have been found
        List<FolioUserLabelValue> userIdLabels = new ArrayList<FolioUserLabelValue>();

        try {
            String query = null;;
            int noOfUserIdsInQuery = 0;

            // Loop through all the user ids
            userIds.each{ String userId ->
                if (query == null) {
                    query = QUERY_PREFIX;
                } else {
                    query += QUERY_OR;
                }

                // Add the userid to our query
                query += FIELD_ID + "=" + userId;

                // Increment the count in the query
                noOfUserIdsInQuery++;

                if (noOfUserIdsInQuery == RECORDS_TO_REQUEST) {
                    // Terminate the query
                    query += QUERY_POSTFIX;

                    // Perform the query and process the result
                    performAndProcess(query, userIdLabels);

                    // Reset the query and the count
                    noOfUserIdsInQuery = 0;
                    query = null;
                }
            }

            // Do we have some unprocess userids
            if (noOfUserIdsInQuery > 0) {
                // Terminate the query
                query += QUERY_POSTFIX;

                // Perform the query and process the result
                performAndProcess(query, userIdLabels);
            }
        } catch(Exception e) {
            log.error("Exception thrown getting users details from the userid", e);
        }

        // Return the user ids to the caller, sorted by the label
        return(userIdLabels.sort { FolioUserLabelValue folioUserLabelValue -> folioUserLabelValue.label});
    }

    /**
     * Perform a search against the folio users module to obtain a label for the userid
     * @param query The query to be performed
     * @param userIdLabels The list that contains the results
     */
    private void performAndProcess(String query, List<FolioUserLabelValue> userIdLabels) {
        Map queryParameters = [
            query: query,
            limit: RECORDS_TO_REQUEST,
            length: RECORDS_TO_REQUEST // This in theory has been superceeded by limit, so in theory not required
        ];

        // Execute the query
        def userResult = okapiClient.getSync(URL_USERS, queryParameters);

        // We are expecting a map back
        if (userResult instanceof Map) {
            Map mapUserResult = (Map)userResult;
            if (mapUserResult.compositeUsers) {
                // A good start, hopefully this is a list
                mapUserResult.compositeUsers.each { user ->
                    if (user.users) {
                        if (user.users.id) {
                            String label;
                            if (user.users.lastName) {
                                label = user.users.lastName;
                                if (user.users.firstName) {
                                    label += ", " + user.users.firstName;
                                }
                            } else if (user.users.firstName) {
                                label = user.users.firstName;
                            } else if (user.users.username) {
                                label = user.users.username;
                            } else {
                                label = user.users.id;
                            }

                            // Now add it to the userIdLabels
                            userIdLabels.add(new FolioUserLabelValue(user.users.id, label));
                        }
                    } else {
                        log.error("No users object in the result from " + URL_USERS + ": " + userResult.toString());
                    }
                }
            } else {
                log.error("No compositeUsers in the result from " + URL_USERS + ": " + userResult.toString());
            }
        } else {
            log.error("Unexpected result from " + URL_USERS + ": " + userResult.toString());
        }
    }
}
