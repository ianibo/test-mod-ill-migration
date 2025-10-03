package com.k_int.ill

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
class PredefinedId implements MultiTenant<PredefinedId> {
    String id;
    String referencesId;

    static mapping = {
                  id column: 'pi_id', generator: 'assigned', length:36
        referencesId column: 'pi_references_id', length:36
             version column: 'pi_version'

    }

    static String createUUIDv3(String namespace, String id) {
        String predefinedId = namespace + ':' + id;

        // Create a v3 UUID based on the combined table and passed in id
        return(UUID.nameUUIDFromBytes(predefinedId.getBytes()).toString());
    }

    static PredefinedId lookupReference(String namespace, String id) {
        return(get(createUUIDv3(namespace, id)));
    }

    static String lookupReferenceId(String namespace, String id) {
        PredefinedId predefinedId = lookupReference(namespace, id);

        // Return the id it references, if the association has been created
        return(predefinedId?.referencesId);
    }

    static void ensureExists(String namespace, String id, String referencesId) {

        // Create a new reference, but not if it already exists
        PredefinedId predefinedId = lookupReference(namespace, id);
        if (predefinedId == null) {
            // Dosn't already exist, so create
            predefinedId = new PredefinedId();
            predefinedId.id = createUUIDv3(namespace, id);
        }

        // We always update the reference in case it has changed
        predefinedId.referencesId = referencesId;
        predefinedId.save(flush:true, failOnError:true);
    }
}
