package com.k_int.ill;

import grails.gorm.multitenancy.Tenants;
import services.k_int.core.FolioLockService;

;/**
 * This is a gateway into FolioLockService so we ensure the locks are tenant based
 * Whereas the locks made against FolioLockService are system wide
 */
public class LockService {

	// This should go somewhere more useful, but putting it here for now
	private static final String TENANT_POSTFIX = "_mod_ill";

    FolioLockService folioLockService;

    /**
     * Attempts to obtain the lock with the specified name and
     * if it is obtained within the specified number of seconds then execute workToBeExecuted
     * @param tenant The tenant the lock is to be obtained for
     * @param lockName The name of the lock to be obtained
     * @param maximumSeconds The maximum number of seconds to wait for the lock
     * @param workToBeExecuted A closure that is the work to be executed if we manage to obtain the lock
     * @return true if we obtained the lock otherwise false if we do not manage to get the lock
     */
    public boolean performWorkIfLockObtained(
        String tenant,
        String lockName,
        int maximumSeconds,
        Closure workToBeExecuted
    ) {
        // Default to not being successful
        boolean result = false;
		String fullLockName = tenant + ":" + lockName;

        // Can only continue if we have a tenant and lock name
        if (tenant && lockName) {
            Closure scopedWork = {
                // Obtaining the lock changed which database schema we are looking at, so we need to flip back to the correct database schema
                try {
					// Start a new session
                    PatronRequest.withNewSession { session ->
						try {
							// We also now need to start a new transaction
	                        PatronRequest.withNewTransaction {
								try {
									Tenants.withId(tenant.toLowerCase() + TENANT_POSTFIX, workToBeExecuted);
				                } catch (Exception e) {
				                    log.error("Exception reseting database tenant for " + tenant + "\" after obtaining federated lock using Tenants.withId", e);
				                }
	                        }
		                } catch (Exception e) {
		                    log.error("Exception starting a new transaction for lock " + fullLockName + " and tenant " + tenant + " after obtaining federated lock", e);
		                }
		 			}
                } catch (Exception e) {
                    log.error("Exception obtaing new session for lock " + fullLockName + " and tenant " + tenant + " after obtaining federated lock", e);
                }
            };

            // We concatenate the tenant and lock name so that we have no effect on other tenants
            result = folioLockService.federatedLockAndDoWithTimeoutOrSkip(
                fullLockName,
                maximumSeconds * 1000,
                scopedWork
            );
        }

        // Let the caller know if we were successful or not
        return(result);
    }
}
