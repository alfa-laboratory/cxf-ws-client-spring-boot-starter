package ru.alfalab.cxf.starter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that interceptor bean marked with the annotation should be applied to the specified stubs.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CxfSpecificInterceptor {

    /**
     * Returns an array of stub classes which should be populated with interceptor marked with this annotation.
     *
     * @return an array of classes which should be populated with interceptor marked with this annotation
     */
    Class<?>[] applyFor();

    /**
     * Returns an interceptor type.
     *
     * @return an interceptor type
     */
    InterceptorType type();
}
