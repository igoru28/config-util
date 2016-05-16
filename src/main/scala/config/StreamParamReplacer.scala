package config

import java.util.Properties
import StreamParamReplacer._
import scala.collection.JavaConverters._

/**
 * Author igor on 13.02.16.
 */

object StreamParamReplacer {
  val paramRegex = "(?<=\\$\\{)(.*?)(?=\\})".r
}

trait ParamResolver {
  def resolve(property: String, staticPropertiesMap: Map[String,String]):String = {
    staticPropertiesMap(property)
  }
}

trait ParamReplacer extends ParamResolver {
  def replace(stream: Stream[String], staticPropertiesMap: Map[String,String]): Stream[String] = {
    stream.map[String,Stream[String]] { line =>
      paramRegex.findAllIn(line).foldLeft(line){ case (str, param) =>
        str.replaceAllLiterally(s"$${${param}}", resolve(param, staticPropertiesMap))
      }
    }
  }
}

trait PropertyResolver extends ParamResolver {
  val properties: Properties

  override def resolve(property: String, staticPropertiesMap: Map[String,String]):String = {
    resolve(property, staticPropertiesMap, Set())
  }

  def resolve(property: String, staticPropertiesMap: Map[String,String], propertyVisitor: Set[String]):String = {
    if (propertyVisitor.contains(property)) {
      throw new IllegalStateException(s"Property $property has cyclic dependency")
    }
    val patternValue = Option(properties.getProperty(property)).getOrElse(staticPropertiesMap(property))
    val value = paramRegex.findAllIn(patternValue).foldLeft(patternValue){case (str, param) =>
      str.replaceAllLiterally(s"$${${param}}", resolve(param, staticPropertiesMap, propertyVisitor + property))
    }
    properties.put(property, value)
    value
  }
}

object PropertyResolver {
  def apply(javaProps: Properties): Map[String,String] = {
    val resolver = new PropertyResolver {
      override val properties: Properties = javaProps
    }
    javaProps.asScala.map{case (k,v) => k->resolver.resolve(k, Map())}.toMap
  }
}

object PlainStringReplacer extends ParamReplacer

object PropertiesReplacer extends ParamReplacer with PropertyResolver {

  override def replace(stream: Stream[String], staticPropertiesMap: Map[String,String]): Stream[String] = {
    properties.load(StringCollectionStreamReader(stream))
    super.replace(stream, staticPropertiesMap)
  }

  override val properties: Properties = new Properties()
}
