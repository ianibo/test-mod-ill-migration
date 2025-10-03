package com.k_int.permissions;

import groovy.transform.CompileStatic;

@CompileStatic
public class Permission {
    /** The name of the permission */
    public String permissionName;

    /** The display name */
    public String displayName;

    /** A brief desceiption */
    public String description;

    /** Is the permission visible */
    public boolean visible;

    /** Sub permissions that are also granted with this permission */
    public ArrayList subPermissions;

    public Permission() {
    }

    public Permission(
        String permissionName,
        String displayName,
        String description,
        boolean visible
    ) {
        this.permissionName = permissionName;
        this.displayName = displayName;
        this.description = description;
        this.visible = visible;
    }

}
