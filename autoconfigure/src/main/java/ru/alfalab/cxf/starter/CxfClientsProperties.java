package ru.alfalab.cxf.starter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduitFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduitFactory.UseAsyncPolicy.ASYNC_ONLY;

/**
 * @author Kirill Tolkachev
 * @since 06/04/16
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "spring.cxf")
public class CxfClientsProperties {
  @Singular
  private List<WSClient> clients;

  private ClientConfig client = new ClientConfig(true, true, ASYNC_ONLY);

  private Packages packages = new Packages();

  public Stream<WSClient> clients() {
    if (clients != null) {
      return clients.stream();
    }
    return Stream.empty();
  }

  /**
   * Use {@link javax.net.ssl.SSLContext} as default for all Cxf Clients
   * If {@link #useAnyBeanAsDefaultSslContext} is set, starter will find any ssl context bean and set it to clients,
   * Works only if {@link WSClient#sslContextBeanName} not set
   *
   * @see javax.net.ssl.SSLContext
   */
  private boolean useAnyBeanAsDefaultSslContext = false;

  @Data
  public static class WSClient {
    private String endpoint;
    private String className;
    private String id;
    private Integer connectionTimeout;
    private Integer receiveTimeout;
    /**
     * Customize CXF SSL context
     * @see TLSClientParameters
     */


    private String sslContextBeanName;

    /**
     * Disable check CN in client side
     * Check by default. Disable only for development purposes
     */
    private boolean disableCNCheck = false;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ClientConfig {
    private boolean enabled;
    private boolean loggingEnabled;
    private AsyncHTTPConduitFactory.UseAsyncPolicy asyncMode;
  }

  @Data
  public static class Packages {
    private List<String> scan = newArrayList("ru");
    private List<String> skip = newArrayList();
  }
}
