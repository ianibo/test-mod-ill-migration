package com.k_int;

import com.k_int.institution.Institution;
import com.k_int.institution.InstitutionService;
import com.k_int.okapi.OkapiTenantAwareController;

import services.k_int.tests.ExcludeFromGeneratedCoverageReport

/**
 * This controller was written to take into account the functionality required for
 * multiple institutions within one tenant
 * @author Chas
 *
 * @param <T> The domain class of record that we are offering the end points for
 */
@ExcludeFromGeneratedCoverageReport
public class OkapiTenantAwareInstitutionController<T> extends OkapiTenantAwareController<T>  {

    private static final String GRAILS_ANONYMOUS_USER = "__grails.anonymous.user__";
    private static final String HEADER_OKAPI_USER_ID  = "X-Okapi-User-Id";

    // A cache that holds whether domain classes contain an institution property
    private static final Map hasInstitutionPropertyCache = [ : ];

    // Required to access the institution functionality
    InstitutionService institutionService;

    public OkapiTenantAwareInstitutionController(Class<T> resource) {
        this(resource, false);
    }

    public OkapiTenantAwareInstitutionController(Class<T> resource, boolean readOnly) {
        super(resource, readOnly);
    }

    /**
     *
     * @return
     */
    protected boolean multipleInstitutionsEnabled() {
        return(institutionService.multipleInstitutionsEnabled());
    }

    /**
     * Obtains the user identifier of the user making the call
     * @return The identifier of the user
     */
    protected String getUserId() {
        // Obtain the user id from the user details
        String userId = getPatron()?.getUsername();

        // If the user id is the grails anonymous user, then we need to look elsewhere for it
        if (GRAILS_ANONYMOUS_USER.equals(userId)) {
            // Set it to the X-Okap-User-Id header instead
            userId = request.getHeader(HEADER_OKAPI_USER_ID);
        }

        // Finally return the found user id
        return(userId);
    }

    /**
     * Obtains the institution the user is currently managing
     * @return The institution that the user is currently managing
     */
    protected Institution getInstitution() {
        Institution institution = null;

        // Obtain the user id
        String userId = getUserId()

        // Do we have a user id
        if (userId == null) {
            // We do not, so use the default institution
            institution = institutionService.getDefaultInstitution();
        } else {
            // We do, so let the service work out the institution to use
            institution = institutionService.getInstitution(userId);
        }
        return(institution);
    }

    /**
     * Retrieves the institutionId for the user
     * @return The institution id for the user or null if there is not one
     */
    protected String getInstitutionId() {
        String institutionId = null;

        // Let the service do the work for us
        Institution institution = getInstitution();
        if (institution != null) {
            institutionId = institution.id;
        }
        return(institutionId);
    }

    /**
     * Retrieves the institution that is for the symbol specified in the parameters as symbol
     * @return The institution that is appropriate for the parameter symbol
     */
    protected Institution getInstitutionForSymbol() {
        return(getInstitutionForSymbol(params.symbo));
    }

    /**
     * Retrieves the institution that for the supplied symbol
     * @param symbol The symbol to look up the institution
     * @return The institution that is appropriate for the supplied symbol
     */
    protected Institution getInstitutionForSymbol(String symbol) {
        return(institutionService.getInstitutionForSymbol(symbol));
    }

    /**
     * Add a filter on institution if needed
     * @param filters The filters supplied
     * @return The filters containing the institution if needed
     */
    protected List<String> addInstitutionFilterIfNeeded(List<String> filters) {
        List<String> result = filters;

        // Are multiple institutions enabled and does the institution property exist for the domain
        if (multipleInstitutionsEnabled() && hasInstitutionProperty()) {
            String institutionId = getInstitutionId();

            // Do we have an institution id
            if (institutionId != null) {
                // It does, have we been supplied existing filters or do we need to allocate a new list
                result = new ArrayList<String>(result);

                // Add institution as a filter
                result.add("institution==" + institutionId);
            }
        }
        return(result);
    }

    /**
     * Updates the JSON object on the request with the institution for the user if there is one
     * Note: This dosn't check to see if there is one already defined it just overwrites it
     */
    protected void updateJsonWithInstitution() {
        // Have we been supplied some json
        if (request.JSON != null) {
            // Get hold of the institution for the user
            Institution institution = getInstitution();

            // If we have an institution add it to the incoming json
            if (institution != null) {
                // Modify the incoming object to include the users institution
                // Note: if they an institution field at the top level it will get overwritten
                request.JSON.put("institution", [ id: institution.id]);
            }
        }
    }

    /**
     * Checks whether the record they are actioning belongs to their institution or not
     * If multiple institutions is not set up, it is assumed it is valid
     * It is only not valid if the institution on the record does not match the users institution or no id is supplied or the record does not exist
     * @return true if the record can be treated as part of the institution
     */
    protected boolean isValidForInstitution(String action, String id, T record = null) {
        boolean isValid = true;

        // If multiple institutions are not enabled then this is always valid
        if (multipleInstitutionsEnabled()) {
            if (id) {
                // We have been supplied an id, now get hold of the institution
                Institution institution = getInstitution();

                // Did we find an institution for the user
                if (institution != null) {
                    // Does this domain contain an institution member
                    if (hasInstitutionProperty()) {
                        if (record == null) {
                            // Try and get hold of the record
                            record = queryForResource(id);
                        }

                        // We need to check that the record is for this institution
                        if (record == null) {
                            log.error("User " + getUserId() + " is trying to " + action + " a record that does not exist");
                            isValid = false;
                        } else {
                            if (!institution.id.equals(record.institution.id)) {
                                log.error("User " + getUserId() + " is trying to " + action + " a record that belongs to institution " + record.institution.name + ", users institution is " + institution.name);
                                isValid = false;
                            }
                        }
                    }
                }
            } else {
                log.error("Attempt to " + action + " a record with no id");
                isValid = false;
            }
        }

        // Return whether it is valid to be able to perform the action
        return(isValid);
    }

    /**
     * Determines whether the domain class has an institution property
     * @return true if it does otherwise false
     */
    protected boolean hasInstitutionProperty() {
        Boolean hasProperty = hasInstitutionPropertyCache[this.resource.getName()];
        if (hasProperty == null) {
            hasProperty =  new Boolean(this.resource.metaClass.properties.find { it.name == "institution" } != null);
            hasInstitutionPropertyCache[this.resource.getName()] = hasProperty;
        }
        return(hasProperty);
    }

    /**
     * Returns a list of identifiers from the list supplied that belong to the institution for the user
     * @param domainClass The domain class that the identifiers belong to
     * @param identifiers The identifiers that need checking
     * @return The list of valid identifiers
     */
    protected List validateForInstitution(Class domainClass, List identifiers) {
        // Obtain the institution identifier
        String institutionId = getInstitutionId();

        // return the list of valid identifiers
        return(institutionService.validateForInstitution(domainClass, identifiers, institutionId));
    }
}
