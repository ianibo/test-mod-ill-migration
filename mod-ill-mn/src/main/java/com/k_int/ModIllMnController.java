package com.k_int;

import io.micronaut.http.annotation.*;

@Controller("/mod-ill-mn")
public class ModIllMnController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}