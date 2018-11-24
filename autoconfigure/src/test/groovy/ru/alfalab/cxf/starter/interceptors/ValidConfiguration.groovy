package ru.alfalab.cxf.starter.interceptors

import org.apache.cxf.jaxws.interceptors.HolderOutInterceptor
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor
import org.apache.cxf.wsdl.interceptors.BareInInterceptor
import org.springframework.beans.factory.BeanFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import ru.alfalab.cxf.starter.CxfInterceptorAnnotationProcessor
import ru.alfalab.cxf.starter.CxfInterceptorConfigurer
import ru.alfalab.cxf.starter.annotation.CxfGlobalInterceptor
import ru.alfalab.cxf.starter.annotation.CxfSpecificInterceptor
import ru.alfalab.cxf.starter.annotation.InterceptorType
import ru.test.info.CorruptedWSInfo12PortType
import ru.test.info.WSInfo12PortType

@TestConfiguration
class ValidConfiguration {

    @Bean
    CxfInterceptorConfigurer interceptorConfigurer(
            CxfInterceptorAnnotationProcessor ciap,
            BeanFactory beanFactory
    ) {
        new CxfInterceptorConfigurer(
                beanFactory,
                ciap.globalInterceptors,
                ciap.specificInterceptors
        )
    }

    @Bean
    static CxfInterceptorAnnotationProcessor ciap() {
        new CxfInterceptorAnnotationProcessor()
    }

    @Bean
    @CxfSpecificInterceptor(type = InterceptorType.IN, applyFor = [WSInfo12PortType])
    BareInInterceptor bareInInterceptor() {
        new BareInInterceptor()
    }

    @Bean
    @CxfSpecificInterceptor(type = InterceptorType.IN, applyFor = [WSInfo12PortType])
    WSS4JInInterceptor wss4JInInterceptor() {
        new WSS4JInInterceptor()
    }

    @Bean
    @CxfSpecificInterceptor(type = InterceptorType.OUT, applyFor = [WSInfo12PortType, CorruptedWSInfo12PortType])
    WSS4JOutInterceptor wss4JOutInterceptor() {
        new WSS4JOutInterceptor()
    }

    @Bean
    @CxfGlobalInterceptor(type = InterceptorType.IN_FAULT)
    HolderOutInterceptor holderOutInterceptor() {
        new HolderOutInterceptor()
    }
}
