package ru.alfalab.cxf.starter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.alfalab.cxf.starter.app.TestApplication
import ru.alfalab.cxf.starter.test.utils.ValidSpecification
import ru.test.async.AsyncHelloPortType

import javax.xml.ws.WebServiceException
import java.util.concurrent.ExecutionException

@SpringBootTest(classes = [TestApplication])
class CxfConnectionTimeoutSpec extends ValidSpecification {

    @Autowired
    private AsyncHelloPortType client

    private static ServerSocket serverSocket

    def setupSpec() {
        // server socket with single element backlog queue (1)
        serverSocket = new ServerSocket(4567, 1)
        // fill backlog queue by this request so consequent requests will be blocked
        new Socket().connect(serverSocket.getLocalSocketAddress())
    }

    def cleanupSpec() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close()
        }
    }

    def 'should apply timeout to SYNC requests'() {
        when:
        client.sayHello('foo')

        then:
        WebServiceException ex = thrown()
        ex.cause.class == SocketTimeoutException
    }

    def 'should apply timeout to ASYNC requests'() {
        when:
        client.sayHelloAsync('foo', { res -> }).get()

        then:
        ExecutionException ex = thrown()
        ex.cause.class == ConnectException
    }
}
