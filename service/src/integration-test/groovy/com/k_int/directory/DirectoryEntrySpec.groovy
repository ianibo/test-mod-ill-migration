package com.k_int.directory;

import com.k_int.TestBase;
import com.k_int.okapi.OkapiHeaders;

import grails.gorm.multitenancy.Tenants;
import grails.testing.mixin.integration.Integration;
import groovy.util.logging.Slf4j;
import spock.lang.*;

@Slf4j
@Integration
@Stepwise
class DirectoryEntrySpec extends TestBase {

	def grailsApplication

	Closure authHeaders = {
		header OkapiHeaders.TOKEN, 'dummy'
		header OkapiHeaders.USER_ID, 'dummy'
		header OkapiHeaders.PERMISSIONS, '[ "directory.admin", "directory.user", "directory.own.read", "directory.any.read"]'
	}

	void "Attempt to delete any old tenant"(String tenantid, String name) {

		when:"We post a delete request"
			boolean response = deleteTenant(tenantid, name);

		then:"Any old tenant removed"
        	assert(response);

		where:
			tenantid    | name
			TENANT_FOUR | TENANT_FOUR
	}

	// Set up a new tenant called RSTestTenantA
	void "Set up test tenants "(tenantid, name) {
		when:"We post a new tenant request to the OKAPI controller"
			boolean response = setupTenant(tenantid, name);

		then:"The response is correct"
			assert(response);

		where:
			tenantid    | name
			TENANT_FOUR | TENANT_FOUR
	}

	void "test directory entry creation"(tenantid, name) {

		log.debug("Sleep 2s to see that schema creation went OK - running test for ${tenantid} ${name}");

		// Switching context, just want to make sure that the schema had time to finish initialising.
		Thread.sleep(2000)

		Map new_entry = [
			id: java.util.UUID.randomUUID().toString(),
			name: name,
			slug: name,
			description: 'Test new entry',
			status: 'managed'
		]

		when: "We create a new directory entry"
			log.debug("Attempt to post ${new_entry}");

			setHeaders(
				[
					'X-Okapi-Tenant': tenantid,
					'X-Okapi-Token': 'dummy',
					'X-Okapi-User-Id': 'dummy',
					'X-Okapi-Permissions': '[ "directory.admin", "directory.user", "directory.own.read", "directory.any.read" ]'
				]
			);
			def resp = doPost("${baseUrl}ill/directory/entry".toString(), new_entry);

		then: "New directory entry created with the given name"
			log.debug("Got response ${resp}");
			resp != null;

		where:
			tenantid    | name
			TENANT_FOUR | 'University of DIKU'
	}

	void "add Friend"(tenant_id, friend_url) {

		log.debug("Add a friend");

		when: "We add a new friend"
			def dirent = null;
			setHeaders(['X-Okapi-Tenant': tenant_id])
			def resp = doGet("${baseUrl}ill/directory/entry".toString());

			// def resp = doGet("${baseUrl}ill/directory/api/addFriend?friendUrl=$friend_url".toString()) {
			//   authHeaders.rehydrate(delegate, owner, thisObject)()
			//   accept 'application/json'
			// }

		then: "New directory entry created with the given name"
			// dirent.name == name
			log.debug("Add friend response: ${resp}");
			resp != null

			// Give the out of band kafka event enough time to propagate
			Thread.sleep(2000);

		where:
			tenant_id   | friend_url
			TENANT_FOUR | 'https://raw.githubusercontent.com/openlibraryenvironment/mod-directory/master/seed_data/test/test_cons.json'
	}

