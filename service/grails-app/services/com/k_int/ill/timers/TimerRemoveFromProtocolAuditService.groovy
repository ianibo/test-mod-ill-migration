package com.k_int.ill.timers;

import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.Duration;

import com.k_int.ill.ProtocolAudit;
import com.k_int.ill.ProtocolType;
import com.k_int.ill.referenceData.SettingsData;
import com.k_int.institution.Institution;
import com.k_int.settings.InstitutionSettingsService

/**
 * Removed records from protocol audit that have out stayed there welcome
 *
 * @author Chas
 *
 */
public class TimerRemoveFromProtocolAuditService extends AbstractTimer {

	/** The query to be performed to find the overdue requests */
    private static final String DELETE_AUDIT_RECORDS_QUERY = """
delete ProtocolAudit as pa
where pa.dateCreated < :dateToDeleteBefore and
      pa.protocolType = :protocolType
""";

    InstitutionSettingsService institutionSettingsService;

	@Override
	public void performTask(String tenant, Institution institution, String config) {
        // Only interested in the date segment and that it is in UTC
        DateTime today = new DateTime(TimeZone.getTimeZone(TIME_ZONE_UTC), System.currentTimeMillis()).startOfDay();

		// Run through each of the protocols clearing out the audit records
        clearDownAuditRecords(institution, ProtocolType.ISO18626, today, SettingsData.SETTING_LOGGING_ISO18626_DAYS);
        clearDownAuditRecords(institution, ProtocolType.NCIP, today, SettingsData.SETTING_LOGGING_NCIP_DAYS);
        clearDownAuditRecords(institution, ProtocolType.Z3950_REQUESTER, today, SettingsData.SETTING_LOGGING_Z3950_REQUESTER_DAYS);
        clearDownAuditRecords(institution, ProtocolType.Z3950_RESPONDER, today, SettingsData.SETTING_LOGGING_Z3950_RESPONDER_DAYS);
	}

    private void clearDownAuditRecords(
        Institution institution,
        ProtocolType protocolType,
        DateTime todaysDate,
        String daysToKeepKey
    ) {
        try {
            // First of all lookup to see how many days we need to keep the data
            int daysToKeep = institutionSettingsService.getSettingAsInt(institution, daysToKeepKey, 30);
            Duration duration = new Duration(-1, daysToKeep, 0);
            Date dateToDeleteBefore = new Date(todaysDate.addDuration(duration).getTimestamp());;
            log.info("Deleting records from ProtocolAudit prior to " + dateToDeleteBefore.toString()+ " for protocol: " + protocolType.toString());

            // Now we can execute the delete
            Map deleteParameters = [
                dateToDeleteBefore : dateToDeleteBefore,
                protocolType : protocolType
            ];
            ProtocolAudit.executeUpdate(DELETE_AUDIT_RECORDS_QUERY, deleteParameters);
        } catch (Exception e) {
            log.error("Exception thrown while trying to delete old records from protocol audit", e);
        }
    }
}
