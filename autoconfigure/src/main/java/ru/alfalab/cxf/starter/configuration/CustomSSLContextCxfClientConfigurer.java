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

        if(isTLS(clientDefinition)) {
          TLSClientParameters tlsParams = new TLSClientParameters();
          configureSSLTlsParameter(clientDefinition, tlsParams);
          tlsParams.setDisableCNCheck(clientDefinition.isDisableCNCheck());

          conduit.setTlsClientParameters(tlsParams);
        }

    }

  private void configureSSLTlsParameter(WSClient clientDefinition,
                                        TLSClientParameters tlsParams) {
    if(sslContextIsAvailable(clientDefinition)) {
      String sslContextBeanName = resolveSslContextBeanName(clientDefinition);
      tlsParams.setSslContext(sslContexts.get(sslContextBeanName));
    }
  }

  private boolean isTLS(WSClient clientDefinition) {
    return clientDefinition.isDisableCNCheck() ||
      sslContextIsAvailable(clientDefinition) ||
      canUseAnySSLContextBean(cxfClientsProperties.isUseAnyBeanAsDefaultSslContext());
  }

  private boolean canUseAnySSLContextBean(boolean useAnyBeanAsDefaultSslContext) {
    return useAnyBeanAsDefaultSslContext && !sslContexts.isEmpty();
  }

  private boolean sslContextIsAvailable(WSClient clientDefinition) {
    return canUseAnySSLContextBean(clientDefinition.getSslContextBeanName() != null);
  }

  private String resolveSslContextBeanName(WSClient clientDefinition) {
        String sslContextBeanName = clientDefinition.getSslContextBeanName();
        if (sslContextBeanName != null) {
            return sslContextBeanName;
        }

        if (canUseAnySSLContextBean(cxfClientsProperties.isUseAnyBeanAsDefaultSslContext())) {
            return sslContexts.entrySet().iterator().next().getKey(); // find first ssl context
        }

        return null;
    }

    private boolean isHTTPConduit(Conduit conduit) {
        return conduit instanceof HTTPConduit;
    }
}
