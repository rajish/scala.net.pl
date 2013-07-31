import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "scala_net_pl"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.2.0",
    "com.typesafe.akka" %% "akka-testkit" % "2.2.0" % "test",
    "se.radley" % "play-plugins-salat_2.10" % "1.2",
    "org.slf4j" % "slf4j-api" % "1.7.2",
    "securesocial" %% "securesocial" % "master-SNAPSHOT"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "Typesafe Repository 2" at "http://repo.typesafe.com/typesafe/repo/",
    resolvers += Resolver.url("sbt-plugin-snapshots", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns),
    routesImport += "se.radley.plugin.salat.Binders._",
    templatesImport += "org.bson.types.ObjectId"
  )

}
