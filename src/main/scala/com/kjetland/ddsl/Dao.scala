package com.kjetland.ddsl

import org.joda.time.DateTime
import org.apache.log4j.Logger
import org.apache.zookeeper.{WatchedEvent, Watcher, CreateMode, ZooKeeper}
import org.apache.zookeeper.ZooDefs
import java.util.Properties
import org.joda.time.format.DateTimeFormat
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import org.apache.commons.codec.net.URLCodec
import org.apache.zookeeper.data.Stat
import scala.collection.JavaConversions._

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/13/11
 * Time: 9:06 PM
 * To change this template use File | Settings | File Templates.
 */

case class ServiceId(environment : String, serviceType : String, name : String, version : String)
case class ServiceLocation( id: ServiceId, url : String, testUrl : String, quality : Double, lastUpdated : DateTime)



object DdslDataConverter{

  val ddslDataVersion = "1.0"

  private val dtf = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss")

  def getServiceLocationAsString( sl : ServiceLocation) : String = {
    val props = new Properties()

    props.put("ddslDataVersion", ddslDataVersion)

    props.put( "environment", sl.id.environment)
    props.put( "serviceType", sl.id.serviceType)
    props.put( "name", sl.id.name)
    props.put( "version", sl.id.version)
    
    props.put( "url", sl.url)
    props.put( "testUrl", sl.testUrl)
    props.put( "quality", sl.quality.toString)
    props.put( "lastUpdated", dtf.print( sl.lastUpdated) )

    val buffer = new ByteArrayOutputStream
    props.store( buffer, "")


    return buffer.toString
  }

  def getServiceLocationFromString( s : String) : ServiceLocation = {
    val buffer = new ByteArrayInputStream(s.getBytes("iso-8859-1"))
    val p = new Properties
    p.load(buffer)

    val readDdslDataVersion = p.get("ddslDataVersion")

    if( !ddslDataVersion.equals( readDdslDataVersion)){
      throw new Exception("Incompatible dataVersion. programVersion: " + ddslDataVersion + " readVersion: " + readDdslDataVersion)
    }

    val id = ServiceId(p.getProperty("environment"), p.getProperty("serviceType"), p.getProperty("name"), p.getProperty("version"))

    new DateTime()

    val sl = ServiceLocation(id, p.getProperty("url"), p.getProperty("testUrl"), p.getProperty("quality").toDouble, dtf.parseDateTime(p.getProperty("lastUpdated")))

    return sl
  }


}

trait Dao{

  def update( sl : ServiceLocation)
  def getSLs(id : ServiceId) : Array[ServiceLocation]

}


class ZDao (val hosts : String) extends Dao with Watcher {

  private val log = Logger.getLogger(getClass())


  val sessionTimeout = 5*60*1000

  val basePath = "/ddsl/services/"

  private val client = new ZooKeeper(hosts, sessionTimeout, this)


  private def getSidPath( sid : ServiceId ) : String = {
    return basePath + sid.environment + "/" + sid.serviceType + "/" + sid.name + "/" + sid.version
  }



  override def update( sl : ServiceLocation) {
    
    val path = getSidPath(sl.id)
    val infoString = DdslDataConverter.getServiceLocationAsString( sl )

    validateAndCreate( path )


    val statusPath = getSLInstancePath( path, sl)

    log.info("Writing status to path: " + statusPath)
    log.info("status: " + infoString)

    client.create( statusPath, infoString.getBytes("utf-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL )
  }

  private def getSLInstancePath( sidPath : String, sl : ServiceLocation) : String = {
    //url is the key to this instance of this service
    //it must be urlencoded to be a valid path-node-name
    val encodedUrl = new URLCodec().encode(sl.url)

    return sidPath + "/" + encodedUrl

  }

  private def validateAndCreate( path : String) {

    //skip the first /
    val pathParts = path.substring(1).split("/")

    validateAndCreate( "/", pathParts )
  }

  private def validateAndCreate( parentPath : String, restPathParts : Array[String]) {

    val path = parentPath + restPathParts(0)

    log.debug("Checking path: " + path)

    if( client.exists(path, false) == null ){

      log.debug("Creating path: " + path)
      //must create it
      client.create( path, Array(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
    }


    val rest = restPathParts.toList.slice(1, restPathParts.length)

    if( rest.length > 0 ){
      validateAndCreate( path + "/", rest.toArray)
    }

  }

  override def getSLs(id : ServiceId) : Array[ServiceLocation] = {

    def getInfoString( path : String ) : String = {

      try{
        val stat = new Stat
        val bytes = client.getData( path, false, stat)
        new String( bytes, "utf-8")
      }catch{
        case _ => null //return null if error - node might have gone offline since we created the list
      }

    }

    val sidPath = getSidPath( id )

    val slList = client.getChildren( sidPath, false).map { path : String => {

      val string = getInfoString( sidPath+"/"+path )

      if( string != null ) {
        DdslDataConverter.getServiceLocationFromString( string )
      }else{
        null
      }
    }}.filter { _ != null}

    

    return slList.toArray
  }




  def process( event: WatchedEvent){
    log.info("got watch: " + event)
  }

  

}




