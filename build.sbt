name := "ConfigurationUtil"

version := "1.0"

organization := "home"

scalaVersion := "2.11.8"

publishTo := Some(Resolver.publishMavenLocal)

isSnapshot := true

libraryDependencies ++= Seq(
"org.specs2" %% "specs2" % "3.7" % Test
)

resolvers := Seq[Resolver](
"Typesafe maven Releases" at "https://dl.bintray.com/typesafe/maven-releases/",

"Typesafe Maven Repository" at "http://repo.typesafe.com/typesafe/maven-releases/",
//
"scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",

"Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
,
//
"Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
//
//"Typesafe Simple Repository" at "http://repo.typesafe.com/typesafe/simple/maven-releases/",
//
//"Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
"oss" at "https://oss.sonatype.org/service/local/repositories/releases/content/",

Resolver.mavenLocal
)


