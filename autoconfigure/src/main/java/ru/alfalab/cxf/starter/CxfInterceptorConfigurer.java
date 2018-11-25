package ru.alfalab.cxf.starter;

import lombok.RequiredArgsConstructor;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.springframework.beans.factory.BeanFactory;
import ru.alfalab.cxf.starter.annotation.InterceptorType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class CxfInterceptorConfigurer {

    private final BeanFactory beanFactory;

    private final Map<InterceptorType, List<CxfInterceptorAnnotationProcessor.InterceptorInfo>> globalInterceptors;
    private final Map<Class<?>, Set<CxfInterceptorAnnotationProcessor.InterceptorInfo>>         specificInterceptors;

    public void configure(JaxWsProxyFactoryBean factoryBean) {
        Class<?> stubClass = factoryBean.getServiceClass();
        Stream.of(InterceptorType.values())
                .forEach(type -> {
                    addInterceptors(factoryBean, globalInterceptors.getOrDefault(type, emptyList()), type);
                    addInterceptors(factoryBean, specificInterceptors.getOrDefault(stubClass, emptySet()), type);
                });
    }

    private void addInterceptors(JaxWsProxyFactoryBean factoryBean,
                                 Collection<CxfInterceptorAnnotationProcessor.InterceptorInfo> interceptorInfos,
                                 InterceptorType interceptorType
    ) {
        getInterceptorList(factoryBean, interceptorType).addAll(
                interceptorInfos.stream()
                        .filter(info -> interceptorType.equals(info.getType()))
                        .map(this::getBean)
                        .collect(toList())
        );
    }

    private List<Interceptor<? extends Message>> getInterceptorList(JaxWsProxyFactoryBean factoryBean,
                                                                    InterceptorType type) {
        switch (type) {
            case IN:
                return factoryBean.getInInterceptors();
            case IN_FAULT:
                return factoryBean.getInFaultInterceptors();
            case OUT:
                return factoryBean.getOutInterceptors();
            case OUT_FAULT:
                return factoryBean.getOutFaultInterceptors();
            default:
                return emptyList();
        }
    }

    private Interceptor<? extends Message> getBean(CxfInterceptorAnnotationProcessor.InterceptorInfo info) {
        Object bean = beanFactory.getBean(info.getInterceptorBeanName(), info.getInterceptorClass());
        return (Interceptor<? extends Message>) bean;
    }
}
