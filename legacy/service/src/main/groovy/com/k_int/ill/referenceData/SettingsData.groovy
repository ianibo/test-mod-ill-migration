package com.k_int.ill.referenceData;

import com.k_int.ill.IllActionService;
import com.k_int.ill.ReferenceDataService;
import com.k_int.ill.statemodel.StateModel;
import com.k_int.institution.Institution
import com.k_int.settings.InstitutionSettingsService
import com.k_int.settings.SystemSettingsService
import com.k_int.web.toolkit.files.FileUploadService;

import groovy.util.logging.Slf4j;

/**
 * Class that contains the Settings and Reference Data values required by ILL
 * @author Chas
 *
 */
@Slf4j
public class SettingsData {

    public static final String SETTING_TYPE_PASSWORD = 'Password';
    public static final String SETTING_TYPE_REF_DATA = 'Refdata';
    public static final String SETTING_TYPE_STRING   = 'String';
    public static final String SETTING_TYPE_TEMPLATE = 'Template';

    public static final String SECTION_AUTO_RESPONDER         = 'autoResponder';
    public static final String SECTION_CHAT                   = 'chat';
    public static final String SECTION_DIRECTORY              = 'directory';
    public static final String SECTION_FILE_STORAGE           = 'fileStorage';
    public static final String SECTION_GENERAL                = 'general';
    public static final String SECTION_HOST_LMS_INTEGRATION   = 'hostLMSIntegration';
    public static final String SECTION_INSTITUTION            = 'institution';
    public static final String SECTION_LOCAL_NCIP             = 'localNCIP';
    public static final String SECTION_LOGGING                = 'logging';
    public static final String SECTION_NETWORK                = 'network';
    public static final String SECTION_PATRON_STORE           = 'patronStore';
    public static final String SECTION_PULLSLIP_CONFIGURATION = 'pullslipConfiguration';
    public static final String SECTION_PULLSLIP_TEMPLATE      = 'pullslipTemplateConfig';
    public static final String SECTION_REQUESTS               = 'requests';
    public static final String SECTION_ROUTING                = 'Routing';
    public static final String SECTION_SHARED_INDEX           = 'sharedIndex';
    public static final String SECTION_STATE_ACTION_CONFIG    = 'state_action_config';
    public static final String SECTION_STATE_MODEL            = 'state_model';
    public static final String SECTION_WMS                    = 'wmsSettings';
    public static final String SECTION_VOYAGER                = 'voyagerSettings';
    public static final String SECTION_Z3950                  = 'z3950';

    // Settings for the z3950 section
    public static final String SETTING_Z3950_SERVER_ADDRESS = 'z3950_server_address';
    public static final String SETTING_Z3950_PROXY_ADDRESS = 'z3950_proxy_address';

    // Settings for the localNCIP section
    public static final String SETTING_NCIP_APP_PROFILE                = 'ncip_app_profile';
    public static final String SETTING_NCIP_FROM_AGENCY                = 'ncip_from_agency';
    public static final String SETTING_NCIP_FROM_AGENCY_AUTHENTICATION = 'ncip_from_agency_authentication';
    public static final String SETTING_NCIP_SERVER_ADDRESS             = 'ncip_server_address';
    public static final String SETTING_NCIP_TO_AGENCY                  = 'ncip_to_agency';
    public static final String SETTING_NCIP_USE_DUE_DATE               = 'ncip_use_due_date';
    public static final String SETTING_NCIP_DUE_DATE_FORMAT            = 'ncip_due_date_format';

    // Settings for the wmsSettings section
    public static final String SETTING_WMS_API_KEY                = 'wms_api_key';
    public static final String SETTING_WMS_API_SECRET             = 'wms_api_secret';
    public static final String SETTING_WMS_CONNECTOR_ADDRESS      = 'wms_connector_address';
    public static final String SETTING_WMS_CONNECTOR_PASSWORD     = 'wms_connector_password';
    public static final String SETTING_WMS_CONNECTOR_USERNAME     = 'wms_connector_username';
    public static final String SETTING_WMS_LOOKUP_PATRON_ENDPOINT = 'wms_lookup_patron_endpoint';
    public static final String SETTING_WMS_REGISTRY_ID            = 'wms_registry_id';

