package com.k_int.ill;

import com.k_int.directory.DirectoryEntry;
import com.k_int.ill.referenceData.RefdataValueData;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.okapi.OkapiClient;
import com.k_int.settings.SystemSettingsService

import grails.core.GrailsApplication;
import grails.events.annotation.Subscriber;
import grails.gorm.multitenancy.Tenants;
import services.k_int.utils.UUIDUtils;

/**
 *
 */
public class AppInteractionService {

  SystemSettingsService systemSettingsService;
  OkapiClient okapiClient;
  GrailsApplication grailsApplication;

  // We really could do with a way to debounce this in system startup
  @Subscriber('UserEditedDirectory')
  public pushManagedDirectoryEntriesAsUserAffiliations(String tenantId) {
    // Look at configuraiton

    log.debug("AppInteractionService::pushManagedDirectoryEntriesAsUserAffiliations(${tenantId})");
    try {
	  // The tenant id seems to be already coming through correctly, so no need to add the "_mod_ill" postfix
      Tenants.withId(tenantId) {

        // If user affiliation sychronization is enabled, then do it.
        String sv =  systemSettingsService.getSettingValue(SettingsData.SETTING_DIRECTORY_SYNC_USER_AFFILIATION)
        if ( ( sv != null ) && ( sv.equals(RefdataValueData.YES_NO_YES) ) ) {
          log.debug("Directory entry synchronization with users is enabled");

          String groups_st = systemSettingsService.getSettingValue(SettingsData.SETTING_DIRECTORY_SYNC_USER_GROUPS);
          String[] groups = ( groups_st != null ? groups_st.split(',') : [] ) ;


          UUID uuid_of_groups_custom_field = UUIDUtils.dnsUUID("user_groups_cf");


          // https://s3.amazonaws.com/foliodocs/api/folio-custom-fields/p/custom-fields.html
          // We create custom fields with a put to https://ch-ok-test12.sph.thelibraryplatform.com/custom-fields
          // example body:
          //
          // Key info - set entityType = user but post X-Okapi-Module-Id of ill-1.0  so that custom props knows we are installing directory
          // definitions
          //
          // {"customFields":
          // [
          // {"id":"5c8ad0ce-edbe-4f38-91a3-abee3970a035",
          //  "name":"Agency","refId":"agency","type":"MULTI_SELECT_DROPDOWN","entityType":"user","visible":true,"required":false,"isRepeatable":false,"order":1,"helpText":"","selectField":{"multiSelect":true,
          //             "options":{"values":[
          //                            {"id":"opt_0","value":"Inst1","default":false},
          //                            {"id":"opt_1","value":"inst2","default":false},
          //                            {"id":"opt_2","value":"Inst3","default":false}],"sortingOrder":"CUSTOM"}},
          //              "metadata":{"createdDate":"2023-11-17T15:53:09.492+00:00","createdByUserId":"3d9cb149-9da1-47a1-ab11-f683ab5d2d21","createdByUsername":"admin","updatedDate":"2023-11-17T15:53:09.492+00:00","updatedByUserId":"3d9cb149-9da1-47a1-ab11-f683ab5d2d21"}},
          // {"id":null,
          //  "name":"test2.1","visible":true,"required":false,"helpText":"","type":"MULTI_SELECT_DROPDOWN",
          //  "selectField":{"multiSelect":true,"options":{"values":[
          //              {"value":"value2","id":"opt_0","default":false},
          //              {"id":"opt_1","default":false,"value":"value3"}
          //              ]}},"entityType":"user"}
          // ]}


          // Now the question is.. Is te UUID we need to post to configurations/entries ALWAYS e514d966-4ac2-4957-b267-841bc7594258 for users?

          // then we post to https://ch-ok-test12.sph.thelibraryplatform.com/configurations/entries
          // payload {"module":"USERS","configName":"custom_fields_label","value":"test2"}
          List multi_select_values = []
          DirectoryEntry.executeQuery('select de from DirectoryEntry de where de.status.value = :m order by de.slug',[m:'managed']).each { de ->
            groups.each { g ->
              multi_select_values.add(['id':"${g}@${de.slug}".toString(), 'value': "${g}@${de.slug}".toString(), default: false])
            }
          }

          Map groups_cf = [
            id : uuid_of_groups_custom_field.toString(),
            name: 'Affiliations',
            refId: 'agency',
            type: 'MULTI_SELECT_DROPDOWN',
            entityType: 'user',
            visible: true,
            required: false,
            isRepeatable: false,
            order:1,
            helpText: '',
            selectField:[
              multiSelect:true,
              options:[
                values: multi_select_values
                // [
                //   [ 'id':'opt_0', 'value':'Inst1','default':false ],
                //   [ 'id':'opt_1', 'value':'Inst2','default':false ]
                // ]
              ]
            ]
          ]

          /* This currenty fails with tennant cannot be resolved outside a session - A solition may be to wrap with tenants.withId */

          if ( okapiClient.withTenant().providesInterface("custom-fields", "^2.0") ) {
            log.debug("Checking user group memberships");
            def cfresult = okapiClient.put("/custom-fields/${uuid_of_groups_custom_field.toString()}", affiliations_cf)
          }
          else {
            log.warn("Okapi client reports that users ^18");
          }
        }
      }
    }
    catch ( Exception e ) {
        e.printStackTrace();
    }
  }
}
