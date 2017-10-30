// Written by ThanhPV (at) HUS, based on play-webpack-template and vuejs-typescript-template

import com.typesafe.sbt.packager.MappingsHelper._

mappings in Universal ++= directory(baseDirectory.value / "public")

name := "play-vue-webpack-typescript"

version := "1.1"

scalaVersion := "2.12.4"  // Recent: Changed from 2.12.3 to 2.12.4 - 27/10

lazy val `play-vue-webpack` = (project in  file(".")).enablePlugins(PlayJava, PlayScala, PlayEbean)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % "2.12.4",
  evolutions, // Play's plugin for managing RDBMS, see more at www.playframework.com/documentation/2.6.x/Evolutions
  javaJdbc,
  guice,      // Google's Guide for Dependencies Injection Pattern
  filters,
  jdbc,
  cacheApi,
  ws,         // WebSocket
  "org.scalatestplus.play" %% "scalatestplus-play"  % "3.1.0" % Test, // The Scala Test Library
  "com.github.etaty"       %% "rediscala"           % "1.8.0",        // Scala Redis wrapper Library
  "org.reactivemongo"      %% "play2-reactivemongo" % "0.12.6-play26",// Scala MongoDB wrapper Library
  "io.swagger"             %% "swagger-play2"       % "1.6.0",        // Swagger - RestAPIs Docs Generator
  "org.webjars"             % "swagger-ui"          % "3.1.5",        // Swagger's UI
  "mysql"                   % "mysql-connector-java"% "6.0.6"         // Released date: Feb 23, 2017
)

// Play framework hooks for development
PlayKeys.playRunHooks += WebpackServer(file("./front"))

unmanagedResourceDirectories in Test += baseDirectory(_ / "target/web/public/test").value

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

// Production front-end build
lazy val cleanFrontEndBuild = taskKey[Unit]("Remove the old front-end build")

cleanFrontEndBuild := {
  val d = file("public/bundle")
  if (d.exists()) {
    d.listFiles.foreach(f => {
      if (f.isFile) f.delete
    })
  }
}

lazy val frontEndBuild = taskKey[Unit]("Execute the npm build command to build the front-end")

frontEndBuild := {
  println(Process("npm install", file("front")).!!)
  println(Process("npm run build", file("front")).!!)
}

frontEndBuild := (frontEndBuild dependsOn cleanFrontEndBuild).value

dist := (dist dependsOn frontEndBuild).value

import play.sbt.routes.RoutesKeys

RoutesKeys.routesImport += "play.modules.reactivemongo.PathBindables._"
