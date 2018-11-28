package ru.alfalab.cxf.starter.factory

import org.apache.cxf.endpoint.Client
import org.apache.cxf.transport.http.HTTPConduit
import org.mockito.Mockito
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
import org.springframework.boot.context.annotation.UserConfigurations
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.annotation.Bean
import ru.alfalab.cxf.starter.CxfClientAutoConfiguration
import ru.test.info.WSInfo12PortType
import spock.lang.Specification

import javax.net.ssl.SSLContext

import static org.assertj.core.api.Assertions.assertThat
import static ru.alfalab.cxf.starter.factory.ClientSpringPropertiesConstants.CXF_ALL_CLIENTS_VARARG

class CxfClientsFactorySpec extends Specification {
    def runner = new ApplicationContextRunner()
            .withConfiguration(
            AutoConfigurations.of(
                    PropertyPlaceholderAutoConfiguration,
                    CxfClientAutoConfiguration
            ))
            .withPropertyValues(*(CXF_ALL_CLIENTS_VARARG+'spring.cxf.useAnyBeanAsDefaultSSLContext=true'))

    def 'should work without ssl context'() {
        expect:
        runner.withConfiguration(UserConfigurations.of(
                WithoutSslContextConfiguration,
        )).run { context ->
            verifyAll {
                def bean = context.getBean(WSInfo12PortType)

                assertThat context hasSingleBean WSInfo12PortType
                assertThat bean isInstanceOf Client
                def client = bean as Client
                assertThat client.getConduit() isInstanceOf HTTPConduit
                def conduit = client.getConduit() as HTTPConduit
                assertThat conduit.getTlsClientParameters() isNull()
            }
        }
    }

    def 'should configure custom ssl context'() {
        expect:
        runner.withConfiguration(UserConfigurations.of(
                WithSslContextConfiguration,
        )).run { context ->
            verifyAll {
                def bean = context.getBean(WSInfo12PortType)

                assertThat context hasSingleBean WSInfo12PortType
                assertThat bean isInstanceOf Client
                def client = bean as Client
                assertThat client.getConduit() isInstanceOf HTTPConduit
                def conduit = client.getConduit() as HTTPConduit
                assertThat conduit.getTlsClientParameters() isNotNull()
            }
        }
    }

    @TestConfiguration
    static class WithoutSslContextConfiguration {

    }

    @TestConfiguration
    static class WithSslContextConfiguration {
        @Bean
        SSLContext context() {
            Mockito.mock(SSLContext)
        }
    }
}
