package com.k_int.ill;

import org.springframework.web.multipart.MultipartFile;

import com.k_int.GenericResult;
import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.ill.constants.ErrorCodes;
import com.k_int.ill.files.FileFetchResult;
import com.k_int.ill.files.FileService;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.patronRequest.PatronRequestCopyrightService;
import com.k_int.ill.patronRequest.PatronRequestDocumentService;
import com.k_int.ill.reporting.ReportService;
import com.k_int.ill.statemodel.ActionEvent;
import com.k_int.ill.statemodel.ActionResult;
import com.k_int.ill.statemodel.ActionService;
import com.k_int.ill.statemodel.Actions;
import com.k_int.ill.statemodel.StateModel;
import com.k_int.ill.utils.Json;
import com.k_int.institution.Institution;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;

import grails.converters.JSON;
import grails.gorm.multitenancy.CurrentTenant;
import grails.gorm.transactions.Transactional;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

@Slf4j
@CurrentTenant
@Api(value = "/ill/patronrequests", tags = ["Patron Request"])
@OkapiApi(name = "patronrequests")
@ExcludeFromGeneratedCoverageReport
public class PatronRequestController extends OkapiTenantAwareSwaggerController<PatronRequest>  {

    private static final String RESOURCE_PATRON_REQUEST = PatronRequest.getSimpleName();

	ActionService actionService;
    BatchService batchService;
	FileService fileService;
    OpenUrlService openUrlService;
	PatronRequestCopyrightService patronRequestCopyrightService;
	PatronRequestDocumentService patronRequestDocumentService;
	PatronRequestService patronRequestService;
    ReportService reportService;

	public PatronRequestController() {
		super(PatronRequest, 100);
	}

