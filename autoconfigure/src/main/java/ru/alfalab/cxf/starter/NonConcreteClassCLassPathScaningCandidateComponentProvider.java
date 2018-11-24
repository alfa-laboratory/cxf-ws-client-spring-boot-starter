package ru.alfalab.cxf.starter;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;

/**
 * @author tolkv
 * @since 08/08/16
 */
public class NonConcreteClassCLassPathScaningCandidateComponentProvider extends ClassPathScanningCandidateComponentProvider {

  public NonConcreteClassCLassPathScaningCandidateComponentProvider(boolean useDefaultFilters) {
    super(useDefaultFilters);
  }

  public NonConcreteClassCLassPathScaningCandidateComponentProvider(boolean useDefaultFilters, Environment environment) {
    super(useDefaultFilters, environment);
  }

  @Override
  protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
    return beanDefinition.getMetadata().isIndependent();
  }
}
