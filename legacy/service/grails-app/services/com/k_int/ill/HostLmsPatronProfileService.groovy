package com.k_int.ill;

import com.k_int.institution.Institution;

/**
 * Perform any services required by the HostLMSPatronProfile domain
 *
 */
public class HostLmsPatronProfileService extends GenericCodeNameService<HostLMSPatronProfile> {

    public HostLmsPatronProfileService() {
        super(HostLMSPatronProfile, { instance ->
            return((instance.hidden != null) && (instance.hidden == true));
        });
    }

    /**
     * Given a code and name looks to see if the HostLMSPatronProfile record already exists and if not creates it
     * If it it does exist, it ensures that it is active
     * @param code The code for the profile
     * @param name The name for the profile
     * @return The record that represents this code and name
     */
    public HostLMSPatronProfile ensureActive(Institution institution, String code, String name) {
        log.debug('Entering HostLMSPatronProfileService::ensureActive(' + code + ', ' + name + ');');
        HostLMSPatronProfile patronProfile = ensureExists(institution, code, name, { instance, newRecord ->
            instance.hidden = false;
        });

        log.debug('Exiting HostLMSPatronProfileService::ensureActive');
        return(patronProfile);
    }
}