	// Check parent loop failure
	void "Check Parent loop fails"(tenantid, heirachy, expected, runthrough) {
		log.debug("Checking parent loop");
		log.debug("========================================== runthrough ${runthrough} ==========================================")

		when: "We create some directory entries and set their parent structure"
			def dir1 = null;
			def dir2 = null;
			def dir3 = null;
			def dir4 = null;
			def dir5 = null;
			def dir6 = null;
			// Make some Directory Entries
			Tenants.withId(tenantid+'_mod_ill') {
				DirectoryEntry.withTransaction {
					dir1 = DirectoryEntry.findByName('dir1') ?: new DirectoryEntry(id:'dir1', name:'dir1', slug:'dir1').save(flush:true, failOnError:true);
				}
			}
			Tenants.withId(tenantid+'_mod_ill') {
				DirectoryEntry.withTransaction {
					dir2 = DirectoryEntry.findByName('dir2') ?: new DirectoryEntry(id:'dir2', name:'dir2', slug:'dir2').save(flush:true, failOnError:true);
				}
			}
			Tenants.withId(tenantid+'_mod_ill') {
				DirectoryEntry.withTransaction {
					dir3 = DirectoryEntry.findByName('dir3') ?: new DirectoryEntry(id:'dir3', name:'dir3', slug:'dir3').save(flush:true, failOnError:true);
				}
			}
			Tenants.withId(tenantid+'_mod_ill') {
				DirectoryEntry.withTransaction {
					dir4 = DirectoryEntry.findByName('dir4') ?: new DirectoryEntry(id:'dir4', name:'dir4', slug:'dir4').save(flush:true, failOnError:true);
				}
			}
			Tenants.withId(tenantid+'_mod_ill') {
				DirectoryEntry.withTransaction {
					dir5 = DirectoryEntry.findByName('dir5') ?: new DirectoryEntry(id:'dir5', name:'dir5', slug:'dir5').save(flush:true, failOnError:true);
				}
			}
			Tenants.withId(tenantid+'_mod_ill') {
				DirectoryEntry.withTransaction {
					dir6 = DirectoryEntry.findByName('dir6') ?: new DirectoryEntry(id:'dir6', name:'dir6', slug:'dir6').save(flush:true, failOnError:true);
				}
			}

			// Change list of strings to be the ids of entries defined above (choosing id so that we can easily pass these between sessions)
			for (def i=0; i < heirachy.size(); i++){
				Tenants.withId(tenantid+'_mod_ill') {
					DirectoryEntry.withTransaction {
						heirachy[i] = (DirectoryEntry.findByName(heirachy[i])).id
					}
				}
			}

			// Set parents of those entries one by one.
			def parentValidation = null
			try {
				for (def i = 0; i < heirachy.size(); i++) {
					Tenants.withId(tenantid+'_mod_ill') {
						DirectoryEntry.withTransaction {
							def dirent = DirectoryEntry.get(heirachy[i])
							if (i + 1 < heirachy.size()) {
								dirent.parent = DirectoryEntry.get(heirachy[i + 1])
								dirent.save(flush:true, failOnError: true);
							}
						}
					}
				}
				parentValidation = 'succeeds'
			} catch(grails.validation.ValidationException e) {
				log.debug("An error has occured: ${e}")
				parentValidation = 'fails because loop'
			} catch (Exception e) {
				log.debug("An error has occured: ${e}")
				parentValidation = 'fails otherwise'
			}

		then: "Check that we get the expected validation failures"
			log.debug("Checking validator. Expected: ${expected}, Validation: ${parentValidation}")
			expected == parentValidation

			// Give the out of band kafka event enough time to propagate
			Thread.sleep(2000);

			log.debug("==================================================================================================")

		where:
			tenantid    | heirachy                         | expected             | runthrough
			TENANT_FOUR | ['dir1', 'dir2', 'dir3', 'dir1'] | 'fails because loop' | 1
			TENANT_FOUR | ['dir4', 'dir5', 'dir6']         | 'succeeds'           | 2
	}

	void "test external api"(String tenant_id, friend_url) {

		when: "We ask the externl API for the index of entries"
			setHeaders(['X-Okapi-Tenant': tenant_id])
			def resp = doGet("${baseUrl}ill/externalApi/managedDirectories".toString());

		then: "We get back the expected list"
			// dirent.name == name
			log.debug("externalApi list: ${resp}");
			resp != null

		where:
			tenant_id   | friend_url
			TENANT_FOUR | 'https://raw.githubusercontent.com/openlibraryenv'

	}

	void "test freshen worker thread"() {
		when: "We call the freshen endpoint"
			String tenant_id = TENANT_FOUR
			setHeaders(['X-Okapi-Tenant': tenant_id])
			def resp = doGet("${baseUrl}ill/directory/api/freshen".toString());

			Thread.sleep(4000);

		then: "All is well"
			// dirent.name == name
			log.debug("externalApi list: ${resp}");
			resp != null
	}
}
