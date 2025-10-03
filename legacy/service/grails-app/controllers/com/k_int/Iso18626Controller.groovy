package com.k_int;

import com.k_int.ill.iso18626.Iso18626Message;
import com.k_int.ill.logging.ContextLogging;
import com.k_int.ill.protocols.iso18626.Iso18626MessageService;
import com.k_int.ill.protocols.iso18626.Iso18626Service;
import com.k_int.institution.InstitutionSetting;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.web.toolkit.settings.AppSetting;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 * External Read-Only APIs for resource sharing network connectivity
 */
@Slf4j
@CurrentTenant
@Api(value = "/ill/externalApi", tags = ["ISO18626"])
@OkapiApi(name = "iso18626")
@ExcludeFromGeneratedCoverageReport
// Note: We needed to extend from OkapiTenantAwareInstitutionController. so needed a domain,
// so just used InstitutionSetting,feel free to swap it out as it should never get used
public class Iso18626Controller extends OkapiTenantAwareInstitutionController<InstitutionSetting> {

	Iso18626Service iso18626Service;

    public Iso18626Controller() {
        // Have purposely made this domain different, so it will blow up if it ever uses it
        super(AppSetting);
    }

    @ApiOperation(
        value = "Receives an ISO18626 xml message",
        nickname = "iso18626",
        httpMethod = "POST",
        produces = "application/xml"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Created"),
        @ApiResponse(code = 400, message = "Bad Request")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            paramType = "body",
            required = true,
            allowMultiple = false,
            value = "The xml that contains the ISO18626 message",
            defaultValue = "{}",
            dataType = "string"
        )
    ])
    @OkapiPermission()
    public def iso18626() {
        ContextLogging.startTime();
        ContextLogging.setValue(ContextLogging.FIELD_ACTION, ContextLogging.ACTION_ISO18626);
        log.debug(ContextLogging.MESSAGE_ENTERING);

        String xml = null;

        try {
            // Read the xml
            xml = request.getReader().text;
            ContextLogging.setValue(ContextLogging.FIELD_XML, xml);

            // Create ourselves an Iso18626Nessage object
            Iso18626Message iso18626Message = iso18626Service.fromXml(xml);

            if (iso18626Message == null) {
                log.error("Failed to parse the iso18626 into an instance: " + xml);
                render(status: 500, text: "Error parsing recieved XML", contentType: "text/text", encoding: "UTF-8")
            } else {
				Iso18626MessageService iso18626MessageService = iso18626Service.getMessageService(iso18626Message);
                Iso18626Message iso18626ConfirmationMessage = iso18626MessageService.processMessage(
                    getInstitution(),
                    xml,
                    iso18626Message
                );
                if (iso18626ConfirmationMessage == null) {
                    render(status: 400, text: "Unable to determine ISO18626 message type from the xml", contentType: "text/text", encoding: "UTF-8");
                } else {
                    render(text: iso18626Service.toXml(iso18626ConfirmationMessage), contentType: "text/xml", encoding: "UTF-8");
                }
            }
        } catch (Exception e) {
            log.error("Exception thrown while trying to parse iso18626 message: " + xml, e);
            render(status: 500, text: "Error parsing recieved XML", contentType: "text/text", encoding: "UTF-8");
        }

        // Record how long it took
        ContextLogging.duration();
        log.debug(ContextLogging.MESSAGE_EXITING);
    }
}
