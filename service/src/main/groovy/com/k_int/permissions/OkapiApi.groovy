package com.k_int.permissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import groovy.transform.CompileStatic;

@CompileStatic
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OkapiApi {
    /** The api name to be used in the descriptor, if empty the class will not be included in the descriptor */
    public String name() default "";

    /** Is this api a system api or not */
    public boolean isSystem() default false;

    /** The override display name for the all permissions group */
    public String displayNameGroupAll() default "";

    /** The override description for the all permissions group */
    public String descriptionGroupAll() default "";

    /** The visibility for the all permissions group */
    public boolean isVisibleGroupAll() default true;

    /** The override display name for the write permissions group */
    public String displayNameGroupWrite() default "";

    /** The override description for the write permissions group */
    public String descriptionGroupWrite() default "";

    /** The visibility for the write permissions group */
    public boolean isVisibleGroupWrite() default true;

    /** The override display name for the read permissions group */
    public String displayNameGroupRead() default "";

    /** The override description for the read permissions group */
    public String descriptionGroupRead() default "";

    /** The visibility for the read permissions group */
    public boolean isVisibleGroupRead() default true;
}