    // Settings for the voyagerSettings section
    public static final String SETTING_VOYAGER_ITEM_API_ADDRESS   = 'voyager_item_api_address';

    // Settings for the pull slip configuration section
    public static final String SETTING_PULL_SLIP_LOGO_ID          = 'pull_slip_logo_id';
    public static final String SETTING_PULL_SLIP_MAX_ITEMS        = 'pull_slip_max_items';
    public static final String SETTING_PULL_SLIP_MAX_ITEMS_MANUAL = 'pull_slip_max_items_manual';
    public static final String SETTING_PULL_SLIP_REPORT_ID        = 'pull_slip_report_id';

    // Settings for the pullslipTemplateConfig section
    public static final String SETTING_PULL_SLIP_TEMPLATE_ID = 'pull_slip_template_id';

    // Settings for the hostLMSIntegration section
    public static final String SETTING_ACCEPT_ITEM          = 'accept_item';
    public static final String SETTING_BORROWER_CHECK       = 'borrower_check';
    public static final String SETTING_CHECK_IN_ITEM        = 'check_in_item';
    public static final String SETTING_CHECK_OUT_ITEM       = 'check_out_item';
    public static final String SETTING_CHECK_IN_ON_RETURN   = 'check_in_on_return';
    public static final String SETTING_HOST_LMS_INTEGRATION = 'host_lms_integration';

    // Settings for the requests section
    public static final String SETTING_DEFAULT_INSTITUTIONAL_PATRON_ID = 'default_institutional_patron_id';
    public static final String SETTING_DEFAULT_REQUEST_SYMBOL          = 'default_request_symbol';
    public static final String SETTING_LAST_RESORT_LENDERS             = 'last_resort_lenders';
    public static final String SETTING_REQUEST_ID_PREFIX               = 'request_id_prefix';

    // Settings for the sharedIndex section
    public static final String SETTING_SHARED_INDEX_BASE_URL    = 'shared_index_base_url';
    public static final String SETTING_SHARED_INDEX_INTEGRATION = 'shared_index_integration';
    public static final String SETTING_SHARED_INDEX_PASS        = 'shared_index_pass';
    public static final String SETTING_SHARED_INDEX_TENANT      = 'shared_index_tenant';
    public static final String SETTING_SHARED_INDEX_USER        = 'shared_index_user';

    public static final String SETTING_SHARED_INDEX_AVAILABILITY_AUTHORITY = 'shared_index_availability_authority';
    public static final String SETTING_SHARED_INDEX_AVAILABILITY_URL       = 'shared_index_availability_url';
    public static final String SETTING_SHARED_INDEX_TOKEN_CLIENT_ID        = 'shared_index_token_client_id';
    public static final String SETTING_SHARED_INDEX_TOKEN_PASS             = 'shared_index_token_pass';
    public static final String SETTING_SHARED_INDEX_TOKEN_SECRET           = 'shared_index_token_secret';
    public static final String SETTING_SHARED_INDEX_TOKEN_URL              = 'shared_index_token_url';
    public static final String SETTING_SHARED_INDEX_TOKEN_USER             = 'shared_index_token_user';

    // Settings for the patronStore section
    public static final String SETTING_PATRON_STORE          = 'patron_store';
    public static final String SETTING_PATRON_STORE_BASE_URL = 'patron_store_base_url';
    public static final String SETTING_PATRON_STORE_GROUP    = 'patron_store_group';
    public static final String SETTING_PATRON_STORE_PASS     = 'patron_store_pass';
    public static final String SETTING_PATRON_STORE_TENANT   = 'patron_store_tenant';
    public static final String SETTING_PATRON_STORE_USER     = 'patron_store_user';

    // Settings for the autoResponder section
    public static final String SETTING_AUTO_RESPONDER_CANCEL           = 'auto_responder_cancel';
    public static final String SETTING_AUTO_RESPONDER_LOCAL            = 'auto_responder_local';
    public static final String SETTING_AUTO_RESPONDER_STATUS           = 'auto_responder_status';
    public static final String SETTING_ENABLE_LOCAL_AVAILABILITY_CHECK = 'enable_local_availability_check';
    public static final String SETTING_STALE_REQUEST_2_DAYS            = 'stale_request_2_days';
    public static final String SETTING_STALE_REQUEST_1_ENABLED         = 'stale_request_1_enabled';
    public static final String SETTING_STALE_REQUEST_3_EXCLUDE_WEEKEND = 'stale_request_3_exclude_weekend';