    @Override
    @ApiOperation(
        value = "Search with the supplied criteria",
        nickname = "/",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "term",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "The term to be searched for",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "match",
            paramType = "query",
            required = false,
            allowMultiple = true,
            value = "The properties the match is to be applied to",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "filters",
            paramType = "query",
            required = false,
            allowMultiple = true,
            value = "The filters to be applied",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "sort",
            paramType = "query",
            required = false,
            allowMultiple = true,
            value = "The properties to sort the items by",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "max",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Maximum number of items to return",
            dataType = "integer"
        ),
        @ApiImplicitParam(
            name = "perPage",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Number of items per page",
            dataType = "integer"
        ),
        @ApiImplicitParam(
            name = "offset",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Offset from the becoming of the result set to start returning results",
            dataType = "integer"
        ),
        @ApiImplicitParam(
            name = "page",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "The page you wnat the results being returned from",
            dataType = "integer"
        ),
        @ApiImplicitParam(
            name = "stats",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Do we return statistics about the search",
            dataType = "boolean"
        ),
        @ApiImplicitParam(
            name = "includeTerminal",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Do we return terminal requests in the search",
            dataType = "boolean"
        )
    ])
    @OkapiPermission(name = "index", permissionGroup = PermissionGroup.READ)
    public def index(Integer max) {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_SEARCH);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Use gorm criteria builder to always add your custom filter....
        Closure gormFilterClosure = {
			createAlias 'stateModel', 'sm'
			createAlias 'sm.states', 'sms', org.hibernate.sql.JoinType.INNER_JOIN 
			
            and {
                eqProperty('state', 'sms.state')
                eq('sms.isTerminal', false)
            }
        };

		// Do we want to include terminal requests
		if (params.includeTerminal != null) {
			Boolean includeTerminal = new Boolean(params.includeTerminal);
			if (includeTerminal) {
				// They want to include the terminal requests, so reset the filter closure 
				gormFilterClosure = null;
			}
		}

        // Now we can perform the lookup
        respond doTheLookup(gormFilterClosure);

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    /**
     *  Controller action that takes a POST containing a json payload with the following parameters
     *   {
     *     action:"StartRota",
     *     actionParams:{}
     *   }
     */
    @ApiOperation(
        value = "Performs the action the posted action for the given request",
        nickname = "{patronRequestId}/performAction",
        produces = "application/json",
        httpMethod = "POST",
        notes = 'Need to describe the actions here some how'
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "patronRequestId",
            paramType = "path",
            required = true,
            value = "The identifier of the request that the action is to be performed on",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "actionParameters",
            paramType = "body",
            required = false,
            value = "The parameters required to perform the action, in a json format",
            defaultValue = '''
{
    "action": "",
    "actionParams": {
    }
}
''',
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "performAction", permissionGroup = PermissionGroup.WRITE)
	public def performAction() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_PATRON_REQUEST);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_PERFORM_ACTION);
        ContextLogging.setValue(ContextLogging.FIELD_JSON, request.JSON);
        log.debug(ContextLogging.MESSAGE_ENTERING);

		def result = [ : ]
        if (params.patronRequestId) {
            if (request.JSON != null) {
				PatronRequest.withTransaction { tstatus ->
                    // Is the request for the users institution
                    if (actionService.isRequestForInstitution(params.patronRequestId, getInstitution())) {
                        // Execute the action
                        result = actionService.executeAction(params.patronRequestId, request.JSON.action, request.JSON.actionParams, true);
                        response.status = (result.actionResult == ActionResult.SUCCESS ? 200 : (result.actionResult == ActionResult.INVALID_PARAMETERS ? 400 : 500));

                        // We do not want to pass the internal action result back to the caller, so we need to remove it
                        result.remove('actionResult');
                    } else {
                        // No it wasn't
                        log.error("Request id not valid for institution");
                        result.actionResult = ActionResult.INVALID_PARAMETERS;
                        response.status = 400;
                    }
				}
            } else {
                log.error("No json supplied to action request " + params.patronRequestId);
                result.actionResult = ActionResult.INVALID_PARAMETERS;
                response.status = 400;
            }
        } else {
            log.error("No request id supplied to perform an action on a request");
            result.actionResult = ActionResult.INVALID_PARAMETERS;
            response.status = 400;
        }
		log.debug("PatronRequestController::performAction exiting");
		render result as JSON

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    /**
     * Allows a limited number of fields on a request to be updated, why isn't this implemented as an action,
     * to all intents and purposes I will implement this as an action so it should be straight forward to swap it over
     */
    @ApiOperation(
        value = "Updates the record with the supplied data",
        nickname = "{id}",
        produces = "application/json",
        httpMethod = "PUT"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "id",
            paramType = "path",
            required = true,
            allowMultiple = false,
            value = "The id of the record to be updated",
            dataType = "string"
        ),
        @ApiImplicitParam(
            paramType = "body",
            required = true,
            allowMultiple = false,
            value = "The json record that we are going to update the current record with",
            defaultValue = "{}",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "item", permissionGroup = PermissionGroup.WRITE)
    @Override
    public def update() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_PATRON_REQUEST);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_UPDATE);
        ContextLogging.setValue(ContextLogging.FIELD_JSON, request.JSON);
        ContextLogging.setValue(ContextLogging.FIELD_ID, params.id);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        def result = [:]
        if ( params.id ) {
            PatronRequest.withTransaction { tstatus ->
                // Is the request for the users institution
                if (actionService.isRequestForInstitution(params.id, getInstitution())) {
					// If the json body just has the tags property then we just treat this as a standard update
					if (justTags()) {
						// just call update on the parent class
						super.update();
					} else {
	                    // Execute the edit action
	                    result = actionService.executeAction(params.id, Actions.ACTION_REQUESTER_EDIT, request.JSON);
	                    response.status = (result.actionResult == ActionResult.SUCCESS ? 200 : (result.actionResult == ActionResult.INVALID_PARAMETERS ? 400 : 500));
	
	                    // We do not want to pass the internal action result back to the caller, so we need to remove it
	                    result.remove('actionResult');
					}
                } else {
                    // No it wasn't
                    log.error("Request id not valid for institution, user institution: " + getInstitution());
                    result.actionResult = ActionResult.INVALID_PARAMETERS;
                    response.status = 400;
                }
            }
        }

        // Finally render the result
        render result  as JSON;

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

	/**
	 * Inspects the supplied json to see if it only contains tags
	 * @return true if the json only contains or falise if it does not or there is no supplied json
	 */
	private boolean justTags() {
		boolean onlyTags = false;

		// Do we have any json
		if (request.JSON != null) {
			// Have we been supplied tags
			if (request.JSON.tags != null) {
				// Are tags the only property supplied
				if (request.JSON.size() == 1) {
					// We just have tags
					onlyTags = true;
				}
			}
		}

		// Let the caller know the result
		return(onlyTags);
	}

    @ApiOperation(
        value = "New request details",
        nickname = "newRequestDetails",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @OkapiPermission(name = "newRequestDetails", permissionGroup = PermissionGroup.READ)
    public def newRequestDetails() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_NEW_REQUEST_DETAILS);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Get hold of the details from the patron request service
        render Json.toJson(patronRequestService.createDetails(getInstitution())), contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Performs the specified action against the specified requests",
        nickname = "bulkAction",
        produces = "application/json",
        httpMethod = "POST"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "actionParameters",
            paramType = "body",
            required = false,
            value = "The parameters required to perform the action, in a json format",
            defaultValue = '''
{
    "requestIds": [ ],
    "action": "",
    "actionParams": {
    }
}
''',
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "bulkAction", permissionGroup = PermissionGroup.WRITE)
  	public def bulkAction() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_PATRON_REQUEST);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_BULK_ACTION);
        ContextLogging.setValue(ContextLogging.FIELD_JSON, request.JSON);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // We will return an array of results
        Map result = [ : ];
        List successfulRequests = [ ];
        List failedRequests = [ ];
        result.successful = successfulRequests;
        result.failed = failedRequests;

        // The request ids are part of the body
        def requestIds = request.JSON?.requestIds;

        // Set the action action in the result
        result.action = request.JSON?.action;

        // Have we been supplied any request identifiers to action
        if (requestIds && (requestIds instanceof List<String>)) {
            // Have we been supplied an action
            if (request.JSON?.action) {
                // Lookup the action event
                ActionEvent actionEvent = ActionEvent.lookup(request.JSON.action);

                // Is it valid to perform a bulk action
                if ((actionEvent != null) && actionEvent.isAction && actionEvent.isAvailableForBulk) {
                    // Grab the users institution
                    Institution institution = getInstitution();

                    // We have so loop through them
                    requestIds.each { String requestId ->
                        // Start a new transaction for each request
                        PatronRequest.withTransaction { tstatus ->
                            Map actionRequestResult;
                            boolean successful = false;

                            // Is the request for the users institution
                            if (actionService.isRequestForInstitution(params.patronRequestId, getInstitution())) {
                                // Execute the action
                                actionRequestResult = actionService.executeAction(requestId, request.JSON.action, request.JSON.actionParams);

                                // Remove the ActionResult, once we know we have been successful or not
                                successful = (actionRequestResult.actionResult == ActionResult.SUCCESS);
                                actionRequestResult.remove('actionResult');
                            } else {
                                actionRequestResult = [
                                    message : 'Unable to find request with id: ' + patronRequestId
                                ];
                            }

                            // Add the request id to the result
                            actionRequestResult.requestId = requestId;

                            // Add the result to our result list
                            if (successful) {
                                successfulRequests.add(actionRequestResult);
                            } else {
                                failedRequests.add(actionRequestResult);
                            }
                        }
                    }
                } else {
                    result.error = "The action supplied to perform the bulk action is not applicable";
                    response.status = 400;
                }
            } else {
                result.error = "No action supplied";
                response.status = 400;
            }
        } else {
            result.error = "No request identifiers supplied to bulk action";
            response.status = 400;
        }

        // Render the result
        render result as JSON;

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
	}

    /**
     * list the valid actions for this request
     */
    @ApiOperation(
        value = "List the actions that are available for the request",
        nickname = "{patronRequestId}/validActions",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "patronRequestId",
            paramType = "path",
            required = true,
            value = "The identifier of the request that the valid actions are required for",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "validActions", permissionGroup = PermissionGroup.WRITE)
	public def validActions() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_PATRON_REQUEST);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_VALID_ACTIONS);
        ContextLogging.setValue(ContextLogging.FIELD_ID, params.patronRequestId);
        log.debug(ContextLogging.MESSAGE_ENTERING);

		def result = [:];

		if ( params.patronRequestId ) {
			PatronRequest patronRequest = PatronRequest.get(params.patronRequestId)

			if (patronRequest != null) {
                // Is the request for the users institution
                if (actionService.isRequestForInstitution(patronRequest, getInstitution())) {
                    // It is
                    ContextLogging.setValue(ContextLogging.FIELD_HRID, patronRequest.hrid);
    				result.actions = actionService.getValidActions(patronRequest);
                } else {
                    // Request does not belong to the users institution
                    result.actions = [ ];
                    result.message = "Unable to locate request";
                }
			} else {
				result.actions = [ ];
				result.message = "Unable to locate request";
			}
		} else {
			result.actions=[];
			result.message="No ID provided in call to validActions";
		}

		render result as JSON

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
	}

    /**
     * list the close states that are valid for for this request
     */
    @ApiOperation(
        value = "List the close states that are valid for the request",
        nickname = "{patronRequestId}/manualCloseStates",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "patronRequestId",
            paramType = "path",
            required = true,
            value = "The identifier of the request that the close states are required for",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "manualCloseStates", permissionGroup = PermissionGroup.WRITE)
    public def manualCloseStates() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_PATRON_REQUEST);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_MANUAL_CLOSE_STATES);
        ContextLogging.setValue(ContextLogging.FIELD_ID, params.patronRequestId);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        def result = [ ];

        // Cannot proceed if we do not have a requestId
        if (params.patronRequestId) {
            // Grab the request
            PatronRequest patronRequest = PatronRequest.get(params.patronRequestId);

            // Did we find the request
            if (patronRequest != null) {
                // Is the request for the users institution
                if (actionService.isRequestForInstitution(patronRequest, getInstitution())) {
                    // Add the hrid to the logging
                    ContextLogging.setValue(ContextLogging.FIELD_HRID, patronRequest.hrid);

                    // Get hold of the states from the model
                    result = StateModel.getVisibleTerminalStates(patronRequest.stateModel.shortcode);
                }
            }
        }

        render result as JSON

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    /**
     * Generates a batch from the passed in list of filters
     */
    @ApiOperation(
        value = "Generates the pick list batch based on the passed in filter",
        nickname = "generatePickListBatch",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "term",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "The term to be searched for",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "filters",
            paramType = "query",
            required = false,
            allowMultiple = true,
            value = "The filters to be applied",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "match",
            paramType = "query",
            required = false,
            allowMultiple = true,
            value = "The properties the match is to be applied to",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "generatePickListBatch", permissionGroup = PermissionGroup.WRITE)
    public def generatePickListBatch() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_PATRON_REQUEST);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_GENERATE_PICK_LIST_BATCH);
        ContextLogging.setValue(ContextLogging.FIELD_TERM, params.term);
        ContextLogging.setValue(ContextLogging.FIELD_FIELDS_TO_MATCH, params.match);
        ContextLogging.setValue(ContextLogging.FIELD_FILTERS, params.filters);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        List<String> filters = addInstitutionFilterIfNeeded(getParamList("filters"));
        List<String> searchFields = getParamList("match");
        String term = params.term;
        Map result = [ : ];

        Batch.withTransaction { tstatus ->
            // Generate the batch for the pick list
            result = batchService.generatePickListBatchFromFilter(
                getInstitution(),
                term,
                searchFields,
                filters,
                reportService.getMaxRequestsInPullSlipManual(getInstitution()),
                "User generated pick list:",
                true
            );
        }

        // Give the result back to the caller
        render result as JSON, status: result.error ? 400 : 200, contentType: "application/json";

        // Record how long it took and the created batch id
        ContextLogging.setValue(ContextLogging.FIELD_ID, result.batchId);
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    /**
     * Marks the requests in a batch as being printed if that action is valid for the request
     * This will return the following arrays:
     *  1. The request ids that were successfully marked as printed
     *  2. The request ids that failed to be marked as printed that were valid to mark as printed
     *  3. The requests ids that were not valid to mark as printed
     */
    @ApiOperation(
        value = "Actions the requests in the batch as printed",
        nickname = "markBatchAsPrinted",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Invalid parameters have been supplied")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "batchId",
            paramType = "query",
            required = true,
            allowMultiple = false,
            value = "The batch to mark the requests it contains as printed",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "markBatchAsPrinted", permissionGroup = PermissionGroup.WRITE)
    public def markBatchAsPrinted() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_PATRON_REQUEST);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_MARK_BATCH_AS_PRINTED);
        ContextLogging.setValue(ContextLogging.FIELD_ID, params.batchId);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        Map result = [ : ];
        int resultStatus = 200;

        // Have we been supplied a batch id
        if (params.batchId) {
            // Good start, so get hod of the batch
            Batch batch = Batch.get(params.batchId);

            // Did we find the batch
            if (batch) {
                // Is the batch for this institution
                if (batch.institution.id.equals(getInstitutionId())) {
                    // Start a transaction to perform the updates against
                    PatronRequest.withTransaction { tstatus ->
                        result = batchService.markRequestsInBatchAsPrinted(batch);
                    }
                } else {
                    // No we did not
                    result.error = "unknown batch id supplied: " + params.batchId;
                    resultStatus = 400;
                }
            } else {
                // No we did not
                result.error = "unknown batch id supplied: " + params.batchId;
                resultStatus = 400;
            }
        } else {
            result.error = "No batch id was supplied";
            resultStatus = 400;
        }

        render result as JSON, status: resultStatus, contentType: "application/json";

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Uploads a document to be associated with the request",
        nickname = "{patronRequestId}/uploadDocument",
        produces = "application/json",
        consumes = "multipart/form-data",
        httpMethod = "POST"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Invalid parameters have been supplied")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "patronRequestId",
            paramType = "path",
            required = true,
            allowMultiple = false,
            value = "The request to associate the document with",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "description",
            paramType = "formData",
            allowMultiple = false,
            required = true,
            value = "The description for this file",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "file",
            paramType = "formData",
            allowMultiple = false,
            required = true,
            value = "The file to be uploaded",
            dataType = "file"
        )
    ])
    @OkapiPermission(name = "uploadDocument", permissionGroup = PermissionGroup.WRITE)
    @Transactional
	public def uploadDocument() {
		GenericResult result = new GenericResult();
		PatronRequest patronRequest = fetchRequest(true, result);

		// Did we find the request 		
		if (patronRequest != null) {
			def file = params.file;
			if ((file == null) || !(file instanceof MultipartFile)) {
				result.error("No file has been submitted");
				if (file != null) {
					result.addMessage("File class type: " + file.getClass().getName());
				}
			} else {
				// Let us attempt to create the file
				patronRequestDocumentService.add(
					patronRequest,
					file,
					params.description,
					result
				);
			}
		}

		// Give the result to the caller
        render Json.toJson(result), contentType: "application/json";
	}
	
    @ApiOperation(
        value = "Return the document associated with the request if the copyright has been agrred to",
        nickname = "{patronRequestId}/viewDocument",
        produces = "application/octet",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Invalid parameters have been supplied")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "patronRequestId",
            paramType = "path",
            required = true,
            allowMultiple = false,
            value = "The request to obtain the document from",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "position",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "The document position",
            dataType = "number"
        )
    ])
    @OkapiPermission()
    @Transactional
	public def viewDocument() {
		GenericResult result = new GenericResult();
		FileFetchResult fileFetchResult = null;
		PatronRequest patronRequest = fetchRequest(false, result);

		// Did we find the request 		
		if (patronRequest != null) {
			int position = 0;

			// Have they supplied as a position parameter			
			if (params.position != null) {
				// They have so turn it into a number
				try {
					position = Integer.parseInt(params.position);
					log.debug("Converted position is " + position);
				} catch (Exception e) {
					// We ignore any exception any just return them the document at position 0
					log.debug("Exception thrown while trying to convert string (" + params.position + ") to int: " + e.getMessage());
				}
			}
			
			// Let us attempt to get hold of the document
			fileFetchResult = patronRequestDocumentService.download(
				patronRequest,
				position,
				result
			);
		}

		// Did we obtain the document
		if ((fileFetchResult == null) || (result.result != ActionResult.SUCCESS)) {
			// Give the error back to the caller
	        render Json.toJson(result), contentType: "application/json";
		} else {
			// Success so render the document back
			render file: fileFetchResult.inputStream, contentType: fileFetchResult.contentType;
		}
	}

    @ApiOperation(
        value = "Return the copyright notice associated with the request",
        nickname = "{patronRequestId}/fetchCopyright",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Invalid parameters have been supplied")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "patronRequestId",
            paramType = "path",
            required = true,
            allowMultiple = false,
            value = "The request to obtain the copyright notice for",
            dataType = "string"
        )
    ])
    @OkapiPermission()
	public def fetchCopyright() {
		FetchPatronRequestCopyrightResult result = new FetchPatronRequestCopyrightResult(params.patronRequestId);
		PatronRequest patronRequest = fetchRequest(true, result);

		// Did we find the request 		
		if (patronRequest != null) {
			patronRequestCopyrightService.fetchCopyrightMessage(
				patronRequest,
				result
			);
		}

		// Give the result to the caller
        render Json.toJson(result), contentType: "application/json";
	}
		
    @ApiOperation(
        value = "Agree to the copyright conditions",
        nickname = "{patronRequestId}/agreeCopyright",
        produces = "application/json",
        httpMethod = "PUT"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Invalid parameters have been supplied")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "patronRequestId",
            paramType = "path",
            required = true,
            allowMultiple = false,
            value = "The request that we agree to the copyright terms",
            dataType = "string"
        )
    ])
    @OkapiPermission()
    @Transactional
	public def agreeCopyright() {
		GenericResult result = new GenericResult();
		PatronRequest patronRequest = fetchRequest(false, result);

		// Did we find the request 		
		if (patronRequest != null) {
			patronRequestCopyrightService.agreeCopyright(
				patronRequest,
				result
			);
		}

		// Give the result to the caller
        render Json.toJson(result), contentType: "application/json";
	}

	/**
	 * Fetches the patron request from the passed in parameters	
	 * @param checkInstitution Check to see if the request belongs to the users institution
	 * @return The found request or null if the request cannot be found
	 */
	private PatronRequest fetchRequest(boolean checkInstitution, GenericResult result) {

		PatronRequest patronRequest = null;

		// Cannot proceed if we do not have a requestId
		if (params.patronRequestId) {
			// Grab the request
			patronRequest = PatronRequest.get(params.patronRequestId);

			// Did we find the request
			if (patronRequest != null) {
				// Add the hrid to the logging
				ContextLogging.setValue(ContextLogging.FIELD_HRID, patronRequest.hrid);

				// Do we need to check it is for an institution
				if (checkInstitution) {
					// Is the request for the users institution
					if (!actionService.isRequestForInstitution(patronRequest, getInstitution())) {
						// The request is not for the users institution, so effectively we have not found it
						patronRequest = null;
					}
				}
			}
		}

		// Did we find the request
		if (patronRequest == null) {
			// We did not. do we have a result object
			if (result != null) {
				// We have a result object, so mark it as an error 
				result.error("Unable to find request with id: " + params.patronRequestId, ErrorCodes.ERROR_NO_PATRON_REQUEST);
			}
		}

		// Return the result to the caller
		return(patronRequest);
	}

    /**
     * Receives an OpenURL either version 0.1 or 1.0
     */
    @ApiOperation(
        value = "Interprets an OpenUrl version 0.1 or 1.0",
        nickname = "openURL",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "req.emailAddress",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: EMail address of the patron",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "req.id",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Id of this request",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.artnum",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Article number",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.atitle",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Article title",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.au",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Author",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.aufirst",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Author first name",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.auinitl",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: First author initials",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.auinit",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Author initials",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.auinitm",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Author middle initial",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.aulast",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Author last name",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.bici",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Bici",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.btitle",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Book title",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.coden",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Coden",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.creator",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Creator",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.edition",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Edition",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.eissn",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: EISSN",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.genre",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: ",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.id",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Identifier of the item",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.identifier.illiad",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Illiad identifier",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.isbn",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: ISBN",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.issn",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: ISSN",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.issue",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Issue",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.jtitle",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Journal title",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.epage",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Last page number",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.spage",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Start page",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.pages",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Number of pages",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.part",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Part",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.date",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Publication dste",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.place",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Publication place",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.pub",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Publisher",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.quarter",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Quarter",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.sici",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: SICI",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.ssn",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: SSN",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.title",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Title",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "rft.volume",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Volume",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "svc.neededBy",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Needed by",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "svc.note",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Note",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "svc.pickupLocation",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Pickup location",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "svc.id",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Service type",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "artnum",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v1.0: Article number",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "aufirst",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Author first name",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "auinitl",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Author first initial",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "auinit",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Author middle initial",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "aulast",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Author last name",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "auinitm",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Author middle initial",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "bici",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: BICI",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "coden",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Coden",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "genre",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Genre",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "issn",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: ISSN",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "eissn",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: EISSN",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "isbn",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: ISBN",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "issue",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Issue",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "epage",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: End page",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "spage",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Start page",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "pages",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Number of pages",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "part",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Part",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "pickupLocation",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Pickup location",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "date",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Publication date",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "quarter",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Quarter",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "ssn",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: SSN",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "sici",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: SICI",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "sharedIndexId",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: sharedIndexId",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "title",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Title",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "stitle",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Abbreviated title",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "atitle",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Article Title",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "volume",
            paramType = "query",
            allowMultiple = false,
            required = false,
            value = "v0.1: Volume",
            dataType = "string"
        )
    ])
    @OkapiPermission()
    public def openURL() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_PATRON_REQUEST);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_OPEN_URL);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Maps an OpenURL onto a request, originally taken from here https://github.com/openlibraryenvironment/listener-openurl/blob/master/src/ReshareRequest.js
        GenericResult result = openUrlService.mapToRequest(params) ;
        response.status = 200;

        render Json.toJson(result), contentType: "application/json";

        // Record how long it took and the request id
        ContextLogging.setValue(ContextLogging.FIELD_ID, result.id);
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}
