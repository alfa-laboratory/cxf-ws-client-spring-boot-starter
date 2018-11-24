package ru.alfalab.cxf.starter;

import lombok.Data;
import lombok.Getter;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.type.MethodMetadata;
import ru.alfalab.cxf.starter.annotation.CxfGlobalInterceptor;
import ru.alfalab.cxf.starter.annotation.CxfSpecificInterceptor;
import ru.alfalab.cxf.starter.annotation.InterceptorType;

import java.util.*;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;

public class CxfInterceptorAnnotationProcessor implements BeanFactoryPostProcessor {

    @Getter
    private final Map<InterceptorType, List<InterceptorInfo>> globalInterceptors   = new HashMap<>();
    @Getter
    private final Map<Class<?>, Set<InterceptorInfo>>         specificInterceptors = new HashMap<>();

    @Data
    static class InterceptorInfo {
        private final InterceptorType                                 type;
        private final String                                          interceptorBeanName;
        private final Class<? extends Interceptor<? extends Message>> interceptorClass;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();

        for (String beanName : beanDefinitionNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            if (!AnnotatedBeanDefinition.class.isAssignableFrom(beanDefinition.getClass())) {
                continue;
            }

            AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) beanDefinition;
            Optional.ofNullable(abd.getFactoryMethodMetadata())
                    .ifPresent(methodMetadata -> findAnnotatedInterceptor(beanName, methodMetadata));
        }
    }

    private void findAnnotatedInterceptor(String beanName, MethodMetadata metadata) {
        String typeName = metadata.getReturnTypeName();
        Optional.ofNullable(metadata.getAnnotationAttributes(CxfGlobalInterceptor.class.getName()))
                .ifPresent(map -> {
                    Class<? extends Interceptor<? extends Message>> interceptorClass = checkClass(beanName, typeName);
                    addGlobalInterceptor(beanName, interceptorClass, map);
                });

        Optional.ofNullable(metadata.getAnnotationAttributes(CxfSpecificInterceptor.class.getName()))
                .ifPresent(map -> {
                    Class<? extends Interceptor<? extends Message>> interceptorClass = checkClass(beanName, typeName);
                    addSpecificInterceptor(beanName, interceptorClass, map);
                });
    }

    private Class<? extends Interceptor<? extends Message>> checkClass(String beanName, String typeName) {
        Class clazz = null;
        try {
            clazz = Class.forName(typeName);
        } catch (ClassNotFoundException ignored) {
            // ex will thrown below
        }

        if (clazz == null || !Interceptor.class.isAssignableFrom(clazz)) {
            throw new BeanCreationException(beanName,
                    "Annotations @CxfSpecificInterceptor and @CxfGlobalInterceptor should present only on beans of type " +
                            Interceptor.class.getName() + " and its subclasses"
            );
        }
        return (Class<? extends Interceptor<? extends Message>>) clazz;
    }

    private void addGlobalInterceptor(String beanName,
                                      Class<? extends Interceptor<? extends Message>> clazz,
                                      Map<String, Object> cxfGlobalAnn
    ) {
        InterceptorType type = (InterceptorType) cxfGlobalAnn.get("type");
        addItem(globalInterceptors, type, asList(new InterceptorInfo(type, beanName, clazz)));
    }

    private void addSpecificInterceptor(String interceptorBeanName,
                                        Class<? extends Interceptor<? extends Message>> clazz,
                                        Map<String, Object> cxfSpecificAnn
    ) {
        InterceptorType type     = (InterceptorType) cxfSpecificAnn.get("type");
        Class<?>[]      applyFor = (Class<?>[]) cxfSpecificAnn.get("applyFor");

        if (applyFor == null || applyFor.length == 0) {
            throw new BeanCreationException("@" + CxfSpecificInterceptor.class
                    .getSimpleName() + " member 'applyFor' must not be empty!");
        }

        for (Class stubClass : applyFor) {
            InterceptorInfo interceptorInfo = new InterceptorInfo(type, interceptorBeanName, clazz);
            addItem(specificInterceptors, stubClass, newHashSet(interceptorInfo));
        }
    }

    private void addItem(Map map, Object key, Collection<InterceptorInfo> value) {
        map.merge(
                key,
                value,
                (oldValue, newValue) -> {
                    ((Collection) oldValue).addAll((Collection) newValue);
                    return oldValue;
                }
        );
    }
}
