package com.k_int.ill;

import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.statemodel.ActionEvent;
import com.k_int.ill.statemodel.Actions;
import com.k_int.ill.statemodel.AvailableAction;
import com.k_int.ill.statemodel.GraphVizService;
import com.k_int.ill.statemodel.StateModel;
import com.k_int.ill.statemodel.StatusService;
import com.k_int.ill.statemodel.Transition;
import com.k_int.okapi.OkapiTenantAwareController;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;

import grails.converters.JSON;
import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Slf4j
@CurrentTenant
@Api(value = "/ill", tags = ["Available Action"])
@OkapiApi(name = "availableAction")
@ExcludeFromGeneratedCoverageReport
public class AvailableActionController extends OkapiTenantAwareController<AvailableAction>  {

    private static final String RESOURCE_AVAILABLE_ACTION = AvailableAction.getSimpleName();

    GraphVizService graphVizService;
    StatusService statusService;

	public AvailableActionController() {
		super(AvailableAction)
	}

    /**
     * Gets hold of the states an action can be called from
     * Example call: curl --http1.1 -sSLf -H "accept: application/json" -H "Content-type: application/json" -H "X-Okapi-Tenant: diku" --connect-timeout 10 --max-time 30 -XGET http://localhost:8081/ill/availableAction/toStates/Responder/respondYes
     * @return the array of states the action can be called from
     */
    @ApiOperation(
        value = "List the from states that an action can be triggered from",
        nickname = "availableAction/fromStates/{stateModel}/{actionCode}",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "stateModel",
            paramType = "path",
            required = true,
            value = "The state model the action is applicable for",
            dataType = "string",
            defaultValue = "PatronRequest"
        ),
        @ApiImplicitParam(
            name = "actionCode",
            paramType = "path",
            required = true,
            value = "The action that you want to know which states a re applicable for it",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "fromStates",  permissionGroup = PermissionGroup.READ)
	public def fromStates() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_AVAILABLE_ACTION);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_FROM_STATES);
        log.debug(ContextLogging.MESSAGE_ENTERING);

  		def result = [ : ]
		if (request.method == 'GET') {
			if (params.stateModel && params.actionCode) {
                result.fromStates = AvailableAction.getFromStates(params.stateModel, params.actionCode);
			} else {
				result.message = "Need to supply both action and state model , to see what states this action could transition from";
			}
		} else {
			request.message("Only GET requests are supported");
		}
		render result as JSON;

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

	/**
	 * Gets hold of the states an action can lead to
	 * Example call: curl --http1.1 -sSLf -H "accept: application/json" -H "Content-type: application/json" -H "X-Okapi-Tenant: diku" --connect-timeout 10 --max-time 30 -XGET http://localhost:8081/ill/availableAction/toStates/Responder/respondYes
	 * @return the array of states a request can end up in after the action has been performed
	 */
    @ApiOperation(
        value = "List the states that a request can end up in after the action has been performed",
        nickname = "availableAction/toStates/{stateModel}/{actionCode}",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "stateModel",
            paramType = "path",
            required = true,
            value = "The state model the action is applicable for",
            dataType = "string",
            defaultValue = "PatronRequest"
        ),
        @ApiImplicitParam(
            name = "actionCode",
            paramType = "path",
            required = true,
            value = "The action that you want to know which states a request could move onto after the action has been performed",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "traverseHierarchy",
            paramType = "query",
            required = true,
            value = "Do we look at the state models we have inherited about",
            dataType = "boolean",
            defaultValue = "true"
        )
    ])
    @OkapiPermission(name = "toStates",  permissionGroup = PermissionGroup.READ)
	public def toStates() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_AVAILABLE_ACTION);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_TO_STATES);
        log.debug(ContextLogging.MESSAGE_ENTERING);

		def result = [ : ]
		if (request.method == 'GET') {
			if (params.stateModel && params.actionCode) {
                boolean traverseHierarchy = false;
                if (params.traverseHierarchy) {
                    traverseHierarchy = params.traverseHierarchy.toBoolean();
                }
                List<Transition> transitions = statusService.possibleActionTransitionsForModel(StateModel.lookup(params.stateModel), ActionEvent.lookup(params.actionCode), traverseHierarchy);
                result.toStates = [ ];
                transitions.forEach{transition ->
                    Map coreDetails = [ : ];
                    coreDetails.fromStatus = transition.fromStatus.code;
                    coreDetails.action = transition.actionEvent.code;
                    coreDetails.qualifier = transition.qualifier;
                    coreDetails.toStatus = transition.toStatus.code;
                    result.toStates.add(coreDetails);
                };
			} else {
				result.message = "Need to supply both action and state model , to see what states this action could transition to";
			}
		} else {
			request.message("Only GET requests are supported");
		}
		render result as JSON;

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

	/**
	 * Builds a graph of the requested state model, excluding any actions / events specified in the query
	 * It saves the .dot and .svg files locally in a directory called D:/Temp/graphviz
	 * Example call: curl --http1.1 -sSLf -H "accept: image/png" -H "X-Okapi-Tenant: diku" --connect-timeout 10 --max-time 300 -XGET http://localhost:8081/ill/availableAction/createGraph/PatronRequest?height=4000\&excludeActions=requesterCancel,manualClose
	 * @return The .dot file that represents the graph
	 */
    @ApiOperation(
        value = "Builds a graph of the requested state model using the DOT language ",
        nickname = "availableAction/createGraph/{stateModel}",
        produces = "text/plain",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "stateModel",
            paramType = "path",
            required = true,
            value = "The state model the graph is for",
            dataType = "string",
            defaultValue = "PatronRequest"
        ),
        @ApiImplicitParam(
            name = "excludeActions",
            paramType = "query",
            required = false,
            value = "A comma separated list of actions that are to be excluded from the chart",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "excludeProtocolActions",
            paramType = "query",
            required = false,
            value = "Do we exclude protocol actions or not",
            dataType = "boolean",
            defaultValue = "false"
        ),
        @ApiImplicitParam(
            name = "outputFormat",
            paramType = "query",
            required = false,
            value = "The format the output the format should be in",
            dataType = "string",
            defaultValue = "dot",
            allowableValues = "dot,png,svg"
        ),
        @ApiImplicitParam(
            name = "height",
            paramType = "query",
            required = false,
            value = "The height of the graph",
            dataType = "integer",
            defaultValue = "2000"
        ),
        @ApiImplicitParam(
            name = "traverseHierarchy",
            paramType = "query",
            required = true,
            value = "Do we look at the state models we have inherited about",
            dataType = "boolean",
            defaultValue = "true"
        )
    ])
    @OkapiPermission(name = "createGraph",  permissionGroup = PermissionGroup.READ)
	public def createGraph() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_AVAILABLE_ACTION);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_CREATE_GRAPH);
        log.debug(ContextLogging.MESSAGE_ENTERING);

		// Remove messagesAllSeen, messageSeen and message as they occur for all states
		// We also only want to keep those for the state model we are interested in
		String nameStartsWith = "action" + params.stateModel.capitalize();
		List<String> ignoredActions = [
            Actions.ACTION_MANUAL_CLOSE,
            Actions.ACTION_MESSAGES_ALL_SEEN,
            Actions.ACTION_MESSAGE_SEEN,
            Actions.ACTION_MESSAGE,
            Actions.ACTION_INCOMING_ISO18626, 
			Actions.ACTION_ISO18626_NOTIFICATION,
			Actions.ACTION_REQUESTER_ISO18626_STATUS_REQUEST_RESPONSE,
			Actions.ACTION_REQUESTER_ISO18626_REQUEST_RESPONSE
        ];

        boolean traverseHierarchy = false;
        if (params.traverseHierarchy) {
            traverseHierarchy = params.traverseHierarchy.toBoolean();
        }

		if (params.excludeActions) {
			// They have specified some additional actions that should be ignored
			ignoredActions.addAll(params.excludeActions.split(","));
		}

		// Were we passed a height in the parameters
		int height = 2000;
		if (params.height) {
			try {
				height = params.height as int;
			} catch (Exception e) {
			}
		}

        // Do we want to include the protocol actions
        Boolean includeProtocolActions = !((params.excludeProtocolActions == null) ? false : params.excludeProtocolActions.toBoolean());

        // Ensure the output format is a string
        String parsedOutputFormat = params.outputFormat ? params.outputFormat.toString(): null;

        // Determine the content type
        String contentType = "text/plain";
        if (parsedOutputFormat) {
            switch (params.outputFormat) {
                case GraphVizService.FORMAT_SVG:
                    contentType =  "image/svg+xml";
                    break;

                case GraphVizService.FORMAT_PNG:
                    contentType =  "image/png";
                    break;

                case GraphVizService.FORMAT_DOT:
                default:
                    // Already set as text/plain when the variable was initialised
                    break;
            }
        }

        // Send it straight to the output stream, in doing so we need specify the headers first
        response.status = 200;
        response.setContentType(contentType);
        OutputStream outputStream = response.getOutputStream();

		// Tell it to build the graph, it should return the dot file in the output stream
		graphVizService.generateGraph(
            params.stateModel,
            includeProtocolActions,
            ignoredActions,
            outputStream,
            parsedOutputFormat,
            height,
            traverseHierarchy
        );

		// Hopefully we have what we want in the output stream
		outputStream.flush();


        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
	}
}
