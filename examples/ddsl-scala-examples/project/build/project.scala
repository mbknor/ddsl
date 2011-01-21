import sbt._

class DdslScalaExamplesProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject
{
	val scalaSnapshotRepo = "scala snapshot" at "http://scala-tools.org/repo-snapshots/"


  val ddsl = "com.kjetland" % "ddsl_2.8.1" % "0.1"
}