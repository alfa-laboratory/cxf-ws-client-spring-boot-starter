package ru.alfalab.cxf.starter


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import ru.alfalab.cxf.starter.app.TestApplication
import ru.alfalab.cxf.starter.test.utils.ValidSpecification
import ru.test.info.WSInfo12PortType
import ru.test.info.skip.SkippedWSInfo12PortType

/**
 * @author tolkv
 * @since 05/04/16
 */
@SpringBootTest(classes = [TestApplication])
class CxfAutoConfigurationSpec extends ValidSpecification {
  @Autowired
  ApplicationContext applicationContext

  @Autowired
  WSInfo12PortType info12PortType

  @Autowired(required = false)
  SkippedWSInfo12PortType skipWSInfo12PortType

  def 'should load context and some ws clients'() {
    expect: 'cxf bus should exist in application context'
    applicationContext.getBean 'cxf'

    and: 'should init cxf client for WSInfo12PortType interface'
    WSInfo12PortType.isAssignableFrom applicationContext.getBean('ru.test.info.WSInfo12PortType').class

    and: 'should inject port type bean into test class'
    info12PortType

    and: 'should ignore port type from skip package list'
    skipWSInfo12PortType == null

  }
}