    // Settings for the chat section
    public static final String SETTING_CHAT_AUTO_READ = 'chat_auto_read';

    // Settings for the Routing section
    public static final String SETTING_ROUTING_ADAPTER = 'routing_adapter';
    public static final String SETTING_STATIC_ROUTES   = 'static_routes';

    // State/Action configuration settings
    public static final String SETTING_COMBINE_FILL_AND_SHIP                      = 'combine_fill_and_ship';
    public static final String SETTING_COMBINE_RETURNED_BY_PATRON_AND_RETURN_SHIP = 'combine_returned_by_patron_and_return_ship';

    // Network configuration settings
    public static final String SETTING_NETWORK_MAXIMUM_SEND_ATEMPTS = 'network_maximum_send_attempts';
    public static final String SETTING_NETWORK_RETRY_PERIOD         = 'network_retry_period';
    public static final String SETTING_NETWORK_TIMEOUT_PERIOD       = 'network_timeout_period';

    // State model configuration settings
    public static final String SETTING_STATE_MODEL_REQUESTER_COPY = 'state_model_requester_copy';
    public static final String SETTING_STATE_MODEL_REQUESTER      = 'state_model_requester';
    public static final String SETTING_STATE_MODEL_REQUESTER_CDL  = 'state_model_requester_cdl';
    public static final String SETTING_STATE_MODEL_RESPONDER_COPY = 'state_model_responder_copy';
    public static final String SETTING_STATE_MODEL_RESPONDER      = 'state_model_responder';
    public static final String SETTING_STATE_MODEL_RESPONDER_CDL  = 'state_model_responder_cdl';

    public static final String SETTING_FILE_STORAGE_ENGINE           = 'storageEngine';
    public static final String SETTING_FILE_STORAGE_S3_ENDPOINT      = 'S3Endpoint';
    public static final String SETTING_FILE_STORAGE_S3_ACCESS_KEY    = 'S3AccessKey';
    public static final String SETTING_FILE_STORAGE_S3_SECRET_KEY    = 'S3SecretKey';
    public static final String SETTING_FILE_STORAGE_S3_BUCKET_NAME   = 'S3BucketName';
    public static final String SETTING_FILE_STORAGE_S3_BUCKET_REGION = 'S3BucketRegion';
    public static final String SETTING_FILE_STORAGE_S3_OBJECT_PREFIX = 'S3ObjectPrefix';

	public static final String SETTING_GENERAL_EMAIL_RESPONSE_URL = 'generalEmailResponseUrl';

    public static final String SETTING_LOGGING_ISO18626             = 'loggingISO18626';
    public static final String SETTING_LOGGING_ISO18626_DAYS        = 'loggingISO18626Days';
    public static final String SETTING_LOGGING_NCIP                 = 'loggingNCIP';
    public static final String SETTING_LOGGING_NCIP_DAYS            = 'loggingNCIPDays';
    public static final String SETTING_LOGGING_Z3950_REQUESTER      = 'loggingZ3950Requester';
    public static final String SETTING_LOGGING_Z3950_REQUESTER_DAYS = 'loggingZ3950RequesterDays';
    public static final String SETTING_LOGGING_Z3950_RESPONDER      = 'loggingZ3950Responder';
    public static final String SETTING_LOGGING_Z3950_RESPONDER_DAYS = 'loggingZ3950ResponderDays';

    // Settings for directory
    public static final String SETTING_DIRECTORY_ANNOUCE_URL = 'directory_announce_url';
    public static final String SETTING_DIRECTORY_SYNC_USER_AFFILIATION = 'directory_sync_user_affiliation';
    public static final String SETTING_DIRECTORY_SYNC_USER_GROUPS = 'directory_sync_user_groups';

    // Settings for the institution section
    public static final String SETTING_INSTITUTION_MULTIPLE_ENABLED = 'institution_multiple_enabled';

    public static void loadAll() {
		(new SettingsData()).load();
    }

    public static void load(Institution institution) {
        (new SettingsData()).loadInstitutionSettings(institution);
    }

    /**
     * Loads the settings into the database
     */
    public void load() {
        log.info('Adding settings to the database');

        // First the system settings
        loadSystemSettings();

        // Now for the institution settings, which needs to happen for each institution
        Institution.findAll().each { Institution institution ->
            loadInstitutionSettings(institution);
        }
    }

