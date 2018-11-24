package ru.alfalab.cxf.starter.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import ru.test.info.WSInfo12PortType
import spock.lang.Specification

/**
 * @author tolkv
 * @version 10/03/2017
 */
@SpringBootTest(classes = [App])
class AppTest extends Specification {

  def 'should load context'() {
    expect: 'valid context '
    appContext

    and: 'WSInfo12 web service bean in context'
    WSInfo12PortType.isAssignableFrom appContext.getBean('WSInfo12PortType').class
  }

  @Autowired
  ApplicationContext appContext


  // tag::example-porttype[]
  @Autowired
  WSInfo12PortType info12PortType
  // end::example-porttype[]

  public static final String COMMON_WS_CONFIGURATION = 'common'
}
