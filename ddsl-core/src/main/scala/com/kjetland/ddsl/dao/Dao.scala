package com.kjetland.ddsl.dao

import org.joda.time.DateTime
import org.apache.zookeeper.{WatchedEvent, Watcher, CreateMode, ZooKeeper}
import org.apache.zookeeper.ZooDefs
import java.util.Properties
import org.joda.time.format.DateTimeFormat
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import org.apache.commons.codec.net.URLCodec
import org.apache.zookeeper.data.Stat
import scala.collection.JavaConversions._
import com.kjetland.ddsl.model._
import java.util.concurrent.atomic.AtomicBoolean
import org.slf4j.LoggerFactory

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/13/11
 * Time: 9:06 PM
 * To change this template use File | Settings | File Templates.
 */


object DdslDataConverter{

  val ddslDataVersion = "1.0"

  private val dtf = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss")

  def getServiceLocationAsString( sl : ServiceLocation) : String = {
    val props = new Properties()

    props.put("ddslDataVersion", ddslDataVersion)

    props.put( "url", sl.url)
    props.put( "quality", sl.quality.toString)
    props.put( "lastUpdated", dtf.print( sl.lastUpdated) )
    props.put( "ip", sl.ip )

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

    val sl = ServiceLocation(
      p.getProperty("url"),
      p.getProperty("quality").toDouble,
      dtf.parseDateTime(p.getProperty("lastUpdated")),
      p.getProperty("ip"))

    return sl
  }


}

trait Dao{

  def serviceUp( s : Service)
  def serviceDown( s : Service )
  def getSLs(id : ServiceId) : Array[ServiceLocation]

}


class ZDao (val hosts : String) extends Dao with Watcher {

  private val log = LoggerFactory.getLogger(getClass())


  val sessionTimeout = 5*60*1000

  val ddslServicesBasePath = "/ddsl/services"

  val basePath = ddslServicesBasePath + "/" 

  private val client = new ZooKeeper(hosts, sessionTimeout, this)

  private var haveDisconnected = new AtomicBoolean(false);

  registerShutdownHook()


  /**
   *Sometimes when killing an app by pressing ctrl+c, it's tcp connections
   * looks as they are still active for quite some time..
   * In our case this problem will fool zookeeper (for some time) to believe that our service
   * is still available after our server has quit..
   * To try to prevent this, we register a shutdown hook which (might) be called
   * when the JVM quit...
   */
  private def registerShutdownHook(){
    Runtime.getRuntime.addShutdownHook( new Thread {
      override def run{
        disconnect()
      }
    })
  }



  private def getSidPath( sid : ServiceId ) : String = {
    return basePath + sid.environment + "/" + sid.serviceType + "/" + sid.name + "/" + sid.version
  }

  /**
   * Returns a list of all available ServiceIds currently stored in the ddsl-network in zookeeper.
   */
  def getAllAvailableServices() : Array[ServiceWithLocations] = {

    def getLastPartOfString(path : String) : String = {
      val parts = path.split("/")
      return parts(parts.length - 1)
    }

    //first we look for for all environments
    val serviceIds = client.getChildren( ddslServicesBasePath, false).map{ a =>
      //got all environments
      client.getChildren( ddslServicesBasePath + "/"+a, false ).map{ b =>
        //got all types
        client.getChildren( ddslServicesBasePath + "/" + a + "/" + b, false ).map { c =>
          //got all services
          client.getChildren( ddslServicesBasePath + "/" + a + "/" + b + "/" + c, false ).map { d =>
            //got all versions
            ServiceId(a, b, c , d)
          }
        }.flatten

      }.flatten
    }.flatten


    //now w have the list of all serviceIds.
    //this list will contain all serviceIds ever stored in zookeeper networks..

    //we must find the locations
    val list = serviceIds.map { id =>
      ServiceWithLocations( id, getSLs(id))
    }.filter {
      swl : ServiceWithLocations =>
      //remove all with empty location-List
      swl.locations.length > 0
    }

    return list.toArray
    
  }

  def disconnect(){

    //using haveDisconnected to only try to disconnect once...
    if( haveDisconnected.compareAndSet(false, true )){
      log.info("Disconnecting from zookeeper - all services will be marked as offline")
      client.close
    }
  }


  override def serviceUp( s : Service) {
    
    val path = getSidPath(s.id)
    val infoString = DdslDataConverter.getServiceLocationAsString( s.sl )

    validateAndCreate( path )


    val statusPath = getSLInstancePath( path, s.sl)

    log.debug("Writing status to path: " + statusPath)


    //just check if it exsists - if it does delete it, then insert it.

    //TODO: is it possible to update instead of delete/create?
    val stat = client.exists( statusPath, false)
    if( stat != null ){
      log.debug("statusnode exists - delete it before creating it")
      try{
        client.delete(statusPath, stat.getVersion)
      }catch{
        case e:Exception => None // ignoring it..
      }
    }

    log.debug("status: " + infoString)
    

    client.create( statusPath, infoString.getBytes("utf-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL )
  }

  override def serviceDown( s : Service ) {

    val path = getSidPath(s.id)
    val statusPath = getSLInstancePath( path, s.sl)

    log.debug("trying to delete path: " + statusPath)
    val stat = client.exists( statusPath, false)
    if( stat != null ){
      log.debug("Deleting path: " + statusPath)
      try{
        client.delete( statusPath, stat.getVersion)
      }catch{
        case e: Exception => log.info("Error deleting path: " + statusPath, e)
      }

    }


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

    /**
     * Returns the string-content of a zookeeper file/path
     */
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
    }}.filter { _ != null} //remove all that was null (error while reading)

    

    return slList.toArray
  }




  def process( event: WatchedEvent){
    log.info("got watch: " + event)
  }

  

}




object ZDaoTestMain{

  def main ( args : Array[String]){
    val log = LoggerFactory.getLogger( getClass )
    try{
      doStuff()
    }catch{
      case x:Exception => log.error("error", x)
    }


  }

  def doStuff() {
    println("hw")

    val hosts = "localhost:2181"
    val dao = new ZDao( hosts )


    val sid = ServiceId("test", "http", "testService", "1.0")
    val sl = ServiceLocation("http://localhost/url", 10.0, new DateTime(), "127.0.0.1")
    Thread.sleep( 100 )
    val sl2 = ServiceLocation("http://localhost:90/url", 9.0, new DateTime(), "127.0.0.1")

    val s = Service(sid,sl)

    dao.serviceUp( s )
    dao.serviceUp( Service(sid,sl2) )

    println(">>start list")
    dao.getSLs( sid).foreach{println( _ )}
    println("<<end list")


    println(">>start getAllAvailableServices-list")
    dao.getAllAvailableServices().foreach{ println(_)}
    println("<<end getAllAvailableServices-list")

    dao.serviceDown( s )

    println(">>start list")
    dao.getSLs( sid).foreach{println( _ )}
    println("<<end list")

    Thread.sleep(100000)
  }
}