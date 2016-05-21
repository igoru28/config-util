package config

import org.specs2.mutable.Specification

/**
 * Author igor on 15.05.16.
 */
class ConfigUtilSpec extends Specification {
  "ConfigUtil" should {
    "have configuration for each environment" in {
      ConfigUtil.environments.map{ env =>
        val config = ConfigUtil.configuration(env)
        config must not beEmpty
      }.reduce(_ and _)
    }

    "override env props with user defined values" in {
      val userDefinedKey = "sect1.prop11"
      val userDefinedValueStr = "user defined value"
      val userConf = Map(userDefinedKey -> userDefinedValueStr)
      ConfigUtil.environments.map { env =>
        val config = ConfigUtil.configuration(env, userConf)
        (config.keySet must contain(userDefinedKey)) and
          (config(userDefinedKey) must beEqualTo(userDefinedValueStr))
      }.reduce(_ and _)
    }

    "make config for apps" in {
      Vector(ConfigUtil.app1, ConfigUtil.springBootWebApp).map{ appTemplate =>
        ConfigUtil.environments.map{ env =>
          val config = ConfigUtil.configuration(env)
          val app1Config = ConfigUtil.getConfig(config, ConfigUtil.app1)
          (app1Config must not beEmpty) and
            (app1Config.map{ case (confFile, stream) =>
                val conf = stream.mkString("\n")
                conf must not beEmpty
            }.reduce(_ and _))
        }.reduce(_ and _)
      }.reduce(_ and _)
    }
  }

}
