import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) with IdeaProject
{
	val scalaSnapshotRepo = "scala snapshot" at "http://scala-tools.org/repo-snapshots/"

  val ddsl = "com.kjetland" % "ddsl_2.8.1" % "0.1"
  val wc_info_e = "com.kjetland"% "webcontainer-info-extractor_2.8.1" % "1.0"

  val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
  
}
