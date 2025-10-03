package com.k_int.ill.logging;

import com.k_int.ill.PatronRequest;
import com.k_int.ill.ProtocolAudit;
import com.k_int.ill.ProtocolMethod;
import com.k_int.ill.ProtocolType;
import com.k_int.ill.ReferenceDataService;
import com.k_int.ill.referenceData.RefdataValueData;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.institution.Institution;
import com.k_int.settings.InstitutionSettingsService

import groovyx.net.http.URIBuilder;

/**
 * Provides the necessary methods for interfacing with the ProtocolAudit table
 * @author Chas
 *
 */
public class ProtocolAuditService {

    public static final String RECEIVED_MESSAGED = "Received message";

    private static final String OBSCURED = "xxx";
    private static final List<String> queryKeysToObscure = new ArrayList<String>(
        Arrays.asList(
            "apikey",
            "user",
            "password"
        )
    );
    private static String refDataYes = null;

    ReferenceDataService referenceDataService;
    InstitutionSettingsService institutionSettingsService;

    /**
     * Given the protocol type determines the class for recording  mechanism for
     * @param protocolType
     * @return
     */
    public IHoldingLogDetails getHoldingLogDetails(Institution institution, ProtocolType protocolType) {
        String settingKey = null;

        // We first need to lookup the settings to see if we are recording information for this protocol using IHoldingsLogDetails
        switch (protocolType) {
            case protocolType.Z3950_REQUESTER:
                settingKey = SettingsData.SETTING_LOGGING_Z3950_REQUESTER;
                break;

            case protocolType.Z3950_RESPONDER:
                settingKey = SettingsData.SETTING_LOGGING_Z3950_RESPONDER;
                break;

            default:
                break;
        }

        // Now we can allocate an appropriate object
        return(((settingKey != null) && institutionSettingsService.hasSettingValue(institution, settingKey, getRefDataYes())) ?
                new HoldingLogDetails(protocolType, ProtocolMethod.GET) : // Logging is enabled
                new DoNothingHoldingLogDetails()                          // Logging is not enabled
        );
    }

    /**
     * Gets hold of the appropriate NCIP logging object depending on whether logging is enabled or not
     * @param institution The institution the call is for
     * @return An INcipLogDetails object
     */
    public INcipLogDetails getNcipLogDetails(Institution institution) {
        // Allocate an appropriate object depending on whether auditing is enabled
        return(institutionSettingsService.hasSettingValue(institution, SettingsData.SETTING_LOGGING_NCIP, getRefDataYes()) ?
                new NcipLogDetails() :        // Logging is enabled
                new DoNothingNcipLogDetails() // Logging is not enabled
        );
    }

    /**
     * Allocates an object that implements the IIso18626LogDetails interface depending on whether auditing is enabled or not
     * @param institution The institution the call is for
     * @return An IIso18626LogDetails object
     */
    public IIso18626LogDetails getIso18626LogDetails(Institution institution) {
        // Allocate an appropriate object depending on whether auditing is enabled
        return(institutionSettingsService.hasSettingValue(institution, SettingsData.SETTING_LOGGING_ISO18626, getRefDataYes()) ?
                new Iso18626LogDetails() :        // Logging is enabled
                new DoNothingIso18626LogDetails() // Logging is not enabled
        );
    }

    /**
     * Associates the audit details with request
     * @param patronRequest The request that the audit details need to be associated with
     * @param baseAuditDetails The audit details
     */
    public void save(PatronRequest patronRequest, IBaseAuditDetails baseAuditDetails) {
        // Have we been supplied a request
        if (patronRequest != null) {
            // Do we have anything to save
            String responseBody = baseAuditDetails.getResponseBody();
            if (responseBody != null) {
                // We have some details to save
                ProtocolAudit protocolAudit = new ProtocolAudit();

                // Populate the protocol audit
                protocolAudit.protocolType = baseAuditDetails.getProtocolType();
                protocolAudit.protocolMethod = baseAuditDetails.getProtocolMethod();
                protocolAudit.url = removePrivateDataFromURI(baseAuditDetails.getURL());
                protocolAudit.requestBody = baseAuditDetails.getRequestBody();
                protocolAudit.responseStatus = baseAuditDetails.getResponseStatus();
                protocolAudit.responseBody = responseBody;
                protocolAudit.duration = baseAuditDetails.duration();
                patronRequest.addToProtocolAudit(protocolAudit);
            }
        }
    }

    /**
     * Obfuscates certain query parameters so that usernames / passwords / apikeys are not recorded
     * @param uri The uri that may need query parameters obfuscating
     * @return The uri with parameters obfuscated
     */
    private String removePrivateDataFromURI(String uri) {
        String cleanedURI = uri;

        // If it is a received message, then the url in our hand is not a valid url
        if (uri && (uri != RECEIVED_MESSAGED)) {
            // We need to manipulate the query string to remove any passwords, apikeys or secrets
            URIBuilder uriBuilder = new URIBuilder(uri);
            Map queryParameters = uriBuilder.getQuery();
            if (queryParameters) {
                queryKeysToObscure.each { String parameter ->
                    if (queryParameters[parameter]) {
                        // it is set, so reset to xxx
                        queryParameters.put(parameter, OBSCURED);
                    }
                }

                // Now replace the query parameters
                uriBuilder.setQuery(queryParameters);
            }

            // Return the actual url that we accessed
            cleanedURI = uriBuilder.toString();
        }

        return(cleanedURI);
    }

    /**
     * Looks up the value for the value of yes for the Yes / No Category
     * @return The value for for Yes
     */
    private String getRefDataYes() {
        if (refDataYes == null) {
            refDataYes = referenceDataService.lookup(RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_YES).value;
        }
        return(refDataYes);
    }
}
