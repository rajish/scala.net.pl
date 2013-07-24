import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "scala_net_pl"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "com.typesafe.akka" %% "akka-actor" % "2.2.0",
    "com.typesafe.akka" %% "akka-testkit" % "2.2.0" % "test",
    "com.typesafe.slick" %% "slick" % "1.0.1"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
