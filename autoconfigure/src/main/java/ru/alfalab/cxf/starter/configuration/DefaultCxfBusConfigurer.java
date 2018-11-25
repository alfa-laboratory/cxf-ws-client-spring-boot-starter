package ru.alfalab.cxf.starter.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduitFactory;
import ru.alfalab.cxf.starter.CxfClientsProperties;

import java.util.Collections;
import java.util.List;

/**
 * @author tolkv
 * @since 08/04/16
 */
@RequiredArgsConstructor
public class DefaultCxfBusConfigurer implements CxfBusConfigurer {

  private static final List<LoggingInInterceptor>  LOGGING_IN_INTERCEPTORS   = Collections.singletonList(new LoggingInInterceptor());
  private static final List<LoggingOutInterceptor> LOGGING_OUT_INTERCEPTORS  = Collections.singletonList(new LoggingOutInterceptor());
  private static final List<LoggingInInterceptor>  LOGGING_IN_INTERCEPTORS1  = Collections.singletonList(new LoggingInInterceptor());
  private static final List<LoggingOutInterceptor> LOGGING_OUT_INTERCEPTORS1 = Collections.singletonList(new LoggingOutInterceptor());

  private final CxfClientsProperties cxfClientsProperties;

  @Override
  public void configure(Bus bus) {
    configureLogs(bus);
    configureClient(bus);
  }

  private void configureClient(Bus bus) {
      bus.setProperty(AsyncHTTPConduitFactory.USE_POLICY, cxfClientsProperties.getClient().getAsyncMode());
  }

  private void configureLogs(Bus bus) {
    if (!cxfClientsProperties.getClient().isLoggingEnabled())
      return;

    List<Interceptor<? extends Message>> inFaultInterceptors = bus.getInFaultInterceptors();
    List<Interceptor<? extends Message>> outFaultInterceptors = bus.getOutFaultInterceptors();

    List<Interceptor<? extends Message>> inInterceptors = bus.getInInterceptors();
    List<Interceptor<? extends Message>> outInterceptors = bus.getOutInterceptors();

    inInterceptors.addAll(LOGGING_IN_INTERCEPTORS);
    outInterceptors.addAll(LOGGING_OUT_INTERCEPTORS);

    inFaultInterceptors.addAll(LOGGING_IN_INTERCEPTORS1);
    outFaultInterceptors.addAll(LOGGING_OUT_INTERCEPTORS1);
  }

}
