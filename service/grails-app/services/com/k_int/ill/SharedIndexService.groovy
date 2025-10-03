package com.k_int.ill;

import com.k_int.ill.referenceData.SettingsData;
import com.k_int.settings.SystemSettingsService

import grails.core.GrailsApplication;

/**
 * Return the right SharedIndexActions for the tenant config
 *
 */
public class SharedIndexService {

    GrailsApplication grailsApplication;
    SystemSettingsService systemSettingsService;

    public SharedIndexActions getSharedIndexActionsFor(String si) {
        log.debug("SharedIndexService::getSharedIndexActionsFor(${si})");
        SharedIndexActions result = grailsApplication.mainContext."${si}SharedIndexService"

        if ( result == null && si != 'none' ) {
            log.warn("Unable to locate SharedIndexActions for ${si}. Did you fail to configure the app_setting \"shared_index_integration\". Current options are folio|none");
        }

        return result;
    }

    public SharedIndexActions getSharedIndexActions() {
        SharedIndexActions result = null;
        String v = systemSettingsService.getSettingValue(SettingsData.SETTING_SHARED_INDEX_INTEGRATION);
        log.debug("Return host si integrations for : ${v} - query application context for bean named ${v}SharedIndexService");
        result = getSharedIndexActionsFor(v);
        return result;
    }

    /**
     * Determine the page size from the passed in parameters
     * @param stringPageSize The pageSize or perPage parameter as supplied with the url
     * @param stringMax The max parameter as supplied with the url
     * @param defaultPageSize The default page size if it cannot be determined
     * @param maxPageSize The maximum page size that is allowed, if the determined value is higher, the the result will be the default page size
     * @return The determined page size
     */
    public long determinePageSize(
        String stringPageSize,
        String stringMax,
        long defaultPageSize = 10,
        long maxPageSize = 100
    ) {
        long pageSize = defaultPageSize;;

        try {
            if (stringPageSize != null) {
                pageSize = stringPageSize.toLong();
            } else if (stringMax != null) {
                pageSize = stringMax.toLong();
            }
        } catch (Exception) {
            // Ignore all conversion exceptions, as we have a sensible default
        }

        // Make sure we have a sensible value for the page size
        if ((pageSize <0) || (pageSize > maxPageSize)) {
            pageSize = defaultPageSize;
        }

        return(pageSize);
    }

    /**
     * Determine the offset from the supplied parameters
     * @param pageSize The page size that has been determined
     * @param stringPage The page parameter as supplied with the url
     * @param stringOffset The offset parameter as supplied with the url
     * @return The determined offset
     */
    public long determineStartOffset(
        long pageSize,
        String stringPage,
        String stringOffset)
    {
        long offset = 0;

        try {
            if (stringPage != null) {
                // It is not null so convert it to an integer
                long page = stringPage.toLong();

                // Ensure we have a legitimate value
                if (page < 1 ) {
                    page = 1;
                }

                // Now determine the starting offset from the page required
                offset = (page - 1) * pageSize;
            } else if (stringOffset != null) {
                // Convert the string to a Long
                offset = stringOffset.toLong();

                // Ensure we do do not have a negative offset
                if (offset < 0) {
                    offset = 0;
                }
            }
        } catch (Exception) {
            // Ignore all conversion exceptions, as we have a sensible default
        }

        // Return the determined offset to the caller
        return(offset);
    }

    private static final String EQUAL_OPERATOR = "==";
    private static final String FIELD_SEPERATOR = ", ";
    private static final String OR_OPERATOR = "\\|\\|";

    /**
     * Parses the filters parameter to determine the filters we have been passed
     * @param suppliedFilters The supplied filters
     * @return The parsed results of suppliedFilters
     */
    public Map parseFilters(String suppliedFilters) {
        Map filters = [ : ];
        String realSuppliedFilters = suppliedFilters;
        if (suppliedFilters) {
            if (realSuppliedFilters[0] == '[')  {
                realSuppliedFilters = realSuppliedFilters.substring(1, realSuppliedFilters.length()- 1);
            }

            // Probably not the best way of doing this, but I'm keeping it simple right now
            // If we were doing this probably, we should process a character at a time abd build up a node tree
            //This is not the fastest way of doing it or the most accurate, just the lazy way ...
            String[] fieldParts = realSuppliedFilters.split(FIELD_SEPERATOR);

            fieldParts.each { field ->
                String[] fieldValues = field.split(OR_OPERATOR);

                fieldValues.each { fieldValue ->
                    String[] fieldValueParts = fieldValue.split(EQUAL_OPERATOR);

                    if (fieldValueParts.length == 2) {
                        String fieldname = fieldValueParts[0];

                        // Allocate new list, if one has not already been allocated
                        if (filters[fieldname] == null) {
                            filters[fieldname] = new ArrayList<String>();
                        }

                        // Now add the value to the list
                        filters[fieldname].add(fieldValueParts[1]);
                    }
                }
            }
        }

        log.debug("Chas: parsed filters: " + filters.toString());
        return(filters);
    }
}
