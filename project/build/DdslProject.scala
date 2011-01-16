import sbt._

class DdslProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject
{
	val scalaSnapshotRepo = "scala snapshot" at "http://scala-tools.org/repo-snapshots/"


  //val mongo = "org.mongodb" % "mongo-java-driver" % "2.3"

  val zooKeeper = "org.apache.zookeeper" % "zookeeper" % "3.3.2"

  val log4j = "log4j" % "log4j" % "1.2.15" intransitive()

  override def ivyXML =
    <dependencies>
      <dependency org="org.apache.zookeeper" name="zookeeper" rev="3.3.2">
        <exclude module="log4j"/>
      </dependency>
    </dependencies>


	val scalatest = "org.scalatest" % "scalatest" % "1.2"
	val junit = "junit" % "junit" % "4.8.2"
	val jodaTime = "joda-time" % "joda-time" % "1.6.2"

  val codec = "commons-codec" % "commons-codec" % "1.4"
}