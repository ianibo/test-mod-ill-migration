package com.k_int.settings;

import com.k_int.institution.InstitutionSetting;
import com.k_int.permissions.OkapiApi;

import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Api(value = "/ill/settings/institutionSetting", tags = ["Settings (Institution)"])
@OkapiApi(name = "institutionSetting")
@ExcludeFromGeneratedCoverageReport
public class InstitutionSettingController extends SettingController<InstitutionSetting> {

    public InstitutionSettingController() {
        super(InstitutionSetting);
    }
}