package ru.alfalab.cxf.starter

import org.junit.Rule
import org.springframework.beans.factory.BeanCreationException
import org.springframework.boot.SpringApplication
import org.springframework.boot.test.rule.OutputCapture
import ru.alfalab.cxf.starter.app.TestApplication
import spock.lang.Specification

/**
 * @author tolkv
 * @since 05/04/16
 */
class CxfAutoConfigurationCorruptedSpec extends Specification {
  public static final String ERROR_MESSAGE_PART_WITH_ADVICE = 'className: ru.test.info.CorruptedWSInfo12PortType'

  @Rule
  public OutputCapture capture = new OutputCapture()

  def 'should explain why'() {
    when:
    SpringApplication.run(TestApplication)

    then:
    def e = thrown(BeanCreationException)
    e.getMessage() contains ERROR_MESSAGE_PART_WITH_ADVICE
    capture.toString() contains ERROR_MESSAGE_PART_WITH_ADVICE
  }
}
