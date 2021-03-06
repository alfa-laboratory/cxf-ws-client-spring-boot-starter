package ru.alfalab.cxf.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import ru.alfalab.cxf.starter.CxfClientsProperties.Packages;

import javax.jws.WebService;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author tolkv
 * @since 06/04/16
 */
@Slf4j
@RequiredArgsConstructor
public class CxfBeanDefinitionPostProcessor implements BeanDefinitionRegistryPostProcessor {

  private final Environment environment;

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
    NonConcreteClassCLassPathScaningCandidateComponentProvider provider = new NonConcreteClassCLassPathScaningCandidateComponentProvider(false);
    provider.addIncludeFilter(new AnnotationTypeFilter(WebService.class));

    Packages packages = Binder.get(environment)
      .bind("spring.cxf.packages", Packages.class)
      .orElse(new Packages());

    packages.getSkip().forEach(p ->
        provider.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(String.format("^%s.*", p))))
    );

    packages.getScan().stream()
        .flatMap(s -> provider.findCandidateComponents(s).stream()
            .map(BeanDefinition::getBeanClassName))
        .forEach(portTypeClassName -> {
          try {
            Class<?> aClass = Class.forName(portTypeClassName);

            RootBeanDefinition beanDefinition = createBeanDefinition(aClass);

            ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
            constructorArgumentValues.addIndexedArgumentValue(0, aClass);

            WebService annotation = AnnotationUtils.findAnnotation(aClass, WebService.class);

            if (isNull(annotation)) {
              throw new IllegalStateException("Couldn't find @WebService annotation in a proxy class");
            }

            String name = isEmpty(annotation.name()) ? aClass.getPackage().getName() + "." +portTypeClassName : aClass.getPackage().getName() + "." + annotation.name();

            beanDefinitionRegistry.registerBeanDefinition(isEmpty(name) ? portTypeClassName : name, beanDefinition);

          } catch (ClassNotFoundException e) {
            log.error("CXF client proxy class not found", e);
          }
        });
  }

  private RootBeanDefinition createBeanDefinition(Class<?> aClass) {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(aClass);
    beanDefinition.setTargetType(aClass);
    beanDefinition.setRole(BeanDefinition.ROLE_APPLICATION);
    beanDefinition.setFactoryBeanName(CxfClientAutoConfiguration.CXF_WS_CLIENT_PROXY_FACTORY_DEFAULT_NAME);
    beanDefinition.setFactoryMethodName("create");
    beanDefinition.setSynthetic(true);
    beanDefinition.setResourceDescription("Auto generated bean by cxf-ws-client-spring-boot-starter");
    return beanDefinition;
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException { /* do nothing */}
}

