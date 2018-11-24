package ru.alfalab.cxf.starter.test.utils

import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseTransformer
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.Response
import groovy.transform.CompileStatic
import org.w3c.dom.NodeList

import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

@CompileStatic
class SoapTransformder extends ResponseTransformer {

    @Override
    String getName() {
        return "xpath-response-transformer"
    }

    @Override
    Response transform(Request request, Response response, FileSource files, Parameters parameters) {
        if (parameters == null) {
            return response
        }

        String xml = response.getBodyAsString()

        DocumentWrapper doc = DocumentWrapper.parse(xml)
        XPath xpath = XPathFactory.newInstance().newXPath()

        parameters.entrySet().forEach { Map.Entry<String, Object> it ->
            NodeList nodes = (NodeList) xpath.evaluate(it.key, doc.document, XPathConstants.NODESET)
            for (int idx = 0; idx < nodes.getLength(); idx++) {
                nodes.item(idx).setTextContent(it.value as String)
            }
        }

        return Response.Builder
                .like(response)
                .body(doc.toString())
                .build()
    }
}
