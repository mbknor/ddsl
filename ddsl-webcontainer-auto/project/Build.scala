import sbt._
import Keys._

object DdslWebcontainerAutoBuild extends Build {

  val mbknorGithubRepoUrl = "http://mbknor.github.com/m2repo/releases/"
  val typesafeRepoUrl = "http://repo.typesafe.com/typesafe/releases/"

  lazy val DdslWebcontainerAutoProject = Project(
    "ddsl-webcontainer-auto",
    new File("."),
    settings = BuildSettings.buildSettings ++ Seq(
      libraryDependencies := Dependencies.runtime,
      publishMavenStyle := true,
      publishTo := Some(Resolvers.mbknorRepository),
      scalacOptions ++= Seq("-Xlint","-deprecation", "-unchecked","-encoding", "utf8"),
      javacOptions ++= Seq("-encoding", "utf8", "-g"),
      resolvers ++= Seq(DefaultMavenRepository
        , Resolvers.mbknorRepository, Resolvers.mbknorGithubRepo, Resolvers.typesafe
        )
    )
  )


  object Resolvers {
    val mbknorRepository = Resolver.ssh("my local mbknor repo", "localhost", "~/projects/mbknor/mbknor.github.com/m2repo/releases/")(Resolver.mavenStylePatterns)
    val mbknorGithubRepo = "mbknor github Repository" at mbknorGithubRepoUrl
    val typesafe = "Typesafe Repository" at typesafeRepoUrl
  }

  object Dependencies {

    val runtime = Seq(
      "com.kjetland"           %% "ddsl"              % BuildSettings.buildVersion,
      "com.kjetland"           %% "webcontainer-info-extractor"   % "1.1",
      "javax.servlet"           % "servlet-api"       % "2.5"     % "provided"
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

