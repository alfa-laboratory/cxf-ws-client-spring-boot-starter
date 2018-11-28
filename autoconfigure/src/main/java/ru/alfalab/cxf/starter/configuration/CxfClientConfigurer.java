package ru.alfalab.cxf.starter.configuration;

import org.apache.cxf.endpoint.Client;
import ru.alfalab.cxf.starter.CxfClientsProperties.WSClient;

// tag::client-configurer-interface[]
public interface CxfClientConfigurer {
    void configure(Client client, WSClient clientDefinition);
}
// end::client-configurer-interface[]
