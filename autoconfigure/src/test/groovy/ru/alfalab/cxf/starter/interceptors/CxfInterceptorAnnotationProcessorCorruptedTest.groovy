package ru.alfalab.cxf.starter.interceptors

import org.apache.cxf.interceptor.Interceptor
import org.springframework.beans.factory.BeanCreationException
import org.springframework.boot.SpringApplication
import spock.lang.Specification

class CxfInterceptorAnnotationProcessorCorruptedTest extends Specification {

    void "should throw exception and explain why"() {
        when:
        SpringApplication.run(InvalidConfiguration)

        then:
        def ex = thrown(BeanCreationException)
        ex.message.contains "Annotations @CxfSpecificInterceptor and @CxfGlobalInterceptor should present only on beans of type " + Interceptor.name
    }
}
