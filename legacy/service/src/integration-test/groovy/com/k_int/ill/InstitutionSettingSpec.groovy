package com.k_int.ill;

import com.k_int.RestResult;
import com.k_int.TestBase;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.ill.settings.MapSettings;
import com.k_int.institution.Institution;
import com.k_int.institution.InstitutionService;
import com.k_int.institution.InstitutionSetting;
import com.k_int.settings.InstitutionSettingsService

import grails.gorm.multitenancy.Tenants;
import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.Stepwise;

@Slf4j
@Integration
@Stepwise
class InstitutionSettingSpec extends TestBase {

	private static final String FIELD_KEY = "key";
	private static final String FIELD_VALUE = "value";

	private static final String CONTEXT_SETTING_ID = "settingId";
	private static final String CONTEXT_SETTING_KEY = "settingKey";
	private static final String CONTEXT_SETTING_VALUE = "settingValue";

	private static final String APP_SETTING_SECTION = "test";

	private static final String APP_SETTING_STRING = "testString";
	private static final String APP_SETTING_INT_ONE = "testIntOne";
	private static final String APP_SETTING_INT_MINUS_ONE = "testIntMinusOne";

    InstitutionService institutionService;
	InstitutionSettingsService institutionSettingsService;

    // This method is declared in the HttpSpec
    def setupSpecWithSpring() {
        super.setupSpecWithSpring();
    }

    def setupSpec() {
    }

    def setup() {
    }

    def cleanup() {
    }

    void "Set up test tenants"(String tenantId, String name) {
        when:"We post a new tenant request to the OKAPI controller"
            boolean response = setupTenant(tenantId, name);

        then:"The response is correct"
            assert(response);

        where:
            tenantId   | name
            TENANT_ONE | TENANT_ONE
    }

    void "Create a new InstitutionSetting"(
        String tenantId,
        String section,
        String key,
        String settingType,
        String vocab,
        String defValue,
        String value
    ) {
        when:"Create a new InstitutionSetting"

            // Create the InstitutionSetting
            Map institutionSetting = [
                section : section,
                key : key,
                settingType : settingType,
                vocab : vocab,
                defValue : defValue,
                value : value
            ];

			// Lets us call the base class to post it
			RestResult restResult = createNewObject(
				tenantId,
				PATH_INSTITUTION_SETTING,
				institutionSetting,
				CONTEXT_SETTING_ID,
				FIELD_ID,
				CONTEXT_SETTING_VALUE,
				FIELD_VALUE
			);

			if (restResult.success) {
				// Save the key
				testctx[CONTEXT_SETTING_KEY] = restResult.responseBody[FIELD_KEY];
			}

        then:"Check we have a valid response"
			assert(restResult.success)
            assert(restResult.responseBody.id != null);
			assert(restResult.responseBody[FIELD_VALUE] == value);

        where:
            tenantId   | section        | key           | settingType | vocab        | defValue  | value
            TENANT_ONE | "test section" | "section key" | "number"    | "vocabulary" | "default" | "value"
    }

