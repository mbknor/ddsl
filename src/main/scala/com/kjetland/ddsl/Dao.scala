package com.kjetland.ddsl

import org.joda.time.DateTime
import collection.mutable.HashMap
import scala.collection.JavaConversions._
import org.apache.zookeeper.data.ACL
import org.apache.log4j.Logger
import org.apache.zookeeper.{WatchedEvent, Watcher, CreateMode, ZooKeeper}
import org.apache.zookeeper.ZooDefs
import java.util.Properties

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/13/11
 * Time: 9:06 PM
 * To change this template use File | Settings | File Templates.
 */

case class ServiceId(environment : String, serviceType : String, name : String, version : String)
case class ServiceLocation( id: ServiceId, url : String, testUrl : String, quality : Double, up : Boolean, lastUpdated : DateTime)


object DdslDataConverter{

  val ddslDataVersion = "1.0"

  def getServiceLocationAsString( sl : ServiceLocation) : String = {
    val props = new Properties()

    

    return "test"
  }

  def getServiceLocationFromString( s : String) : ServiceLocation = {
    return ServiceLocation(null, null, null, 0.0, false,null)
  }


}

trait Dao{

  def update( sl : ServiceLocation)
  def getSLs(id : ServiceId) : ServiceLocation

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


    val statusPath = path + "/status"

    log.info("Writing status to path: " + statusPath)
    log.info("status: " + infoString)

    client.create( statusPath, infoString.getBytes("utf-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL )
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

  override def getSLs(id : ServiceId) : ServiceLocation = {

    throw new Exception("not impl yet");
  }




  def process( event: WatchedEvent){
    log.info("got watch: " + event)
  }

  

}




