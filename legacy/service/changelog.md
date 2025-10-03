# Changelog

## Version 1.9.0

### Additions
* [General]
	* ILL-149 Created the domains / tables to store the documents and whther the user agrees to the copyright

### Changes
* [Chore]
	* Bump dep on gradle plugin
	* Changelog - Generate the changelog
	* bump commit
	* Moved a few move groovy files over to java
	* list tags when building an image for nexus
* [Fic]
	* Undone the changes I made to the statistics service
	* changelog for 1.8.4

### Fixes
* [General]
	* ensure info.app.idMeta is set for release builds. Suspect ./service/src/main/resources/moduleDescriptor/ModuleDescriptor-template.json is defunct and should be removed once confirmed
	* Went down 1 level to far to check the result
	* It appears I changed the name of the property being returned
	* Needed to explicitly specify the content type when using render and Json.toJson
	* Hopefully fixed all the issues around converting some of the groovy files to java
	* Replaced as json with Json.toJson(...) as result is now a java file
	* ILL-149 We can now store documents with a request, view copyright and mark the copyright as read
	* Quote git depth in gitlab-ci.yml
	* Finally sorted out the issue with maps and lists, do not use "as JSON" in the controller
	* Have converted IllStatistics back into java
	* Maybe there with the statistics this time
	* Added more logging
	* Changed how illStatistics.current is assigned
	* reverted a couple more conversions to java, where maps and lists are involved, have to dig a bit deeper it seems
	* reverted result.groovy / java
	* Updated the statistics service, so it is type correctly
	* Corrected a typo
	* Removed the grails swagger plugin

## Version 1.8.4

### Changes
* [Chore]
	* Changelog - Generate the changelog
	* Changelog for version 1.8.3

### Fixes
* [General]
	* Ensured the rota is output in the correct order
	* Was iterating over the audit instead of the rota, I'm sure I fixed this bug previously, but it appears not, unless I did not commit it and it was lost

## Version 1.8.3

### Changes
* [Chore]
	* Changelog - Generate the changelog
	* updated change log

### Fixes
* [General]
	* The tags can now be updated for patron requests trough the put option
	* Changed the timer to be every 10 minutes, as 30 is far to long, we probably should do this in a different manner
	* Changed the timer to be every 30 minutes
	* We now check addresses when they are updated, to try and ensure we do not duplicate addresses with the same label
	* If multiple institutions are not enabled, we return the directory entry associated with the default institution for the requesting institution for new requests
	* Removed class from refdata_value as that was causing an issue with the tests

## Version 1.8.2

### Changes
* [Chore]
	* Changelog - Generate the changelog
	* findByOwnerAndValue wasn't looking at the RefdataValue domain
	* Removed ill.all from the module descriptor as you need to have explicit permissions and not permission sets, also add a check for null in the shared index results
	* changelog for v1.8.1

## Version 1.8.1

### Changes
* [CHORE]
	* Allowed the shared index settings to be set from environment variables
* [Chore]
	* Changelog - Generate the changelog
	* Committed the change log

## Version 1.8.0

### Changes
* [Chore]
	* Changelog - Generate the changelog

## Version 1.7.0

### Changes
* [Chore]
	* Changelog - Generate the changelog

## Version 1.6.2

### Changes
* [Chore]
	* Changelog - Generate the changelog

### Fixes
* [General]
	* The naming authorities data was not getting loaded, puzzled as to why did it not throw an exception

## Version 1.6.1

### Changes
* [Chore]
	* Changelog - Generate the changelog
	* build - Tolerate missing pre-release property

## Version 1.6.0

### Additions
* [General]
	* LOCAL-9, LOCAL-11 Adding functionality to support searching institutions outside of the consortium
	* Add pull for deployment descriptor
	* Augment snapshot descriptor version with Metadata
	* Upgrade to Grails 6

### Changes
* [Build]
	* Publish descriptors for main commits too
* [CHORE]
	* If we have not been supplied a uuid for a directory entry then automatically allocate one
* [Chore]
	* Changelog - Generate the changelog
	* Changed how /admin/health is implemented to conform to other modules
	* Accidently changed the database to okapi from okapi_modules
	* We have to flip back to the correct database schema after obtaining the log
	* Small tweaks and additions to bring in line with ERM workflows

