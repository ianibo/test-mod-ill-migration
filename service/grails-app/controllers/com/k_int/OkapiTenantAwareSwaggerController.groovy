package com.k_int;

import com.k_int.ill.logging.ContextLogging;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;

import grails.gorm.multitenancy.CurrentTenant;
import grails.gorm.transactions.Transactional;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@CurrentTenant
@ExcludeFromGeneratedCoverageReport
public class OkapiTenantAwareSwaggerController<T> extends OkapiTenantAwareSwaggerGetController<T>  {

    /** The resource that has been saved */
    protected T savedResource = null;

    public OkapiTenantAwareSwaggerController(Class<T> resource, int maxRecordsPerPage = 1000) {
        this(resource, false, maxRecordsPerPage);
    }

    public OkapiTenantAwareSwaggerController(Class<T> resource, boolean readOnly, int maxRecordsPerPage = 1000) {
        super(resource, readOnly, maxRecordsPerPage);
    }

    @ApiOperation(
        value = "Creates a new record with the supplied data",
        nickname = "/",
        httpMethod = "POST"
    )
    @ApiResponses([
        @ApiResponse(code = 201, message = "Created")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            paramType = "body",
            required = true,
            allowMultiple = false,
            value = "The json record that is going to be used for creation",
            defaultValue = "{}",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "item", permissionGroup = PermissionGroup.WRITE)
    @Transactional
    public def save() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, resource.getSimpleName());
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_CREATE);
        ContextLogging.setValue(ContextLogging.FIELD_JSON, request.JSON);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Add the institution to the json
        updateJsonWithInstitution();

        // Give them a chance to modify the record and perform anything prior to update
        Map createContext = [ : ];
        preCreate(createContext);

        // Now do the work
        super.save();

        // If the update was successful, call the postUpdate
        if (response.getStatus()== response.SC_CREATED) {
            // The create was successful, so give the caller a chance to do anything extra
            postCreate(createContext);
        }

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

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
    @Transactional
    public def update() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, resource.getSimpleName());
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_UPDATE);
        ContextLogging.setValue(ContextLogging.FIELD_ID, params.id);
        ContextLogging.setValue(ContextLogging.FIELD_JSON, request.JSON);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Is it valid for us to update the record
        if (isValidForInstitution("update", params.id)) {
            // Give them a chance to modify the record and perform anything prior to update
            Map updateContext = [ : ];
            preUpdate(updateContext);

            // Now do the work
            super.update();

            // If the update was successful, call the postUpdate
            if (response.getStatus()== response.SC_OK) {
                // The update was successful, so give the caller a chance to do anything extra
                postUpdate(updateContext);
            }
        } else {
            // We need to set the response to invalid parameter
            response.status = 400;
        }

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @ApiOperation(
        value = "Deletes the record with the supplied identifier",
        nickname = "{id}",
        httpMethod = "DELETE"
    )
    @ApiResponses([
        @ApiResponse(code = 204, message = "Deleted")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "id",
            paramType = "path",
            required = true,
            allowMultiple = false,
            value = "The id of the record to be deleted",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "item", permissionGroup = PermissionGroup.ALL)
    @Transactional
    public def delete() {
        // Setup the variables we want to log
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, resource.getSimpleName());
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_DELETE);
        ContextLogging.setValue(ContextLogging.FIELD_ID, params.id);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        // Is it valid for us to delete the record
        if (isValidForInstitution("delete", params.id)) {
            // Now do the work
            super.delete();
        } else {
            // We need to set the response to invalid parameter
            response.status = 400;
        }

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    @Override
    protected T saveResource(T resource) {
        savedResource = resource;
        return(super.saveResource(resource));
    }

    /**
     * Override this method if you want to modify the json object prior to the create being executed
     * @param createContext A map that will be passed to the postCreate method
     */
    protected void preCreate(Map createContext) {
        // By default we do nothing
    }

    /**
     * Override this method if you wish to perform some work after the create has executed
     * @param createContext The map that was passed to the preCreate method
     */
    protected void postCreate(Map createContext) {
        // By default we do nothing
    }

    /**
     * Override this method if you want to modify the json object prior to the update occuring
     * @param updateContext A map that will be passed to the postUpdate method
     */
    protected void preUpdate(Map updateContext) {
        // By default we do nothing
    }

    /**
     * Override this method if you wish to perform some work after the update has occurred
     * @param updateContext The map that was passed to the preUpdate method
     */
    protected void postUpdate(Map updateContext) {
        // By default we do nothing
    }
}
