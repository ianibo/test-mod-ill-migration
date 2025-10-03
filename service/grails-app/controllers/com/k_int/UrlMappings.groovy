package com.k_int;

/** 
 * See https://docs.grails.org/latest/guide/theWebLayer.html#urlmappings
 * for how to setup the mappings, there are many different styles here, I may aim to unify how it is done 
 */
class UrlMappings {

  static mappings = {

    "/"(controller: 'application', action:'index')

    "/ill"(controller: 'application', action:'index')

    "/ill/statistics" (controller: 'statistics', action: 'index' )
    "/ill/statistics/forSymbol" (controller: 'statistics', action: 'forSymbol' )

    "/ill/externalApi/statistics" (controller: 'externalApi', action:'statistics' )
    "/ill/externalApi/iso18626" (controller: 'iso18626', action:'iso18626', parseRequest: false )
    "/ill/externalApi/statusReport" (controller: 'externalApi', action:'statusReport' )
    "/ill/externalApi/managedDirectories" (controller: 'externalApi', action:'managedDirectories' )
    "/ill/externalApi/directoryEntry" (controller: 'externalApi', action:'directoryEntry' )

    "/ill/patronrequests" (resources:'patronRequest') {
      '/validActions' (controller: 'patronRequest', action: 'validActions')
      '/performAction'  (controller: 'patronRequest', action: 'performAction')
      '/manualCloseStates'  (controller: 'patronRequest', action: 'manualCloseStates')
      '/viewDocument'  (controller: 'patronRequest', action: 'viewDocument')
      '/fetchCopyright'  (controller: 'patronRequest', action: 'fetchCopyright')
      '/agreeCopyright'  (controller: 'patronRequest', action: 'agreeCopyright')
    }
    "/ill/patronrequests/bulkAction" (controller: "patronRequest", action: "bulkAction")
    "/ill/patronrequests/generatePickListBatch" (controller: "patronRequest", action: "generatePickListBatch")
    "/ill/patronrequests/markBatchAsPrinted" (controller: "patronRequest", action: "markBatchAsPrinted")
    "/ill/patronrequests/newRequestDetails" (controller: "patronRequest", action: "newRequestDetails")
    "/ill/patronrequests/openURL" (controller: "patronRequest", action: "openURL")
	post "/ill/patronrequests/$patronRequestId/uploadDocument"  (controller: 'patronRequest', action: 'uploadDocument')
  
    "/ill/patron/$patronIdentifier/canCreateRequest" (controller: 'patron', action: 'canCreateRequest')
    "/ill/patron" (resources: 'patron')

    "/ill/availableAction/fromStates/$stateModel/$actionCode" (controller: "availableAction", action: "fromStates")
    "/ill/availableAction/toStates/$stateModel/$actionCode" (controller: "availableAction", action: "toStates")
    "/ill/availableAction/createGraph/$stateModel" (controller: "availableAction", action: "createGraph")

    "/ill/report/createUpdate" (controller: "report", action: "createUpdate")
    "/ill/report/execute" (controller: "report", action: "execute")
    "/ill/report/generatePicklist" (controller: "report", action: "generatePicklist")

    "/ill/fileDefinition/fileUpload" (controller: "fileDefinition", action: "fileUpload")
    "/ill/fileDefinition/fileDownload/$fileId" (controller: "fileDefinition", action: "fileDownload")

    "/ill/stateModel/export" (controller: "stateModel", action: "export")
    "/ill/stateModel/import" (controller: "stateModel", action: "ingest")
    "/ill/stateModel/getValidActions" (controller: "stateModel", action: "getValidActions")

    '/ill/noticeEvent' (resources: 'noticeEvent')
    '/ill/noticePolicies' (resources: 'noticePolicy')

    "/ill/timers/execute" (controller: "timer", action: "execute")

    "/ill/batch" (resources: 'batch' )
    "/ill/copyrightMessage" (resources: 'copyrightMessage' )
    "/ill/copyrightMessage/createEditDetails" (controller: "copyrightMessage", action: "createEditDetails")
    "/ill/protocol" (resources: 'protocol' )
    "/ill/shipments" (resources: 'shipment' )
    "/ill/timers" (resources: 'timer' )
    "/ill/hostLMSLocations" (resources: 'hostLMSLocation' )
    "/ill/hostLMSPatronProfiles" (resources: 'hostLMSPatronProfile' )
    "/ill/hostLMSItemLoanPolicy" (resources: 'hostLMSItemLoanPolicy' )
    "/ill/shelvingLocations" (resources: 'shelvingLocations' )
    "/ill/shelvingLocationSites" (resources: 'shelvingLocationSite')

    "/ill/illSmtpMessage/createEditDetails" (controller: "illSmtpMessage", action: "createEditDetails")
    "/ill/illSmtpMessage/tokenValues/$patronRequestId" (controller: "illSmtpMessage", action: "tokenValues")
    "/ill/illSmtpMessage" (resources: 'illSmtpMessage')

    "/ill/institution/createEditDetails" (controller: "institution", action: "createEditDetails")
    "/ill/institution/$id/modifyGroups" (controller: "institution", action: "modifyGroups")
    "/ill/institution/$id/users" (controller: "institution", action: "users")
    "/ill/institution" (resources: 'institution' )

    "/ill/institutionGroup/createEditDetails" (controller: "institutionGroup", action: "createEditDetails")
    "/ill/institutionGroup/$id/modifyInstitutions" (controller: "InstitutionGroup", action: "modifyInstitutions")
    "/ill/institutionGroup/$id/modifyUsers" (controller: "InstitutionGroup", action: "modifyUsers")
    "/ill/institutionGroup" (resources: 'institutionGroup' )

    "/ill/institutionUser/canManage" (controller: "institutionUser", action: "canManage")
    "/ill/institutionUser/createEditDetails" (controller: "institutionUser", action: "createEditDetails")
    "/ill/institutionUser/manageInstitution" (controller: "institutionUser", action: "manageInstitution")
    "/ill/institutionUser/$id/institutions" (controller: "institutionUser", action: "institutions")
    "/ill/institutionUser/$id/modifyGroups" (controller: "institutionUser", action: "modifyGroups")
    "/ill/institutionUser" (resources: 'institutionUser' )

    "/ill/sharedIndexQuery" (controller: 'sharedIndexQuery', action: 'passThrough', parseRequest: false)
    "/ill/sharedIndexQuery/findMoreSuppliers" (controller: 'sharedIndexQuery', action: 'findMoreSuppliers')
    "/ill/sharedIndexQuery/token" (controller: 'sharedIndexQuery', action: 'token')
    "/ill/sharedIndexQuery/byId" (controller: 'sharedIndexQuery', action: 'byId')
    "/ill/sharedIndexQuery/byQuery" (controller: 'sharedIndexQuery', action: 'byQuery')
    "/ill/sharedIndexQuery/availability" (controller: 'sharedIndexQuery', action: 'availability')

    "/ill/remoteAction/$id/perform" (controller: "remoteAction", action: "perform")
    "/ill/remoteAction" (resources: 'remoteAction' )
	"/ill/search" (resources: 'search' )
	"/ill/searchAttribute" (resources: 'searchAttribute' )
	"/ill/searchGroup" (resources: 'searchGroup' )
	"/ill/searchTree" (resources: 'searchTree' )
	
    "/ill/directoryGroup" (resources: 'directoryGroup' )
	"/ill/directoryGroups" (resources: 'directoryGroups' )
	
	"/ill/directory/entry/validate" (controller: 'directoryEntry', action: 'validate')
    "/ill/directory/entry" (resources: 'directoryEntry', controller: "directoryEntry" ) {
      collection {
        "/validate" (controller: 'directoryEntry', action: 'validate')
      }
    }

    // Call /ill/refdata to list all refdata categories
    '/ill/refdata'(resources: 'refdata') {
      collection {
        "/$domain/$property" (controller: 'refdata', action: 'lookup')
      }
    }

    '/ill/status'(resources: 'status', excludes: ['update', 'patch', 'save', 'create', 'edit', 'delete'])

    "/ill/kiwt/config/$extended?" (controller: 'illConfig' , action: "resources")
    "/ill/kiwt/config/schema/$type" (controller: 'illConfig' , action: "schema")
    "/ill/kiwt/config/schema/embedded/$type" (controller: 'illConfig' , action: "schemaEmbedded")
    "/ill/kiwt/raml" (controller: 'illConfig' , action: "raml")

    "/ill/settings/tenantSymbols" (controller: 'illSettings', action: 'tenantSymbols');
    "/ill/settings/worker" (controller: 'illSettings', action: 'worker');
    "/ill/settings/institutionSetting" (resources: 'institutionSetting')
    "/ill/settings/systemSetting" (resources: 'systemSetting')

     // Call /ill/custprop  to list all custom properties
    '/ill/custprops'(resources: 'customPropertyDefinition')

	// Imported from mod directory
    '/ill/tags'(resources: 'tags')
    "/ill/directory/symbol"(resources:'Symbol')
    "/ill/directory/service"(resources:'Service')
    "/ill/directory/serviceAccount"(resources:'ServiceAccount')
    "/ill/directory/namingAuthority"(resources:'NamingAuthority')

    // Directory API specific calls
    "/ill/directory/api/findSymbol"(controller: 'directoryApi', action:'findSymbol')
    "/ill/directory/api/addFriend"(controller: 'directoryApi', action:'addFriend')
    "/ill/directory/api/freshen"(controller: 'directoryApi', action:'freshen')

    group "/ill", {
	    // Generates the module descriptor template for the module
	    get "/moduleDescriptor/generate"(controller: 'moduleDescriptor', action:'generate')
	
		// Have deprecated this url, use /ill/swagger/ui instead 
		get "/swaggerUI"(redirect: [uri: '/ill/swagger/ui'])

		// Swagger
	    group "/swagger", {
		    // Swagger document, the grails swagger plugin caused to many problems, so we have built this one that works just from the annotations
		    get "/api"(controller: 'swaggerUI', action:'justFromAnnotations')

			// The swagger resources for display purposes
			// Ones we have modified from the distribution
	        get "/swagger-initializer.js"(uri: '/static/swaggerUI/swagger-initializer.js')

			// Files straight from the distribution			
	        get "/favicon.ico"(uri: '/static/swaggerUI/5.27.1/favicon-32x32.png')
	        get "/favicon-16x16.png"(uri: '/static/swaggerUI/5.27.1/favicon-16x16.png')
	        get "/favicon-32x32.png"(uri: '/static/swaggerUI/5.27.1/favicon-32x32.png')
	        get "/index.css"(uri: '/static/swaggerUI/5.27.1/index.css')
	        get "/swagger-ui.css"(uri: '/static/swaggerUI/5.27.1/swagger-ui.css')
	        get "/swagger-ui-bundle.js"(uri: '/static/swaggerUI/5.27.1/swagger-ui-bundle.js')
	        get "/swagger-ui-standalone-preset.js"(uri: '/static/swaggerUI/5.27.1/swagger-ui-standalone-preset.js')
			get "/ui"(uri: '/static/swaggerUI/5.27.1/index.html')
	    }
    }
	
    // For dynamically changing the logging level
    '/ill/logging'(controller:'logging', action:'index')

    // For testing the host lms
    '/ill/testHostLMS/acceptItem'(controller: 'testHostLMS', action: 'acceptItem')
    '/ill/testHostLMS/checkIn'(controller: 'testHostLMS', action: 'checkIn')
    '/ill/testHostLMS/checkOut'(controller: 'testHostLMS', action: 'checkOut')
    '/ill/testHostLMS/determineBestLocation'(controller: 'testHostLMS', action: 'determineBestLocation')
    '/ill/testHostLMS/validate'(controller: 'testHostLMS', action: 'validate')

    '/ill/testExternalSearch/locate'(controller: 'testExternalSearch', action: 'locate')
    '/ill/testRouting/findMoreSuppliers'(controller: 'testRouting', action: 'findMoreSuppliers')
	
    "500"(view: '/error')
    "404"(view: '/notFound')

    "/ill/template/createEditDetails" (controller: "template", action: "createEditDetails")
    '/ill/template'(resources: 'template')
  }
}
