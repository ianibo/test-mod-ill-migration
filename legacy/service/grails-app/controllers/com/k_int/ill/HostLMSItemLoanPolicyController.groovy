package com.k_int.ill;

import com.k_int.permissions.OkapiApi;

import grails.gorm.multitenancy.CurrentTenant;
import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@CurrentTenant
@Api(value = "/ill/hostLMSItemLoanPolicy", tags = ["Host LMS Item Loan Policies"])
@OkapiApi(name = "hostLMSItemLoanPolicy")
@ExcludeFromGeneratedCoverageReport
public class HostLMSItemLoanPolicyController extends HasHiddenRecordController<HostLMSItemLoanPolicy> {

    static responseFormats = ['json', 'xml']

    public HostLMSItemLoanPolicyController() {
        super(HostLMSItemLoanPolicy)
    }
}
