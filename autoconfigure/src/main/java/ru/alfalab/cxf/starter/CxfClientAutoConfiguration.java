package ru.alfalab.cxf.starter;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.BusExtensionPostProcessor;
import org.apache.cxf.bus.spring.BusWiringBeanFactoryPostProcessor;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.JaxWsClientFactoryBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.alfalab.cxf.starter.configuration.CxfBusConfigurer;
import ru.alfalab.cxf.starter.configuration.DefaultCxfBusConfigurer;

/**
 * @author tolkv
 * @since 05/04/16
 */
@Slf4j
@Configuration
@ConditionalOnClass({SpringBus.class, JaxWsClientFactoryBean.class, ConfigurationPropertiesBindingPostProcessor.class})
@AutoConfigureAfter(ConfigurationPropertiesAutoConfiguration.class)
@EnableConfigurationProperties
@ConditionalOnProperty(name = "spring.cxf.client.enabled", matchIfMissing = true)
public class CxfClientAutoConfiguration {
  static final String CXF_WS_CLIENT_PROXY_FACTORY_DEFAULT_NAME = "CxfWsClientProxyFactory";

  @Bean
  public CxfBeanDefinitionPostProcessor cxfBeanDefinitionPostProcessor(Environment environment) {
    return new CxfBeanDefinitionPostProcessor(environment);
  }

  @Bean
  public static BusWiringBeanFactoryPostProcessor jsr250BeanPostProcessor() {
    return new BusWiringBeanFactoryPostProcessor();
  }

  @Bean
  public static BusExtensionPostProcessor busExtensionPostProcessor() {
    return new BusExtensionPostProcessor();
  }

  @Slf4j
  @Configuration
  @ConditionalOnClass({SpringBus.class, JaxWsClientFactoryBean.class, ConfigurationPropertiesBindingPostProcessor.class})
  @EnableConfigurationProperties(CxfClientsProperties.class)
  public static class CxfClientFactoryAutoConfiguration {

    @Bean(name = CXF_WS_CLIENT_PROXY_FACTORY_DEFAULT_NAME)
    @ConditionalOnMissingBean(name = {CXF_WS_CLIENT_PROXY_FACTORY_DEFAULT_NAME})
    public CxfWsStubBeanFactory proxyWsBeanFactory(
            CxfClientsProperties cxfClientsProperties,
            Bus bus,
            CxfInterceptorConfigurer interceptorConfigurer
    ) {
      return new CxfWsStubBeanFactory(
              cxfClientsProperties,
              bus,
              interceptorConfigurer
      );
    }

    @Bean
    @ConditionalOnMissingBean(CxfBusConfigurer.class)
    public CxfBusConfigurer cxfBusConfigurer(CxfClientsProperties cxfClientsProperties) {
      return new DefaultCxfBusConfigurer(cxfClientsProperties);
    }

    @Bean(destroyMethod = "shutdown")
    public Bus cxf(CxfBusConfigurer cxfBusConfigurer) {
      SpringBus bus = new SpringBus();
      cxfBusConfigurer.configure(bus);

      return bus;
    }

    @Bean
    @ConditionalOnMissingBean(CxfInterceptorConfigurer.class)
    public CxfInterceptorConfigurer cxfInterceptorConfigurer(
            CxfInterceptorAnnotationProcessor cxfInterceptorAnnotationProcessor,
            BeanFactory beanFactory
    ) {
      return new CxfInterceptorConfigurer(
              beanFactory,
              cxfInterceptorAnnotationProcessor.getGlobalInterceptors(),
              cxfInterceptorAnnotationProcessor.getSpecificInterceptors()
      );
    }

    @Bean
    @ConditionalOnMissingBean(CxfInterceptorAnnotationProcessor.class)
    public static CxfInterceptorAnnotationProcessor cxfInterceptorBFPP() {
      return new CxfInterceptorAnnotationProcessor();
    }
  }
}