### Fixes
* [General]
	* For notices changed how we get hold of the admin email as we may have multiple institutions on the system
	* Use different property name to avoid clashes
	* Always include timestamp so OKAPI sorts chronologically
	* change image name to try and resolve docker push errors

## Version 1.5.1

### Changes
* [Chore]
	* Changelog - Generate the changelog

### Fixes
* [General]
	* update jib dependencies and corresponding gradle wrapper

## Version 1.5.0

### Additions
* [General]
	* Remove kafka dependency and rely instead upon internal grails eventing

### Changes
* [CHORE]
	* Corrected case of package name
	* Moved the DirectoryEntryService from the ill package to the directory package
* [Chore]
	* Changelog - Generate the changelog
	* merge

### Fixes
* [General]
	* Fixed the issue of editing a directory entry
	* ORS-33 Missed the fact that the parameter we are using for is term and not query
	* ORS-33 Changes to the so results from the shared index searchByQuery endpoint are consistent with search results in other areas
	* ORS-32 Added canDelete to the groups and user json
	* ORS-32 Changed the manageable institutions endpoint so that it only returned the appropriate information
	* ORS-32 Ensured the groups, institutions and users are sorted appropriately
	* ORS-32 Fixed an issue where there were more than 10 ill users
	* ORS-32 We now provide a list of folio users who have access to the UI modules
	* ORS-32 Added a migration that backs up appsettings then deletes the institution settings from it
	* ORS-32 Ensured the functionality is there to be able to edit groups, institutions and users
	* ORS-32 Added additional endpoints to aid the creation and editing for institution domain in the UI
	* ORS-24 The apis can now saywhether they are a system one, if so they are added to a separate system group instead of the all module one
	* ORS-32 Added additional endpoints to aid the user interface
	* ORS-31 The settings have now been converted over to being institution based
	* ORS-11 Forgot the module descriptor for the new end points
	* ORS-11 Aded the endpoint to say which group you are managing, add endpoints for manipulating users and institutions on groups
	* ORS-11 Fixed the parentage of the institution domains and added endpoints so institution and users can easily be added to groups, also added an endpoint to say which institutions a user can manage
	* ORS-11 The patron domain is now institution aware
	* ORS-11 Fixed a typo and made the while loop more robust
	* ORS-11 Corrected the migrations to remove the default value for the institution fields and to add a check on whether the default institution exists
	* insert default institution before attempting to default FKs to that value, use addColumn defaultValue instead of an update after adding the constraint (which fails on existing installations)

## Version 1.4.0

### Additions
* [General]
	* ORS-11 Implemented the starting point for multiple institutions in 1 tenant

### Changes
* [CHORE]
	* Uncommented the test
	* Missed a file from the merge
	* Temporarily commented out a test to see if it makes a differnce to the CI
	* A couple of minor style changes
	* ORS-29 Changed the majority of references to AppSettings to use the settingsService instead
* [Chore]
	* Changelog - Generate the changelog

### Fixes
* [General]
	* Increased the database connection timeout to 60 seconds
	* Reduced the timeout, back to 5 minutes, moved where it creates the tenants
	* Shouldn't let me out ...
	* Made a bit of a right mess of the template reference data
	* ORS-11 Notice event and policy are now institution specific
	* Upped the timeout to 10 minutes
	* ORS-11 Missed a file from previous commit
	* StatisticsSontroller did not derive frominstitution controller
	* ORS-11 Updated the statistics and Counters to cope with institutions
	* Added annotations for externalApi/directoryEntry
	* ORS-11 We now take into account the HOST LMS XXX domains now have an institution
	* ORS-11, ORS-29 Ensured we always have an institution, in the templating service the methods are no longer static
	* ORS-11 Had the wrong parameter for the request id when editing the request
	* ORS-11 Hopefully fixed the batch and report issues
	* ORS-11 Fixed the test in TimerSpec

## Version 1.3.5

### Changes
* [CHORE]
	* A couple of minor changes to comments and the style of the code
* [Chore]
	* Changelog - Generate the changelog
* [TEST]
	* Increased the timeout to see if that keeps the CI build happy

### Fixes
* [General]
	* Fixed swagger so it works through okapi
	* Commented out the enabling of cors as it outputs an invalid header value according to the console and breaks the apps

## Version 1.3.4

### Changes
* [Chore]
	* Changelog - Generate the changelog

