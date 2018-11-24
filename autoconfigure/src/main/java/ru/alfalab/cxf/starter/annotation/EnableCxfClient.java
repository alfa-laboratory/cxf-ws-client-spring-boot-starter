package ru.alfalab.cxf.starter.annotation;

import org.springframework.context.annotation.Import;
import ru.alfalab.cxf.starter.CxfClientAutoConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author tolkv
 * @since 05/04/16
 */
@Retention(RetentionPolicy.RUNTIME)
@Import(CxfClientAutoConfiguration.class)
public @interface EnableCxfClient {
}
