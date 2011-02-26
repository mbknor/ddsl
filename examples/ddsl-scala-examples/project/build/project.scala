import sbt._

class DdslScalaExamplesProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject
{
	val scalaSnapshotRepo = "scala snapshot" at "http://scala-tools.org/repo-snapshots/"


  val log4j = "log4j" % "log4j" % "1.2.15" intransitive()

  //TODO: must make sure we use the same version here as in core
  val mbknorRepo = "mbknor repo" at "http://mbknor.github.com/m2repo/releases"
  val ddsl = "com.kjetland.ddsl" % "ddsl_2.8.1" % "0.2"



}