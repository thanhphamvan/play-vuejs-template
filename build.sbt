import com.typesafe.sbt.packager.MappingsHelper._

mappings in Universal ++= directory(baseDirectory.value / "public")

name := "play-vue-webpack-typescript"

version := "1.1"

scalaVersion := "2.12.3"

lazy val `play-vue-webpack` = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  filters,
  jdbc,
  cacheApi,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test,
  "com.github.etaty"       %% "rediscala"           % "1.8.0",
  "org.reactivemongo"      %% "play2-reactivemongo" % "0.12.6-play26",
  "io.swagger"             %% "swagger-play2"       % "1.6.0",
  "org.webjars"             % "swagger-ui"          % "3.1.5"
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
