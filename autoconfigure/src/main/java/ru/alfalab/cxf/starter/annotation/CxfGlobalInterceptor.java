package ru.alfalab.cxf.starter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that interceptor bean marked with the annotation should be applied to all stubs.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CxfGlobalInterceptor {

    /**
     * Returns an interceptor type.
     *
     * @return an interceptor type
     */
    InterceptorType type();
}
