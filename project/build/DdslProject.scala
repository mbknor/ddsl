import sbt._

class DdslProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject
{
	val scalaSnapshotRepo = "scala snapshot" at "http://scala-tools.org/repo-snapshots/"

  val mongo = "org.mongodb" % "mongo-java-driver" % "2.3"


	val scalatest = "org.scalatest" % "scalatest" % "1.2"
	val junit = "junit" % "junit" % "4.8.2"
	val jodaTime = "joda-time" % "joda-time" % "1.6.2"
}