### Fixes
* [General]
	* Re-enable directory entry edited event

## Version 1.3.3

### Changes
* [Chore]
	* Changelog - Generate the changelog
	* don't do directory entry modified events

## Version 1.3.2

### Changes
* [Chore]
	* Changelog - Generate the changelog
	* add cors setting to applicatin.yml

## Version 1.3.1

### Changes
* [Chore]
	* Changelog - Generate the changelog
* [Feature]
	* group types for user sync

## Version 1.3.0

### Changes
* [Chore]
	* Changelog - Generate the changelog

## Version 1.2.0

### Additions
* [General]
	* ORS-28 Added a shared index availability end point

### Changes
* [Chore]
	* Changelog - Generate the changelog

### Fixes
* [General]
	* directory sync task static
	* Fixed a couple of errors in the background task service

## Version 1.1.0

### Additions
* [General]
	* ORS-28 Breaking change, have changed the shared index interface so that it returns a generic shared index record, , the folio shared index record has not been mapped, this is for future work.
	* ORS-28 Added the ability to be able to search for availability against the Open RS shared index
	* ORS-24 Added the ability to generate the module descriptor template

### Changes
* [CHORE]
	* Removed the redundant stuff from JiscDiscover
* [Chore]
	* Changelog - Generate the changelog
* [Feature]
	* directory - Add setting to control auto creation of user affiliation options in users app
* [TEST]
	* Upped the polling time for state changes from 30 secs to 120 secs
	* Added an extra test and removed some redundant code
	* Added a couple more tests and fixed a bug in SharedIndexQueryService
	* ORS-24 Added test for the generation of the module descriptor template

### Fixes
* [General]
	* ORS-28 Made a couple of small fixes for the previous merge
	* ORS-28 Changed the import for the mock services to stars as its breaks it running normally
	* ORS-28 Somehow had lost the imports for the 2 mock services
	* ORS-28 Forgot to add test equivalents for the autowired connections
	* ORS-24 Sorted the permissions, so you can tell what has changed in a diff
	* ORS-24 Some directory entry permissions were missing
	* ORS-26 For ISO18626 we need to conform to the xsd and not the standard
	* ORS-24 The Module descriptor template is now the generated one, the url handlers in the module descriptor are now sorted
	* ORS-24 Added swagger and module description generation to the final controllers and methods that did not have it
	* ORS-24 Added swagger and module descriptor generation for refdata, fixed a couple of issues in the descriptor generation

## Version 1.0.1

### Changes
* [Chore]
	* Changelog - Generate the changelog

### Fixes
* [General]
	* Updated readme and test new release pipeline

## Version 1.0.0

### Additions
* [Kafka]
	* Tenant events drive kafka topics
* [General]
	* Allowed the timers to be directly executed from the api
	* HostLMS delete safety
	* RES_UNFILLED (#177)
	* Status 'terminal' boolean (#173)
	* locking on patron request actions
	* enable patron request helper functions to optionally obtain a row level lock

### Changes
* [Build]
	* Update WTK and OKAPI module
* [Chore]
	* Changelog - Generate the changelog
	* add release info to readme
	* Logging - Turn up the fed logs for dev/test.
	* Add Shcheduling behaviour.
	* Migrations - Include the features we want to pull in from TK
	* Deps - Bump the GORM base and the Hibernate implementation
	* Build - Tidy the gradle build file.
	* Build - Update modules
	* Update gradle wrapper
	* Protection from falling off end of rota
	* Call new locking capability in handle supplying or requesting agency message
* [Core]
	* working on integrating conventional commits plugin
* [Feature]
	* Change the kafka consumer creator to react to new events
* [NCIP]
	* Error reporting tweaks (#21)
* [TEST]
	* Added tests for Patron as well as adding the endpoints for patron as they were missing
	* Added timer execute tests and wxtended the controller to be able to take the id as a parameter, added a test for settings/worker
	* Added a test for getting the swagger api doc

### Fixes
* [General]
	* ORS-23 Added s drtting to disable local availability checks
	* Added comments tp the swagger service, ipdated the Api annotation for all controllers, added a path for the swagger plugin document
	* Have implemented a service that generates the api doc for swagger from the annotations
	* Refactor and fix
	* reshareActionService not reshareActionsService
	* Wrong property name in handleCancelRequestReceived - should be autoCancel not autoRespond.