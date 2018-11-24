package ru.alfalab.cxf.starter

import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.alfalab.cxf.starter.app.TestApplication
import ru.alfalab.cxf.starter.test.utils.FileResponseBuilder
import ru.alfalab.cxf.starter.test.utils.SoapTransformder
import ru.alfalab.cxf.starter.test.utils.ValidSpecification
import ru.test.async.AsyncHelloPortType

import javax.xml.ws.WebServiceException
import java.util.concurrent.ExecutionException

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

@SpringBootTest(classes = [TestApplication])
class CxfReceiveTimeoutSpec extends ValidSpecification {

    private static final int CLIENT_TIMEOUT = 1000

    @Rule
    protected WireMockRule wireMockRule = new WireMockRule(options()
            .port(4567)
            .extensions(SoapTransformder)
    )

    @Autowired
    private AsyncHelloPortType client

    def 'should apply timeout to SYNC requests'() {
        given:
        prepareResponseWithTimeout()

        when:
        client.sayHello('foo')

        then:
        WebServiceException ex = thrown()
        ex.cause.class == SocketTimeoutException
    }

    def 'should apply timeout to ASYNC requests'() {
        given:
        prepareResponseWithTimeout()

        when:
        client.sayHelloAsync('foo', { res -> }).get()

        then:
        ExecutionException ex = thrown()
        ex.cause.class == SocketTimeoutException
    }

    private static StubMapping prepareResponseWithTimeout() {
        FileResponseBuilder.stubFor('/SayHello', CLIENT_TIMEOUT * 2)
                .withFile('hello-response.xml')
                .build()
    }
}
