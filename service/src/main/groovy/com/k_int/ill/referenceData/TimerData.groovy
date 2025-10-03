package com.k_int.ill.referenceData;

import com.k_int.ill.Timer;

import groovy.util.logging.Slf4j

@Slf4j
public class TimerData {

	public void load() {
		log.info("Adding timer data to the database");

        // Timer to check for stale requests
		Timer.ensure(
            "CheckForStaleSupplierRequests",
            "Check supplier requests have not become stale",
            "FREQ=DAILY",
            "CheckForStaleSupplierRequests",
            null,
            true,
            true
        );

        // Timer to check for requests we need to retry
        Timer.ensure(
            "RequestNetworkRetry",
            "Retry requests that have the network status of Retry",
            "FREQ=MINUTELY;INTERVAL=10",
            "RequestNetworkRetry"
        );

        // Timer to check for requests we need to check for timeout
        Timer.ensure(
            "RequestNetworkTimeout",
            "Check to see if a timeout request has been received, set it to be resent",
            "FREQ=MINUTELY;INTERVAL=10",
            "RequestNetworkTimeout"
        );

        // Timer to check for overdue requests
        Timer.ensure(
            "CheckForOverdueSupplierRequests",
            "Check for any requests that have not been returned to the supplier by the requester",
            "FREQ=DAILY",
            "CheckForOverdueSupplierRequests",
            null,
            true,
            true
        );

        // Check for patron notices
        Timer.ensure(
            "ProcessPatronNotices",
            "Process patron notices",
            "FREQ=MINUTELY;INTERVAL=10",
            "ProcessPatronNotices"
        );

        // Timer to clear out data from the ProtocolAudit table
        Timer.ensure(
            "RemoveFromProtocolAudit",
            "Removes records from the ProtocolAudit table that have become old",
            "FREQ=DAILY",
            "RemoveFromProtocolAudit",
            null,
            true,
            true
        );

        // Timer to process the mail queue
        Timer.ensure(
            "ProcessMailQueue",
            "Sends emails that are waiting to be sent",
            "FREQ=MINUTELY;INTERVAL=10",
            "ProcessMailQueue",
			null,
			true
        );

        // Timer to clear out invalid records from the RemoteAction table
        Timer.ensure(
            "CheckRemoteAction",
            "Removes records from the remote action table that are no longer valid",
            "FREQ=DAILY",
            "CheckRemoteAction",
            null,
            true,
            true
        );
	}

	public static void loadAll() {
		(new TimerData()).load();
	}
}
