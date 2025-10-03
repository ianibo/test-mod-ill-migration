package com.k_int.settings;

import com.k_int.permissions.OkapiApi;
import com.k_int.web.toolkit.settings.AppSetting;

import io.swagger.annotations.Api;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport

@Api(value = "/ill/settings/systemSetting", tags = ["Settings (System)"])
@OkapiApi(name = "systemSetting")
@ExcludeFromGeneratedCoverageReport
public class SystemSettingController extends SettingController<AppSetting> {

    public SystemSettingController() {
        super(AppSetting);
    }
}