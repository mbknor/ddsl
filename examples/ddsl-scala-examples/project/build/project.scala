import sbt._

class DdslScalaExamplesProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject
{
	val scalaSnapshotRepo = "scala snapshot" at "http://scala-tools.org/repo-snapshots/"


  //TODO: must make sure we use the same version here as in core
  val ddsl = "com.kjetland.ddsl" % "ddsl_2.8.1" % "0.1-RC1"
}