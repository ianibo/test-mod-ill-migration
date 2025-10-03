package com.k_int.permissions;

import groovy.transform.CompileStatic;

@CompileStatic
public enum PermissionGroup {
    /** The group intended for read access on the api */
    READ,

    /** The group intended for read and write access but not delete on the api */
    WRITE,

    /** The group intended for unrestricted access on the api */
    ALL
};