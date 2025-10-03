package com.k_int.ill;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.ill.files.FileDefinition;
import com.k_int.ill.files.FileDefinitionCreateResult;
import com.k_int.ill.files.FileFetchResult;
import com.k_int.ill.files.FileService;
import com.k_int.ill.files.FileType;
import com.k_int.ill.logging.ContextLogging;
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
@Api(value = "/ill/fileDefinition", tags = ["FileDefinition"])
@OkapiApi(name = "fileDefinition")
@ExcludeFromGeneratedCoverageReport
public class FileDefinitionController extends OkapiTenantAwareSwaggerController<FileDefinition>  {

    private static final String RESOURCE_FILE_DEFINITION = FileDefinition.getSimpleName();

    /** The service that handles the storage and retrieval of files */
    FileService fileService;

  	public FileDefinitionController() {
		super(FileDefinition)
	}

    @ApiOperation(
        value = "File upload",
        nickname = "fileUpload",
        httpMethod = "POST",
        consumes = "multipart/form-data",
        produces = "application/json"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "fileType",
            paramType = "formData",
            allowMultiple = false,
            allowableValues = "LOGO,REPORT_DEFINITION,REPORT_OUTPUT",
            required = true,
            value = "The type of file being uploaded",
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
    @OkapiPermission(name = "fileUpload", permissionGroup = PermissionGroup.WRITE)
    public def fileUpload() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_FILE_DEFINITION);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_FILE_UPLOAD);

        FileDefinitionCreateResult result = null;
        def file = params.file;
        if (params.fileType) {
            FileType fileType = convertToFileType(params.fileType);
            if (fileType == null) {
                result = new FileDefinitionCreateResult();
                result.error = "Unkown value \"${params.fileType}\" for fileType has been supplied";
            } else {
                FileDefinition.withTransaction { tstatus ->
                    result = fileService.create(getInstitution(), fileType, params.description, file);
                }
            }
        } else {
            result = new FileDefinitionCreateResult();
            result.error = "File type must be supplied";
        }
        Map jsonResult = [ id : result.fileDefinition?.id, error : result.error ];
        render jsonResult as JSON

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }

    /**
     * Converts the supplied string to a FileType
     * @param fileTypeAsString The string yo be converted
     * @return The FileType the string represents or null if there is not an appropriate FileType for it to map onto
     */
    private FileType convertToFileType(String fileTypeAsString) {
        FileType fileType = null;
        try {
            // Convert it to the enum of FileType
            fileType = fileTypeAsString as FileType;
        } catch(Exception e) {
            // Do nothing as null will just be returned
        }

        return(fileType);
    }

    @ApiOperation(
        value = "File download",
        nickname = "fileDownload/{fileId}",
        httpMethod = "GET",
        produces = "application/octet"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 404, message = "File not found"),
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "fileId",
            paramType = "path",
            allowMultiple = false,
            required = true,
            value = "The id of the file to be retrieved",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "fileDownload", permissionGroup = PermissionGroup.WRITE)
    public def fileDownload() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_RESOURCE, RESOURCE_FILE_DEFINITION);
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_FILE_DOWNLOAD);

        String fileId = params.fileId;
        FileFetchResult result = fileService.fetch(getInstitution(), fileId);
        if (result.inputStream != null) {
            // Success so render the stream back
            render file: result.inputStream, contentType: result.contentType;
        } else {
            // Just render the error
            Map renderResult = [ error: result.error ];
            render renderResult as JSON, status: 404, contentType: "application/json";
        }

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}
