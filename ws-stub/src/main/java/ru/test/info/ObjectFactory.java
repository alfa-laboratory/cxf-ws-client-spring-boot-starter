
package ru.test.info;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.test.info package.
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _WSInfoGetResponse_QNAME = new QName("http://WSInfo12.ws.test.ru", "WSInfoGetResponse");
    private final static QName _WSInfoGet_QNAME = new QName("http://WSInfo12.ws.test.ru", "WSInfoGet");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.test.info
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WSInfoGetResponse }
     * 
     */
    public WSInfoGetResponse createWSInfoGetResponse() {
        return new WSInfoGetResponse();
    }


    /**
     * Create an instance of {@link WSInfoGet }
     * 
     */
    public WSInfoGet createWSInfoGet() {
        return new WSInfoGet();
    }

    /**
     * Create an instance of {@link WSInfoGetResponseType }
     * 
     */
    public WSInfoGetResponseType createWSInfoGetResponseType() {
        return new WSInfoGetResponseType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WSInfoGetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://WSInfo12.ws.test.ru", name = "WSInfoGetResponse")
    public JAXBElement<WSInfoGetResponse> createWSInfoGetResponse(WSInfoGetResponse value) {
        return new JAXBElement<WSInfoGetResponse>(_WSInfoGetResponse_QNAME, WSInfoGetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WSInfoGet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://WSInfo12.ws.test.ru", name = "WSInfoGet")
    public JAXBElement<WSInfoGet> createWSInfoGet(WSInfoGet value) {
        return new JAXBElement<WSInfoGet>(_WSInfoGet_QNAME, WSInfoGet.class, null, value);
    }

}
