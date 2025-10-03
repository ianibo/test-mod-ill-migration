package com.k_int.ill;

import com.k_int.institution.Institution;

/**
 * Perform any services required by the HostLMSShelvingLocation domain
 *
 */
public class HostLmsShelvingLocationService extends GenericCodeNameService<HostLMSShelvingLocation> {

    public HostLmsShelvingLocationService() {
        super(HostLMSShelvingLocation);
    }

    /**
     * Given a code,  name and supply preference create a new HostLMSShelvingLocation record
     * @param code The code for the location
     * @param name The name for the location
     * @param supplyPreference The supply preference defaults to 0
     * @return The record that represents this code and name
     */
    public HostLMSShelvingLocation ensureExists(Institution institution, String code, String name, long supplyPreference = 0) {
        log.debug('Entering HostLMSShelvingLocationService::ensureExists(' + code + ', ' + name + ', ' + supplyPreference.toString()+ ');');

        HostLMSShelvingLocation loc = ensureExists(institution, code, name, { instance, newRecord ->
            if (newRecord) {
                instance.supplyPreference = supplyPreference;
            }
        });

        log.debug('Exiting HostLMSShelvingLocationService::ensureActive');
        return(loc);
    }
}
