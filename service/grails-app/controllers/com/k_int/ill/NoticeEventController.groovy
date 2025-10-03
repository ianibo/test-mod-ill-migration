package com.k_int.ill;

import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.permissions.OkapiApi;

import grails.gorm.multitenancy.CurrentTenant;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@CurrentTenant
@Api(value = "/ill/noticeEvent", tags = ["Notice Events"])
@OkapiApi(name = "noticeEvent")
@ExcludeFromGeneratedCoverageReport
public class NoticeEventController extends OkapiTenantAwareSwaggerController<NoticeEvent> {

    static responseFormats = ['json', 'xml'];

    public NoticeEventController() {
        super(NoticeEvent)
    }
}
