package ru.alfalab.cxf.starter.interceptors

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import ru.alfalab.cxf.starter.CxfInterceptorAnnotationProcessor
import ru.alfalab.cxf.starter.annotation.CxfGlobalInterceptor
import ru.alfalab.cxf.starter.annotation.InterceptorType

@TestConfiguration
class InvalidConfiguration {

    @Bean
    static CxfInterceptorAnnotationProcessor processor() {
        new CxfInterceptorAnnotationProcessor()
    }

    @CxfGlobalInterceptor(type = InterceptorType.OUT)
    @Bean
    Object object() {
        new Object()
    }
}
