package com.k_int.permissions;

import groovy.transform.CompileStatic;

@CompileStatic
public class UrlHandler {
    /** The http methods this handler applies to */
    public ArrayList methods = [ ];

    /** The url pattern that applies */
    public String pathPattern;

    /** The permissions required to access this path */
    public ArrayList permissionsRequired = [ ];

    public UrlHandler() {
    }

    public UrlHandler(
        String method,
        String pathPattern,
        Permission permissionRequired
    ) {
        methods.add(method);
        this.pathPattern = pathPattern;
        if (permissionRequired) {
            permissionsRequired.add(permissionRequired.permissionName);
        }
    }
}
