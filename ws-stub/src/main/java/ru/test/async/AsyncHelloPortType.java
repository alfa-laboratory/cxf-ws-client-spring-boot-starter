package ru.test.async;

import java.util.concurrent.Future;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

/**
 * This class was generated by Apache CXF 3.2.2
 * 2018-04-25T15:12:24.189+03:00
 * Generated source version: 3.2.2
 *
 */
@WebService(targetNamespace = "http://async.test.ru/", name = "AsyncHello_PortType")
@SOAPBinding(use = SOAPBinding.Use.ENCODED, parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface AsyncHelloPortType {

    @WebMethod(operationName = "sayHello")
    public Response<java.lang.String> sayHelloAsync(
        @WebParam(partName = "firstName", name = "firstName", targetNamespace = "")
        java.lang.String firstName
    );

    @WebMethod(operationName = "sayHello")
    public Future<?> sayHelloAsync(
        @WebParam(partName = "firstName", name = "firstName", targetNamespace = "")
        java.lang.String firstName,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<java.lang.String> asyncHandler
    );

    @WebMethod(action = "sayHello")
    @WebResult(name = "greeting", targetNamespace = "", partName = "greeting")
    public java.lang.String sayHello(
        @WebParam(partName = "firstName", name = "firstName", targetNamespace = "")
        java.lang.String firstName
    );
}
