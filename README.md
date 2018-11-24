# Spring Boot Apache CXF WS-Clients starter

[![Coverage Status](https://coveralls.io/repos/github/alfa-laboratory/cxf-ws-client-spring-boot-starter/badge.svg?branch=master)](https://coveralls.io/github/alfa-laboratory/cxf-ws-client-spring-boot-starter?branch=master)
[![Build Status](https://travis-ci.org/alfa-laboratory/cxf-ws-client-spring-boot-starter.svg?branch=master)](https://travis-ci.org/alfa-laboratory/cxf-ws-client-spring-boot-starter)
[![Download](https://api.bintray.com/packages/alfa-laboratory/maven-releases/cxf-ws-client-spring-boot-starter/images/download.svg) ](https://bintray.com/alfa-laboratory/maven-releases/cxf-ws-client-spring-boot-starter/_latestVersion)

The best way to consumer your legacy jax-ws services.

Versions compatibility:

3.x.x – Spring Boot 2.x.x
1.x.x – Is too old, not supported

## Quick start

Add as dependency

```groovy
    dependencies {
        compile 'ru.alfalab.starter.cxf:starter:{starterVersion}'
    }
```

Enable or disable by `spring.cxf.client.enabled` bool option

## How it works

1. Cxf starter configure new `CxfBeanDefinitionPostProcessor` for making beans from each PortType stub with `@WebService` annotation
1. Automatically scan classpath and find all classes with `@WebService` annotatoin. Configure `BeanDefiniton` for this
stub and pass control to next stage for build bean instance.
1. Each bean has constructed by factory bean - `CxfWsStubBeanFactory`. Factory bean match information about service endpoint by port type classname in `spring.cxf` configuration .
See example below

```yaml
spring.cxf:
 clients:
  -
   endpoint: http://ws.srv/TESTSERVICE/TESTSERVICE12
   className: ru.testservice.TestService12PortType
  -
   endpoint: http://ws.srv/TEST/INFO/WSInfo12/WSInfo12PortType
   className: ru.test.info.WSInfo12PortType
```

By default all services searching in `ru.` package. If you need change it you have to add next properties into your `application.yml`.
Also you can specify packages you don't want to scan.
For example:

```yaml
spring.cxf:
 packages:
  scan:
   - myorg.package
  skip:
   - myorg.package.skip
```



## Interceptors

PortType stubs can be provided with a list of interceptors. If you want to use this feature, you have to declare your interceptor as Spring Bean in your code
and annotate it with `@CxfSpecificInterceptor` or `@CxfGlobalInterceptor`.
There are 4 types of interceptors: `IN`, `IN_FAULT`, `OUT` and `OUT_FAULT`.

For example:

```java
@Bean
@CxfGlobalInterceptor(type = InterceptorType.OUT) (1)
my.awesome.in.Interceptor myAwesomeInInterceptor() {
    return new my.awesome.in.Interceptor();
}
```

or

```java
        @Bean
        @CxfSpecificInterceptor(type = InterceptorType.IN, applyFor = { WSInfo12PortType.class, CorruptedWSInfo12PortType.class }) (2)
        public WSS4JInInterceptor specificInterceptor() {
            return new WSS4JInInterceptor();
        }
```

1. Interceptor beans annotated with `@CxfGlobalInterceptor` will be applied to all found stubs.
2. Interceptor beans annotated with `@CxfSpecificInterceptor` will be applied to stubs specified in `applyFor` annotation member.

## Special thanks and authors

* Kirill Tolkachev [@tolkv](https://twitter.com/tolkv)
* Maxim Gorelikov [@gorelikoff](https://twitter.com/gorelikoff)
* Maxim Konshin
* Maxim Shatunov
* Lev Nikeshkin
* Shmakov Nikita
* Yulia Karavaeva
* Anton Fedosov
