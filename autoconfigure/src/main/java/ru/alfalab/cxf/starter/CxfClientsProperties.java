package ru.alfalab.cxf.starter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduitFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.stream.Stream;

import static org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduitFactory.UseAsyncPolicy.ASYNC_ONLY;

/**
 * @author tolkv
 * @since 06/04/16
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "spring.cxf")
public class CxfClientsProperties {
  @Singular
  private List<WSClient> clients;

  private ClientConfig client = new ClientConfig(true, true, ASYNC_ONLY);

  public Stream<WSClient> clients() {
    if (clients != null) {
      return clients.stream();
    }
    return Stream.empty();
  }

  @Data
  public static class WSClient {
    private String endpoint;
    private String className;
    private String id;
    private Integer connectionTimeout;
    private Integer receiveTimeout;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ClientConfig {
    boolean enabled;
    boolean loggingEnabled;
    AsyncHTTPConduitFactory.UseAsyncPolicy asyncMode;
  }
}