    public void loadSystemSettings() {
        log.info('Adding system settings');

        try {
            // Get hold of the system settings service
            SystemSettingsService systemSettingsService = SystemSettingsService.getInstance();

            // We are not a service, so we need to look it up
            ReferenceDataService referenceDataService = ReferenceDataService.getInstance();

			// Delete redundant settings first
			systemSettingsService.delete("generalProxyUrl");
			
            // The shared index settings live under the system configuration
            systemSettingsService.ensureSetting(
                SETTING_SHARED_INDEX_INTEGRATION,
                SECTION_SHARED_INDEX,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_SHARED_INDEX_ADAPTER,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_SHARED_INDEX_ADAPTER, RefdataValueData.SHARED_INDEX_ADAPTER_FOLIO).value
            );
            systemSettingsService.ensureSetting(
                SETTING_SHARED_INDEX_BASE_URL,
                SECTION_SHARED_INDEX,
                SETTING_TYPE_STRING,
                null,
                'http://shared-index.reshare-dev.indexdata.com:9130'
            );
            systemSettingsService.ensureSetting(
                SETTING_SHARED_INDEX_USER,
                SECTION_SHARED_INDEX,
                SETTING_TYPE_STRING
            );
            systemSettingsService.ensureSetting(
                SETTING_SHARED_INDEX_PASS,
                SECTION_SHARED_INDEX,
                SETTING_TYPE_PASSWORD
            );
            systemSettingsService.ensureSetting(
                SETTING_SHARED_INDEX_TENANT,
                SECTION_SHARED_INDEX,
                SETTING_TYPE_STRING,
                null,
                'diku'
            );

            // The shared index may have a separate url for the availability
            systemSettingsService.ensureSetting(
                SETTING_SHARED_INDEX_AVAILABILITY_AUTHORITY,
                SECTION_SHARED_INDEX,
                SETTING_TYPE_STRING
            );
            systemSettingsService.ensureSetting(
                SETTING_SHARED_INDEX_AVAILABILITY_URL,
                SECTION_SHARED_INDEX,
                SETTING_TYPE_STRING
            );
            systemSettingsService.ensureSetting(
                SETTING_SHARED_INDEX_TOKEN_CLIENT_ID,
                SECTION_SHARED_INDEX,
                SETTING_TYPE_STRING
            );
            systemSettingsService.ensureSetting(
                SETTING_SHARED_INDEX_TOKEN_PASS,
                SECTION_SHARED_INDEX,
                SETTING_TYPE_PASSWORD
            );
            systemSettingsService.ensureSetting(
                SETTING_SHARED_INDEX_TOKEN_SECRET,
                SECTION_SHARED_INDEX,
                SETTING_TYPE_PASSWORD
            );
            systemSettingsService.ensureSetting(
                SETTING_SHARED_INDEX_TOKEN_URL,
                SECTION_SHARED_INDEX,
                SETTING_TYPE_STRING
            );
            systemSettingsService.ensureSetting(
                SETTING_SHARED_INDEX_TOKEN_USER,
                SECTION_SHARED_INDEX,
                SETTING_TYPE_STRING
            );

            // Settings for directory are at the system level
            systemSettingsService.ensureSetting(
                SETTING_DIRECTORY_ANNOUCE_URL,
                SECTION_DIRECTORY,
                SETTING_TYPE_STRING
            );
            systemSettingsService.ensureSetting(
                SETTING_DIRECTORY_SYNC_USER_AFFILIATION,
                SECTION_DIRECTORY,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_YES_NO,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_NO).value
            );
            systemSettingsService.ensureSetting(
                SETTING_DIRECTORY_SYNC_USER_GROUPS,
                SECTION_DIRECTORY,
                SETTING_TYPE_STRING,
                null,
                'Admin,User'
            );

            systemSettingsService.ensureSetting(
                SETTING_GENERAL_EMAIL_RESPONSE_URL,
                SECTION_GENERAL,
                SETTING_TYPE_STRING
            );

