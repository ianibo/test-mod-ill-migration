package com.k_int.permissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import groovy.transform.CompileStatic;

@CompileStatic
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OkapiPermission {
    /** The name of the permission (not the full name, that part between the api name and the method), if name is empty, it will not be included in the descriptor */
    public String name() default "";

    /** The permissions group the generated permission should be included in */
    public PermissionGroup permissionGroup() default PermissionGroup.READ;

    /** The display name for this permission */
    public String displayName() default "";

    /** The description for this permission */
    public String description() default "";

    public boolean isVisible() default false;
}
