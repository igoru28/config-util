package config

import org.specs2.mutable.Specification

/**
 * Author igor on 15.05.16.
 */
class ConfigUtilSpec extends Specification {
  "ConfigUtil" should {
    "have configuration for each environment" in {
      ConfigUtil.environments.map{ env =>
        (ConfigUtil.configurations.keySet should contain(env)) and
          (ConfigUtil.configurations(env).keys should not beEmpty)
      }.reduce(_ and _)
    }
  }

}
