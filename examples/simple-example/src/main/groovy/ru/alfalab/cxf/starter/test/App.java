package ru.alfalab.cxf.starter.test;

import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.alfalab.cxf.starter.annotation.CxfGlobalInterceptor;
import ru.alfalab.cxf.starter.annotation.CxfSpecificInterceptor;
import ru.alfalab.cxf.starter.annotation.EnableCxfClient;
import ru.alfalab.cxf.starter.annotation.InterceptorType;
import ru.alfalab.cxf.starter.configuration.CxfClientConfigurer;
import ru.test.info.CorruptedWSInfo12PortType;
import ru.test.info.WSInfo12PortType;

/**
 * @author tolkv
 * @version 10/03/2017
 */
@EnableCxfClient
@SpringBootApplication
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class);
  }

  // tag::example-global-interceptor[]
  @Bean
  @CxfGlobalInterceptor(type = InterceptorType.OUT) // <1>
  public WSS4JOutInterceptor globalInterceptor() {
    return new WSS4JOutInterceptor();
  }
  // end::example-global-interceptor[]

  // tag::example-specific-interceptor[]
  @Bean
  @CxfSpecificInterceptor(type = InterceptorType.IN, applyFor = { WSInfo12PortType.class, CorruptedWSInfo12PortType.class }) // <2>
  public WSS4JInInterceptor specificInterceptor() {
    return new WSS4JInInterceptor();
  }
  // end::example-specific-interceptor[]

  // tag::example-specific-client-configurer[]
  @Bean
  public CxfClientConfigurer cxfClientConfigurer() {
    return (client, clientDefinition) -> {
        // do anything
        System.out.println("clientDefinition = " + clientDefinition);
    };
  }
  // end::example-specific-client-configurer[]
}
