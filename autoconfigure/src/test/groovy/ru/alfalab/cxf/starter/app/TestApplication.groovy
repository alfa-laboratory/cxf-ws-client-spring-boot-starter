package ru.alfalab.cxf.starter.app

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import ru.alfalab.cxf.starter.annotation.EnableCxfClient
/**
 * @author tolkv
 * @since 05/04/16
 */
@EnableCxfClient
@SpringBootApplication
class TestApplication {
  static void main(String[] args) {
    SpringApplication.run(TestApplication)
  }
}