    void "Fetch a specific InstitutionSetting"(String tenantId, String ignore) {
        when:"Fetch the InstitutionSetting"

			// Call the base method to fetch it
			RestResult restResult = fetchObject(tenantId, PATH_INSTITUTION_SETTING, testctx[CONTEXT_SETTING_ID].toString());

        then:"Check we have a valid response"
            // Check the various fields
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_ID] == testctx[CONTEXT_SETTING_ID]);
            assert(restResult.responseBody[FIELD_KEY] == testctx[CONTEXT_SETTING_KEY]);
            assert(restResult.responseBody[FIELD_VALUE] == testctx[CONTEXT_SETTING_VALUE]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Search for InstitutionSettings"(String tenantId, String ignore) {
        when:"Search for InstitutionSettings"

            // Perform a search
			RestResult restResult = searchForObjects(tenantId, PATH_INSTITUTION_SETTING, FIELD_KEY, testctx[CONTEXT_SETTING_KEY]);

        then:"Check we have a valid response"
            // Check the various fields
            assert(restResult.success);
            assert(restResult.responseBody[0][FIELD_KEY] == testctx[CONTEXT_SETTING_KEY]);
			assert(restResult.responseBody[0][FIELD_VALUE] == testctx[CONTEXT_SETTING_VALUE]);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

    void "Update InstitutionSetting value"(String tenantId, value) {
        when:"Update description for InstitutionSetting"

			// Create the map with what is to be updated
            Map institutionSetting = [
                value : value
            ];

			// Call the base class to do the work
			RestResult restResult = updateObject(tenantId, PATH_INSTITUTION_SETTING, testctx[CONTEXT_SETTING_ID].toString(), institutionSetting);

        then:"Check we have a valid response"
            // Check the value
            assert(restResult.success);
            assert(restResult.responseBody[FIELD_VALUE] == value);

        where:
            tenantId   | value
            TENANT_ONE | "Value has been changed"
    }

    void "Delete a InstitutionSetting"(String tenantId, String ignore) {
        when:"Delete a InstitutionSetting"
			// Just call the base class
			RestResult restResult = deleteObject(tenantId, PATH_INSTITUTION_SETTING, testctx[CONTEXT_SETTING_ID].toString());

        then:"Check we have a valid response"
            // Check we have a valid response
            assert(restResult.success);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
    }

	void "Setup test application settings"(
		String tenantId,
		String key,
		String settingType,
		String defaultValue,
		String value
	)
	{
        when:"Create App Setting"
			InstitutionSetting institutionSetting = null;
			Tenants.withId(tenantId + '_mod_ill') {
				institutionSetting = institutionSettingsService.ensureSetting(
                    institutionService.getDefaultInstitution(),
                    key,
                    APP_SETTING_SECTION,
                    settingType,
                    null,
                    defaultValue,
                    value,
                    true
                );
			}

        then:"Check we have a valid app setting"
            assert(institutionSetting != null);

        where:
            tenantId   | key                       | settingType                      | defaultValue | value
            TENANT_ONE | APP_SETTING_STRING        | SettingsData.SETTING_TYPE_STRING | "default"    | null
            TENANT_ONE | APP_SETTING_INT_ONE       | SettingsData.SETTING_TYPE_STRING | null         | "1"
            TENANT_ONE | APP_SETTING_INT_MINUS_ONE | SettingsData.SETTING_TYPE_STRING | null         | "-1"
	}

	void "Check values for settings"(String tenantId, String ignore) {
        when:"Fetch the App Setting"
			boolean stringSettingExists = false;
			boolean intOneSettingExists = false;
			String settingDefault = null;
			int settingOne = 0;
			int settingMinusOne = 0;
			int settingNinetyEight = 0;
			Tenants.withId(tenantId + '_mod_ill') {
                Institution institution = institutionService.getDefaultInstitution();
				stringSettingExists = institutionSettingsService.hasSettingValue(
                    institution,
                    APP_SETTING_STRING,
                    "default"
                );
				intOneSettingExists = institutionSettingsService.hasSettingValue(
                    institution,
                    APP_SETTING_INT_ONE,
                    "99"
                );
				settingDefault = institutionSettingsService.getSettingValue(
                    institution,
                    APP_SETTING_STRING
                );
				settingOne = institutionSettingsService.getSettingAsInt(
                    institution,
                    APP_SETTING_INT_ONE
                );
				settingMinusOne = institutionSettingsService.getSettingAsInt(
                    institution,
                    APP_SETTING_INT_MINUS_ONE,
                    0,
                    true
                );
				settingNinetyEight = institutionSettingsService.getSettingAsInt(
                    institution,
                    APP_SETTING_INT_MINUS_ONE,
                    98
                );
			}

        then:"Check we have a valid app setting"
			assert(stringSettingExists);
			assert(!intOneSettingExists);
            assert(settingDefault == "default");
			assert(settingOne == 1);
			assert(settingMinusOne == -1);
			assert(settingNinetyEight == 98);

        where:
            tenantId   | ignore
            TENANT_ONE | ""
	}

	void "Check the map settings"() {
        when:"Setup the Map Setting"
			MapSettings mapSettings = new MapSettings();
			mapSettings.add(APP_SETTING_STRING, "default");
			mapSettings.add(APP_SETTING_INT_ONE, "1");
			mapSettings.add(APP_SETTING_INT_MINUS_ONE, "-1");

        then:"Check we have a valid map setting"
			assert(mapSettings.getSettingValue(null, APP_SETTING_STRING) == "default");
			assert(mapSettings.getSettingAsInt(null, APP_SETTING_INT_ONE) == 1);
			assert(mapSettings.getSettingAsInt(null, APP_SETTING_INT_MINUS_ONE, 0, true) == -1);
	}
}
