package ru.alfalab.cxf.starter.test.utils


import org.w3c.dom.Document
import org.xml.sax.InputSource
import wiremock.org.custommonkey.xmlunit.XMLUnit

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import static org.codehaus.groovy.runtime.IOGroovyMethods.withCloseable

class DocumentWrapper {

    final Document document

    DocumentWrapper(Document document) {
        this.document = document
    }

    static DocumentWrapper parse(String xml) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance()
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder()

        return withCloseable(new StringReader(xml)) {
            new DocumentWrapper(docBuilder.parse(new InputSource(it)))
        }
    }

    @Override
    String toString() {
        def transformer = XMLUnit.getTransformerFactory().newTransformer()
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")

        return withCloseable(new StringWriter()) {
            transformer.transform(new DOMSource(document), new StreamResult(it))
            it.toString()
        }
    }

    String prettyPrint() {
        def transformer = XMLUnit.getTransformerFactory().newTransformer()
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", '2')

        return withCloseable(new StringWriter()) {
            transformer.transform(new DOMSource(document), new StreamResult(it))
            it.toString()
        }
    }


}
