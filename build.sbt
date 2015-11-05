name := "TrAAllo"

description := "Trello clone/interpretation"

organization := "pl.lodz.p.iis"

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean, PlayEnhancer)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

libraryDependencies += "de.svenkubiak" % "jBCrypt" % "0.4"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
// Options: InjectedRoutesGenerator / StaticRoutesGenerator
// Used legacy static style - injected is not yet supported in IntelliJ
routesGenerator := StaticRoutesGenerator

// fix sbt resolve warn when import IDEA project:
// Multiple dependencies with the same organization/name but different versions
// To avoid conflict, pick one version
dependencyOverrides ++= Set(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scala-lang" % "scala-library" % scalaVersion.value,
  "org.scala-lang" % "scala-compiler"  % scalaVersion.value,

  "org.scala-lang.modules" % "scala-parser-combinators_2.11" % "1.0.4",
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.4",

  "org.apache.httpcomponents" % "httpclient" % "4.5.1",
  "org.apache.httpcomponents" % "httpcore" % "4.4.3",

  "com.typesafe" % "config" % "1.3.0",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.8",

  "commons-logging" % "commons-logging" % "1.2",
  "com.google.guava" % "guava" % "19.0-rc2",
  "junit" % "junit" % "4.12"
)

//update selenium, as bundled	2.44.0 was released 23-Oct-2014
dependencyOverrides ++= Set(
  "org.seleniumhq.selenium" % "selenium-api" % "2.48.2",
  "org.seleniumhq.selenium" % "selenium-chrome-driver" % "2.48.2",
  "org.seleniumhq.selenium" % "selenium-firefox-driver" % "2.48.2",
  "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.48.2",
  "org.seleniumhq.selenium" % "selenium-ie-driver" % "2.48.2",
  "org.seleniumhq.selenium" % "selenium-java" % "2.48.2",
  "org.seleniumhq.selenium" % "selenium-remote-driver" % "2.48.2",
  "org.seleniumhq.selenium" % "selenium-safari-driver" % "2.48.2",
  "org.seleniumhq.selenium" % "selenium-support" % "2.48.2"
)