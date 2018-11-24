package ru.alfalab.cxf.starter.test.utils

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.StubMapping

class FileResponseBuilder {

    ResponseDefinitionBuilder response
    String url
    List<String> requestXpath = []
    String soapAction

    /**
     * Creates WireMock stub for given url with 0 delay.
     * @param url url you want to create stub for
     */
    static FileResponseBuilder stubFor(String url) {
        stubFor(url, 0)
    }

    /**
     * Creates WireMock stub for given url with fixed delay.
     * @param url url you want to create stub for
     * @param fixedDelay response delay in ms
     */
    static FileResponseBuilder stubFor(String url, int fixedDelay) {
        def response = WireMock.aResponse()
                .withStatus(200)
                .withHeader('Content-Type', 'text/xml; charset=utf-8')
                .withTransformers('xpath-response-transformer')
                .withFixedDelay(fixedDelay)

        return new FileResponseBuilder(response: response, url: url)
    }


    /**
     * Sets response body equal to file content.
     * @param fileName path to file
     */
    FileResponseBuilder withFile(String fileName) {
        response = response.withBodyFile(fileName)
        return this
    }

    /**
     * Finish stub building.
     */
    StubMapping build() {
        def mappingBuilder = WireMock.post(WireMock.urlEqualTo(url))
        requestXpath.forEach {
            mappingBuilder = mappingBuilder.withRequestBody(WireMock.matchingXPath(it))
        }
        if (soapAction != null) {
            mappingBuilder.withHeader("SOAPAction", WireMock.containing(soapAction))
        }
        return WireMock.stubFor(mappingBuilder.willReturn(response))
    }


}
