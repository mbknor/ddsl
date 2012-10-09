import sbt._
import Keys._

object DdslCoreBuild extends Build {

  val mbknorGithubRepoUrl = "http://mbknor.github.com/m2repo/releases/"
  val typesafeRepoUrl = "http://repo.typesafe.com/typesafe/releases/"

  lazy val DdslCoreProject = Project(
    "ddsl",
    new File("."),
    settings = BuildSettings.buildSettings ++ Seq(
      libraryDependencies := Dependencies.runtime,
      publishMavenStyle := true,
      publishTo := Some(Resolvers.mbknorRepository),
      scalacOptions ++= Seq("-Xlint","-deprecation", "-unchecked","-encoding", "utf8"),
      javacOptions ++= Seq("-encoding", "utf8", "-g"),
      resolvers ++= Seq(DefaultMavenRepository, Resolvers.mbknorGithubRepo, Resolvers.typesafe)
    )
  )


  object Resolvers {
    val mbknorRepository = Resolver.ssh("my local mbknor repo", "localhost", "~/projects/mbknor/mbknor.github.com/m2repo/releases/")(Resolver.mavenStylePatterns)
    val mbknorGithubRepo = "mbknor github Repository" at mbknorGithubRepoUrl
    val typesafe = "Typesafe Repository" at typesafeRepoUrl
  }

  object Dependencies {

    val runtime = Seq(
      "org.scala-lang"          % "scala-library"     % "2.9.2",
      "org.apache.zookeeper"    % "zookeeper"         % "3.4.3" intransitive(),     
      "org.slf4j"               % "slf4j-api"         % "1.7.1",
      "ch.qos.logback"          % "logback-classic"   % "1.0.7"  % "test",
      "org.scalatest"          %% "scalatest"         % "1.8"    % "test",
      "junit"                   % "junit"             % "4.8.2"  % "test",
      "joda-time"               % "joda-time"         % "1.6.2",
      "commons-codec"           % "commons-codec"     % "1.4"
    )
  }


  object BuildSettings {

    val buildOrganization = "com.kjetland"
    val buildVersion      = "0.3.1"
    val buildScalaVersion = "2.9.2"
    val buildSbtVersion   = "0.12"

    val buildSettings = Defaults.defaultSettings ++ Seq (
      organization   := buildOrganization,
      version        := buildVersion,
      scalaVersion   := buildScalaVersion
    )

  }


}

