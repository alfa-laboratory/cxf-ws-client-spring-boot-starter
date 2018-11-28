package ru.alfalab.cxf.starter.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.HTTPConduit;
import ru.alfalab.cxf.starter.CxfClientsProperties;
import ru.alfalab.cxf.starter.CxfClientsProperties.WSClient;

import javax.net.ssl.SSLContext;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CustomSSLContextCxfClientConfigurer implements CxfClientConfigurer {
    private final Map<String, SSLContext> sslContexts;
    private final CxfClientsProperties    cxfClientsProperties;

    @Override
    public void configure(Client cxfClient, WSClient clientDefinition) {
        if (!isHTTPConduit(cxfClient.getConduit())) {
            log.warn("{} conduit type is not supported", cxfClient.getConduit().getClass().getName());
            return;
        }

        HTTPConduit conduit            = (HTTPConduit) cxfClient.getConduit();
        String      sslContextBeanName = resolveSslContextBeanName(clientDefinition);

        if (sslContextBeanName != null && !sslContexts.isEmpty()) {
            TLSClientParameters tlsParams = new TLSClientParameters();
            tlsParams.setSslContext(sslContexts.get(sslContextBeanName));

            conduit.setTlsClientParameters(tlsParams);
        }

    }

    private String resolveSslContextBeanName(WSClient clientDefinition) {
        String sslContextBeanName = clientDefinition.getSslContextBeanName();
        if (sslContextBeanName != null) {
            return sslContextBeanName;
        }

        if (cxfClientsProperties.isUseAnyBeanAsDefaultSSLContext() && !sslContexts.isEmpty()) {
            return sslContexts.entrySet().iterator().next().getKey(); // find first ssl context
        }

        return null;
    }

    private boolean isHTTPConduit(Conduit conduit) {
        return conduit instanceof HTTPConduit;
    }
}
