package ru.alfalab.cxf.starter.configuration;

import org.apache.cxf.Bus;

/**
 * @author tolkv
 * @since 08/04/16
 */
public interface CxfBusConfigurer {
  void configure(Bus bus);
}
