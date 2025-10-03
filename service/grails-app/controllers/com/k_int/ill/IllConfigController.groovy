package com.k_int.ill;

// import com.k_int.web.toolkit.ConfigController;

import com.k_int.ill.logging.ContextLogging;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Api(value = "/ill/kiwt", tags = ["Ill Config"])
@OkapiApi(name = "kiwt")
// public class IllConfigController extends ConfigController {
@ExcludeFromGeneratedCoverageReport
public class IllConfigController {

  private static String raml_text = '''
#%RAML 1.0

title: ResourceSharing API
baseUri: https://github.com/openlibraryenvironment/mod-ill
version: v1

documentation:
  - title: mod-ill API
    content: This documents the API calls that can be made to query and manage resource sharing requests

types:
  PatronRequest: !include kiwt/config/schema/PatronRequest
  Status: !include kiwt/config/schema/Status
  RefdataValue: !include kiwt/config/schema/RefdataValue
  RefdataCategory: !include kiwt/config/schema/RefdataCategory
  StateModel: !include kiwt/config/schema/StateModel
  Shipment: !include kiwt/config/schema/Shipment
  ShipmentItem: !include kiwt/config/schema/ShipmentItem

traits:
  okapiService:
    headers:
      X-Okapi-Tenant:
        description: Okapi Tenant Id
      X-Okapi-Token:
        description: Okapi JWT

/ill:
  /patronrequests:
    get:
      is: [ okapiService ]
      description: List current patron requests
      responses:
        200:
          description: "OK"
    post:
      is: [ okapiService ]
      description: |
        Submit a new patron request. Normally a rota is NOT specified as the shared index service will generate a rota. One CAN be supplied however.
        RequestingInstitutionSymbol must be set for protocol messages to be sent. N.B. Tags and Refdata values can be specified as simple strings
        As a convenience, but this is not representative of the underlying domain model.
      body:
        application/json:
          type: PatronRequest
          example: |
          {
            "requestingInstitutionSymbol:'OCLC:PPPA',
            "title": "Brain of the firm",
            "author": "Beer, Stafford, A",
            "systemInstanceIdentifier": "01234",
            "patronReference":"Ian's test request",
            "patronIdentifier":"IANBARCODE",
            "isRequester":true,
            "rota":[
              {"directoryId":'OCLC:AVL', "rotaPosition":"0", "instanceIdentifier": "001TagFromMarc", "copyIdentifier":"COPYBarcode from 9xx"}
            ],
            "tags": [ 'RS-TESTCASE-1' ]
          }
    /{requestId}:
      get:
        is: [ okapiService ]
        description: Retrieve a specific patron request
      post:
        description: Update a specific patron request
  /refdata:
    is: [ okapiService ]
    description: List all refdata categories currently known
  /settings:
    /tenantSymbols
      get:
        is: [ okapiService ]
        description: Retrieve the library symbols registered for this tenant
      post:
        description: Register a symbol as "Belonging" to this tenant.
  /shipments:
    get:
      is: [ okapiService ]
      description: List current shipments
    post:
      is: [ okapiService ]
      description: Submit new or updated shipment - post without an ID to create new, with ID to update/patch
      body:
        application/json:
          type: Shipment
'''

    @ApiOperation(
        value = "The raml file for the applications",
        nickname = "raml",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @OkapiPermission(name = "raml", permissionGroup = PermissionGroup.READ)
    public def raml() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_RAML);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // yaml can be application/x-yaml or
        render ( text: raml_text )

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}
