package ru.alfalab.cxf.starter.factory

interface ClientSpringPropertiesConstants {
    List<String> CXF_ALL_CLIENTS_VARARG = ["spring.cxf.clients[0].endpoint=http://SOME_HOST/SOME_PATH_TO_WS",
                                           "spring.cxf.clients[0].className=ru.test.async.AsyncHelloPortType",
                                           "spring.cxf.clients[1].endpoint=http://SOME_HOST/SOME_PATH_TO_WS",
                                           "spring.cxf.clients[1].className=ru.test.info.skip.SkippedWSInfo12PortType",
                                           "spring.cxf.clients[2].endpoint=http://SOME_HOST/SOME_PATH_TO_WS",
                                           "spring.cxf.clients[2].className=ru.test.info.WSInfo12PortType",
                                           "spring.cxf.clients[3].endpoint=http://SOME_HOST/SOME_PATH_TO_WS",
                                           "spring.cxf.clients[3].className=ru.test.info.CorruptedWSInfo12PortType",
    ]
}
