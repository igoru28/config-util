package config

import java.io.{BufferedReader, InputStreamReader}
import java.util.Properties

import io.Finalizer

import scala.collection.JavaConverters._
import scala.util.matching.Regex


/**
 * Author igor on 29.01.16.
 */
case class ApplicationPropertiesTemplate(name: String, config: Map[String, ParamReplacer])

object ConfigUtil {

  lazy val app1 = ApplicationPropertiesTemplate("app1", Map(
    "config.yaml" -> PlainStringReplacer,
    "subdir/job.properties" -> PropertiesReplacer
  ))
  lazy val springBootWebApp = ApplicationPropertiesTemplate("app2", Map(
    "ext.properties" -> PropertiesReplacer
  ))
  val paramRegex = "(?<=\\$\\{)(.*?)(?=\\})".r
  val environments = List("env1", "env2", "env3")
  val defaultValues = Map(
    "macros1" -> "macros1Value",
    "macros1.1" -> "${macros1}/add",
    "macros2" -> "macros2Value",
    "macros3" -> "macros3Value",
    "deployPath" -> "target/deploy"
  )

  def configuration(env: String, userConfig: Map[String, String] = Map()): Map[String, String] = {
    val properties = new Properties
    val configPropertiesResource = "conf/" + env + ".properties"
    val in = ConfigUtil.getClass.getClassLoader.getResourceAsStream(configPropertiesResource)
    if (in == null) {
      throw new IllegalStateException("Cannot find resource " + configPropertiesResource)
    }
    properties.load(in)
    defaultValues.map { case (k, v) => properties.putIfAbsent(k, v) }
    PropertyResolver(properties.asScala.toMap ++ userConfig)
  }

  def getConfig(params: Map[String, String], app: ApplicationPropertiesTemplate): Map[String, Stream[String]] = {
    app.config.map { case (fileName, resolver) =>
      val br: BufferedReader = new BufferedReader(
        new InputStreamReader(ClassLoader.getSystemClassLoader.getResourceAsStream("template/" + app.name + "/" + fileName))
      )
      fileName -> resolver.replace(Stream.continually(
        Finalizer[String](br.readLine, _ == null, br.close)
      ).takeWhile(_ != null), params)
    }
  }

  private def replaceParams(s: String, conf: Map[String, String]): String = {
    paramRegex.findAllIn(s).foldLeft(s) { case (str, param) =>
      str.replaceAllLiterally(s"$${${param}}", conf(param))
    }
  }
}
