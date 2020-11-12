package com.ntankard.javaObjectDatabase.database;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterMap {

    /**
     * Should this file be saved?
     */
    boolean shouldSave() default true;

    /**
     * The getters that correspond to the constructor parameters
     */
    String[] parameterGetters() default {""};
}
