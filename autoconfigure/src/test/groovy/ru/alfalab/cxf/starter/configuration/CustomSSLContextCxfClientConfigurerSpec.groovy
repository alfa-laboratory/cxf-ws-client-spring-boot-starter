package ru.alfalab.cxf.starter.configuration

import org.apache.cxf.configuration.jsse.TLSClientParameters
import org.apache.cxf.endpoint.Client
import org.apache.cxf.transport.Conduit
import org.apache.cxf.transport.http.HTTPConduit
import org.junit.Rule
import org.springframework.boot.test.rule.OutputCapture
import ru.alfalab.cxf.starter.CxfClientsProperties
import spock.lang.Specification
import spock.lang.Subject

import javax.net.ssl.SSLContext

import static ru.alfalab.cxf.starter.CxfClientsProperties.*

class CustomSSLContextCxfClientConfigurerSpec extends Specification {
    def sslContextMock = [
            'someSslContext': Mock(SSLContext),
            'someSslContext2': Mock(SSLContext),
            'someSslContext3': Mock(SSLContext)
    ]
    def clientMock = Mock(Client)
    def cxfClientsProperties = Mock(CxfClientsProperties)

    @Rule
    OutputCapture outputCapture = new OutputCapture()

    @Subject
    CustomSSLContextCxfClientConfigurer configurer

    def setup() {
        configurer = new CustomSSLContextCxfClientConfigurer(sslContextMock, cxfClientsProperties)
    }

    def 'should add tls properties with specific ssl context name'() {
        given:
        def hTTPConduit = Mock(HTTPConduit)

        when:
        configurer.configure clientMock, new WSClient(
                className: 'WSInfoPortType',
                endpoint: 'http://test',
                sslContextBeanName: 'someSslContext'
        )

        then:
        clientMock.getConduit() >> hTTPConduit

        1 * hTTPConduit.setTlsClientParameters(_) >> { TLSClientParameters p ->
            assert p.getSslContext().is(sslContextMock['someSslContext'])
        }
    }

    def 'should add tls properties when use any sslContext bean flag is true'() {
        given:
        def hTTPConduit = Mock(HTTPConduit)

        when:
        configurer.configure clientMock, new WSClient(
                className: 'WSInfoPortType',
                endpoint: 'http://test'
        )

        then:
        clientMock.getConduit() >> hTTPConduit
        cxfClientsProperties.isUseAnyBeanAsDefaultSSLContext() >> true

        1 * hTTPConduit.setTlsClientParameters(_)
    }

    def 'should add tls properties with specific context when use any sslContext bean flag is true'() {
        given:
        def hTTPConduit = Mock(HTTPConduit)

        when:
        configurer.configure clientMock, new WSClient(
                className: 'WSInfoPortType',
                endpoint: 'http://test',
                sslContextBeanName: 'someSslContext2'
        )

        then:
        clientMock.getConduit() >> hTTPConduit
        cxfClientsProperties.isUseAnyBeanAsDefaultSSLContext() >> true

        1 * hTTPConduit.setTlsClientParameters(_) >> { TLSClientParameters p ->
            assert p.getSslContext().is(sslContextMock['someSslContext2'])
        }
    }

    def 'should ignore when HTTP Conduit not available'() {
        when:
        configurer.configure clientMock, new WSClient(
                className: 'WSInfoPortType',
                endpoint: 'http://test',
        )

        then:
        clientMock.getConduit() >> Mock(Conduit)
        outputCapture.toString() contains 'conduit type is not supported'
        noExceptionThrown()
    }
}
