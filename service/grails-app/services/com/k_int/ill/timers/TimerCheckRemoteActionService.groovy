package com.k_int.ill.timers;

import com.k_int.ill.RemoteAction;
import com.k_int.ill.RemoteActionService;
import com.k_int.institution.Institution;

/**
 * Checks the remote action records and removes any expired records
 *
 * @author Chas
 *
 */
public class TimerCheckRemoteActionService extends AbstractTimer {

	RemoteActionService remoteActionService
    @Override
    public void performTask(String tenant, Institution institution, String config) {
        log.debug("Removing expired remote actions");
		RemoteAction.findAll().each { RemoteAction remoteAction ->
			try {
				// Is the action valid
				if (!remoteActionService.isValid(remoteAction)) {
					// It is not valid, so delete it
					remoteAction.delete(flush: true);
				}
			} catch (Exception e) {
				log.error("Exception thrown while trying to delete remote action: " + remoteAction.id, e);
			}
		}
        log.debug("Finished removing expired remote actions");
    }
}