            // Settings for institution, these are at the system level
            systemSettingsService.ensureSetting(
                SETTING_INSTITUTION_MULTIPLE_ENABLED,
                SECTION_INSTITUTION,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_YES_NO,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_NO).value
            );

			// The z3850 proxy address should be the same for all institutions
            systemSettingsService.ensureSetting(
                SETTING_Z3950_PROXY_ADDRESS,
                SECTION_Z3950,
                SETTING_TYPE_STRING,
                null,
                'http://reshare-mp.folio-dev.indexdata.com:9000'
            );
        } catch (Exception e) {
            log.error('Exception thrown while loading system settings', e);
        }
    }

    public void loadInstitutionSettings(Institution institution) {
        try {
            // These need to be added for each institution
            log.info('Adding settings to the database for institution: ' + institution.name);

            // Get hold of the institution settings service
            InstitutionSettingsService institutionSettingsService = InstitutionSettingsService.getInstance();

            // We are not a service, so we need to look it up
            ReferenceDataService referenceDataService = ReferenceDataService.getInstance();

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_Z3950_SERVER_ADDRESS,
                SECTION_Z3950,
                SETTING_TYPE_STRING
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_NCIP_SERVER_ADDRESS,
                SECTION_LOCAL_NCIP,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_NCIP_FROM_AGENCY,
                SECTION_LOCAL_NCIP,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_NCIP_FROM_AGENCY_AUTHENTICATION,
                SECTION_LOCAL_NCIP,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_NCIP_TO_AGENCY,
                SECTION_LOCAL_NCIP,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_NCIP_APP_PROFILE,
                SECTION_LOCAL_NCIP,
                SETTING_TYPE_STRING,
                null,
                'EZBORROW'
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_NCIP_USE_DUE_DATE,
                SECTION_LOCAL_NCIP,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_NCIP_DUE_DATE,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_NCIP_DUE_DATE, RefdataValueData.NCIP_DUE_DATE_ON).value
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_NCIP_DUE_DATE_FORMAT,
                SECTION_LOCAL_NCIP,
                SETTING_TYPE_STRING,
                null,
                IllActionService.DEFAULT_DATE_FORMAT
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_WMS_API_KEY,
                SECTION_WMS,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_WMS_API_SECRET,
                SECTION_WMS,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_WMS_LOOKUP_PATRON_ENDPOINT,
                SECTION_WMS,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_WMS_REGISTRY_ID,
                SECTION_WMS,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_WMS_CONNECTOR_ADDRESS,
                SECTION_WMS,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_WMS_CONNECTOR_USERNAME,
                SECTION_WMS,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_WMS_CONNECTOR_PASSWORD,
                SECTION_WMS,
                SETTING_TYPE_STRING
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_VOYAGER_ITEM_API_ADDRESS,
                SECTION_VOYAGER,
                SETTING_TYPE_STRING
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_PULL_SLIP_TEMPLATE_ID,
                SECTION_PULLSLIP_TEMPLATE,
                SETTING_TYPE_TEMPLATE,
                RefdataValueData.VOCABULARY_PULL_SLIP_TEMPLATE
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_PULL_SLIP_REPORT_ID,
                SECTION_PULLSLIP_CONFIGURATION,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_PULL_SLIP_LOGO_ID,
                SECTION_PULLSLIP_CONFIGURATION,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_PULL_SLIP_MAX_ITEMS,
                SECTION_PULLSLIP_CONFIGURATION,
                SETTING_TYPE_STRING,
                null,
                "100",
                null,
                true
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_PULL_SLIP_MAX_ITEMS_MANUAL,
                SECTION_PULLSLIP_CONFIGURATION,
                SETTING_TYPE_STRING,
                null,
                "100",
                null,
                true
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_BORROWER_CHECK,
                SECTION_HOST_LMS_INTEGRATION,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_BORROWER_CHECK_METHOD
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_CHECK_OUT_ITEM,
                SECTION_HOST_LMS_INTEGRATION,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_CHECK_OUT_METHOD
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_CHECK_IN_ITEM,
                SECTION_HOST_LMS_INTEGRATION,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_CHECK_IN_METHOD
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_CHECK_IN_ON_RETURN,
                SECTION_HOST_LMS_INTEGRATION,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_CHECK_IN_ON_RETURN,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_CHECK_IN_ON_RETURN, RefdataValueData.CHECK_IN_ON_RETURN_OFF).value
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_ACCEPT_ITEM,
                SECTION_HOST_LMS_INTEGRATION,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_ACCEPT_ITEM_METHOD
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_HOST_LMS_INTEGRATION,
                SECTION_HOST_LMS_INTEGRATION,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_HOST_LMS_INTEGRATION_ADAPTER,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_HOST_LMS_INTEGRATION_ADAPTER, RefdataValueData.HOST_LMS_INTEGRATION_ADAPTER_MANUAL).value
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_REQUEST_ID_PREFIX,
                SECTION_REQUESTS,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_DEFAULT_REQUEST_SYMBOL,
                SECTION_REQUESTS,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_LAST_RESORT_LENDERS,
                SECTION_REQUESTS,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_DEFAULT_INSTITUTIONAL_PATRON_ID,
                SECTION_REQUESTS,
                SETTING_TYPE_STRING
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_PATRON_STORE_BASE_URL,
                SECTION_PATRON_STORE,
                SETTING_TYPE_STRING,
                null,
                'http://127.0.0.1:9130'
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_PATRON_STORE_TENANT,
                SECTION_PATRON_STORE,
                SETTING_TYPE_STRING,
                null,
                'diku'
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_PATRON_STORE_USER,
                SECTION_PATRON_STORE,
                SETTING_TYPE_STRING,
                null,
                'diku_admin'
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_PATRON_STORE_PASS,
                SECTION_PATRON_STORE,
                SETTING_TYPE_PASSWORD
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_PATRON_STORE_GROUP,
                SECTION_PATRON_STORE,
                SETTING_TYPE_STRING,
                null,
                'bdc2b6d4-5ceb-4a12-ab46-249b9a68473e'
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_PATRON_STORE,
                SECTION_PATRON_STORE,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_PATRON_STORE_ADAPTER,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_PATRON_STORE_ADAPTER, RefdataValueData.PATRON_STORE_ADAPTER_MANUAL).value
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_AUTO_RESPONDER_STATUS,
                SECTION_AUTO_RESPONDER,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_AUTO_RESPONDER,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_AUTO_RESPONDER, RefdataValueData.AUTO_RESPONDER_ON_WILL_SUPPLY_CANNOT_SUPPLY).value
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_AUTO_RESPONDER_CANCEL,
                SECTION_AUTO_RESPONDER,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_AUTO_RESPONDER_CANCEL,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_AUTO_RESPONDER_CANCEL, RefdataValueData.AUTO_RESPONDER_CANCEL_ON).value
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_AUTO_RESPONDER_LOCAL,
                SECTION_AUTO_RESPONDER,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_AUTO_RESPONDER_LOCAL,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_AUTO_RESPONDER_LOCAL, RefdataValueData.AUTO_RESPONDER_LOCAL_OFF).value
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_ENABLE_LOCAL_AVAILABILITY_CHECK,
                SECTION_AUTO_RESPONDER,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_YES_NO,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_YES).value
            );

            // Setup the Stale request settings (added the numbers so they appear in the order I want them in
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_STALE_REQUEST_1_ENABLED,
                SECTION_AUTO_RESPONDER,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_YES_NO,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_NO).value
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_STALE_REQUEST_2_DAYS,
                SECTION_AUTO_RESPONDER,
                SETTING_TYPE_STRING,
                null,
                '3'
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_STALE_REQUEST_3_EXCLUDE_WEEKEND,
                SECTION_AUTO_RESPONDER,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_YES_NO,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_YES).value
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_CHAT_AUTO_READ,
                SECTION_CHAT,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_CHAT_AUTO_READ,
                'on'
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_ROUTING_ADAPTER,
                SECTION_ROUTING,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_REQUEST_ROUTING_ADAPTER,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_REQUEST_ROUTING_ADAPTER, RefdataValueData.REQUEST_ROUTING_ADAPTER_SHARED_INDEX).value
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_STATIC_ROUTES,
                SECTION_ROUTING,
                SETTING_TYPE_STRING
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_COMBINE_FILL_AND_SHIP,
                SECTION_STATE_ACTION_CONFIG,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_YES_NO,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_NO).value
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_COMBINE_RETURNED_BY_PATRON_AND_RETURN_SHIP,
                SECTION_STATE_ACTION_CONFIG, SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_YES_NO,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_NO).value
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_NETWORK_MAXIMUM_SEND_ATEMPTS,
                SECTION_NETWORK,
                SETTING_TYPE_STRING,
                null,
                '0'
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_NETWORK_RETRY_PERIOD,
                SECTION_NETWORK,
                SETTING_TYPE_STRING,
                null,
                '10'
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_NETWORK_TIMEOUT_PERIOD,
                SECTION_NETWORK,
                SETTING_TYPE_STRING,
                null,
                '30'
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_STATE_MODEL_REQUESTER_COPY,
                SECTION_STATE_MODEL,
                SETTING_TYPE_STRING,
                null,
                StateModel.MODEL_REQUESTER_COPY,
                null,
                true
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_STATE_MODEL_REQUESTER_CDL,
                SECTION_STATE_MODEL,
                SETTING_TYPE_STRING,
                null,
                StateModel.MODEL_DIGITAL_RETURNABLE_REQUESTER,
                null,
                true
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_STATE_MODEL_REQUESTER,
                SECTION_STATE_MODEL,
                SETTING_TYPE_STRING,
                null,
                StateModel.MODEL_REQUESTER,
                null,
                true
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_STATE_MODEL_RESPONDER_COPY,
                SECTION_STATE_MODEL,
                SETTING_TYPE_STRING,
                null,
                StateModel.MODEL_RESPONDER_COPY,
                null,
                true
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_STATE_MODEL_RESPONDER_CDL,
                SECTION_STATE_MODEL,
                SETTING_TYPE_STRING,
                null,
                StateModel.MODEL_CDL_RESPONDER,
                null,
                true
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_STATE_MODEL_RESPONDER,
                SECTION_STATE_MODEL,
                SETTING_TYPE_STRING,
                null,
                StateModel.MODEL_RESPONDER,
                null,
                true
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_FILE_STORAGE_ENGINE,
                SECTION_FILE_STORAGE,
                SETTING_TYPE_STRING,
                null,
                FileUploadService.S3_STORAGE_ENGINE
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_FILE_STORAGE_S3_ENDPOINT,
                SECTION_FILE_STORAGE,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_FILE_STORAGE_S3_ACCESS_KEY,
                SECTION_FILE_STORAGE,
                SETTING_TYPE_PASSWORD
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_FILE_STORAGE_S3_SECRET_KEY,
                SECTION_FILE_STORAGE,
                SETTING_TYPE_PASSWORD
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_FILE_STORAGE_S3_BUCKET_NAME,
                SECTION_FILE_STORAGE,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_FILE_STORAGE_S3_BUCKET_REGION,
                SECTION_FILE_STORAGE,
                SETTING_TYPE_STRING
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_FILE_STORAGE_S3_OBJECT_PREFIX,
                SECTION_FILE_STORAGE,
                SETTING_TYPE_STRING,
                null,
                'ill-'
            );

            institutionSettingsService.ensureSetting(
                institution,
                SETTING_LOGGING_ISO18626, SECTION_LOGGING,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_YES_NO,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_NO).value
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_LOGGING_ISO18626_DAYS,
                SECTION_LOGGING,
                SETTING_TYPE_STRING,
                null,
                '30'
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_LOGGING_NCIP,
                SECTION_LOGGING,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_YES_NO,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_NO).value
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_LOGGING_NCIP_DAYS,
                SECTION_LOGGING,
                SETTING_TYPE_STRING,
                null,
                '30'
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_LOGGING_Z3950_REQUESTER,
                SECTION_LOGGING,
                SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_YES_NO,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_NO).value
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_LOGGING_Z3950_REQUESTER_DAYS,
                SECTION_LOGGING,
                SETTING_TYPE_STRING,
                null,
                '30'
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_LOGGING_Z3950_RESPONDER,
                SECTION_LOGGING, SETTING_TYPE_REF_DATA,
                RefdataValueData.VOCABULARY_YES_NO,
                null,
                referenceDataService.lookup(RefdataValueData.VOCABULARY_YES_NO, RefdataValueData.YES_NO_NO).value
            );
            institutionSettingsService.ensureSetting(
                institution,
                SETTING_LOGGING_Z3950_RESPONDER_DAYS,
                SECTION_LOGGING,
                SETTING_TYPE_STRING,
                null,
                '30'
            );

        } catch (Exception e) {
            log.error('Exception thrown while loading settings for institution ' + institution.name, e);
        }
    }
}
