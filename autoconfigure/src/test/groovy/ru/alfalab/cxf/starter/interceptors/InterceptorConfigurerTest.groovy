package ru.alfalab.cxf.starter.interceptors

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import org.apache.cxf.jaxws.interceptors.HolderOutInterceptor
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import ru.alfalab.cxf.starter.CxfInterceptorConfigurer
import ru.test.info.CorruptedWSInfo12PortType
import ru.test.info.WSInfo12PortType
import spock.lang.Specification

@ContextConfiguration(classes = ValidConfiguration)
public class InterceptorConfigurerTest extends Specification {

    @Autowired
    CxfInterceptorConfigurer interceptorConfigurer

    @Autowired
    WSS4JOutInterceptor wss4JOutInterceptor
    @Autowired
    HolderOutInterceptor holderOutInterceptor

    def "should add interceptors to JaxWsProxyFactoryBean"() {
        given:
        JaxWsProxyFactoryBean wSInfoFactoryBean = new JaxWsProxyFactoryBean(serviceClass: WSInfo12PortType)
        JaxWsProxyFactoryBean corruptedWsInfoFactoryBean = new JaxWsProxyFactoryBean(serviceClass: CorruptedWSInfo12PortType)

        when:
        interceptorConfigurer.configure(wSInfoFactoryBean)
        interceptorConfigurer.configure(corruptedWsInfoFactoryBean)

        then:
        wSInfoFactoryBean.outInterceptors.size() == 1
        wSInfoFactoryBean.outInterceptors[0] == wss4JOutInterceptor

        wSInfoFactoryBean.inInterceptors.size() == 2

        wSInfoFactoryBean.inFaultInterceptors.size() == 1
        wSInfoFactoryBean.inFaultInterceptors[0] == holderOutInterceptor

        and:
        corruptedWsInfoFactoryBean.outInterceptors.size() == 1
        corruptedWsInfoFactoryBean.outInterceptors[0] == wss4JOutInterceptor

        corruptedWsInfoFactoryBean.inFaultInterceptors.size() == 1
        corruptedWsInfoFactoryBean.inFaultInterceptors[0] == holderOutInterceptor
    }
}
