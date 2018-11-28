package ru.alfalab.cxf.starter;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.BeanCreationException;
import ru.alfalab.cxf.starter.configuration.CxfClientConfigurer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author tolkv
 * @since 08/04/16
 */
@Slf4j
public class CxfWsStubBeanFactory {
  private final Bus                                        bus;
  private final CxfInterceptorConfigurer                   interceptorConfigurer;
  private final Map<String, CxfClientsProperties.WSClient> stringClientMap;
  private final List<CxfClientConfigurer>                  clientConfigurers;

  public CxfWsStubBeanFactory(
          CxfClientsProperties cxfClientsProperties,
          Bus bus,
          CxfInterceptorConfigurer interceptorConfigurer,
          List<CxfClientConfigurer> clientConfigurers) {
    this.bus = bus;
    this.interceptorConfigurer = interceptorConfigurer;
    this.clientConfigurers = clientConfigurers;

    log.debug("cxfClientsProperties = {}", cxfClientsProperties);
    // create map with WSStub class name as key
    stringClientMap = cxfClientsProperties.clients()
        .collect(
            Collectors.toMap(
                CxfClientsProperties.WSClient::getClassName, //key
                client -> client,//value
                (u, u2) -> u2 //merge function
            )
        );
    log.debug("stringClientMap = {}", stringClientMap);
  }

  /**
   * Factory method for producing ***PortType stub objects
   *
   * @param portTypeClass PortType class
   * @return prepared stub
   */
  public Object create(Class<?> portTypeClass) {
    CxfClientsProperties.WSClient clientConfig = stringClientMap.get(portTypeClass.getName());
    if (clientConfig == null || isEmpty(clientConfig.getEndpoint())) {
      showWarningMessageWithExampleConfig(portTypeClass);
      throw new BeanCreationException("Apache CXF starter autoscan package", portTypeClass.getCanonicalName(),
          "Add next properties to your application.yml file:\nspring.cxf:\n" +
              " clients:\n  -\n   endpoint: http://SOME_HOST/SOME_PATH_TO_WS\n   className: " + portTypeClass.getCanonicalName()
      );
    }

    JaxWsProxyFactoryBean jaxWsClientFactoryBean = new JaxWsProxyFactoryBean();
    jaxWsClientFactoryBean.setBus(bus);
    jaxWsClientFactoryBean.setAddress(clientConfig.getEndpoint());
    jaxWsClientFactoryBean.setServiceClass(portTypeClass);

    interceptorConfigurer.configure(jaxWsClientFactoryBean);

    Object wsClient = jaxWsClientFactoryBean.create();
    setTimeouts(wsClient, clientConfig);

    if(wsClient instanceof Client) {
      Client cxfClient = (Client) wsClient;

      clientConfigurers.forEach(cxfClientConfigurer -> cxfClientConfigurer
              .configure(cxfClient, clientConfig)
      );
    } else {
      log.warn("Can't configure jax-ws client for class {}. \n" +
              "Cxf factory produced class which is't derived from org.apache.cxf.endpoint.Client",
              portTypeClass
      );
    }

    return wsClient;
  }

  private void setTimeouts(Object wsClient, CxfClientsProperties.WSClient clientConfig) {
    if (clientConfig.getConnectionTimeout() == null && clientConfig.getReceiveTimeout() == null) {
      return;
    }

    Client client = ClientProxy.getClient(wsClient);
    HTTPConduit http = (HTTPConduit) client.getConduit();
    HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();

    if (clientConfig.getConnectionTimeout() != null) {
      httpClientPolicy.setConnectionTimeout(clientConfig.getConnectionTimeout());
    }
    if (clientConfig.getReceiveTimeout() != null) {
      httpClientPolicy.setReceiveTimeout(clientConfig.getReceiveTimeout());
    }

    http.setClient(httpClientPolicy);
  }

  private void showWarningMessageWithExampleConfig(Class<?> portTypeClass) {
    log.info("Bean for {} client have not initialized", portTypeClass.getCanonicalName());
    log.info("Add next properties to your application.yml file:\nspring.cxf:\n" +
            " clients:\n  -\n   endpoint: http://SOME_HOST/SOME_PATH_TO_WS\n   className: {}",
        portTypeClass.getCanonicalName());
  }
}
