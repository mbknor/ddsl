package com.kjetland.ddsl

import org.joda.time.DateTime
import collection.mutable.HashMap
import scala.collection.JavaConversions._
import org.apache.zookeeper.data.ACL
import org.apache.zookeeper.{CreateMode, ZooKeeper}


/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/13/11
 * Time: 9:06 PM
 * To change this template use File | Settings | File Templates.
 */

case class ServiceId(environment : String, serviceType : String, name : String, version : String)
case class ServiceLocation( id: ServiceId, url : String, testUrl : String, quality : Double, up : Boolean, lastUpdated : DateTime)

trait Dao{

  def update( sl : ServiceLocation)
  def getSLs(id : ServiceId) : ServiceLocation

}


class ZDao (val hosts : String) extends Dao {

  val sessionTimeout = 5*60*1000

  val basePath = "/ddsl/services/"

  private val client = new ZooKeeper(hosts, sessionTimeout, null)


  private def getSidPath( sid : ServiceId ) : String = {
    return basePath + sid.environment + "/" + sid.serviceType + "/" + sid.name + "/" + sid.version
  }



  override def update( sl : ServiceLocation) {
    
    val path = getSidPath(sl.id)
    val infoString = getServiceLocationAsString( sl )

    validateAndCreate( "/", path.split("/") )

    client.create( path + "/status", infoString.getBytes("utf-8"), List( new ACL()), CreateMode.EPHEMERAL )
  }

  private def validateAndCreate( parentPath : String, restPathParts : Array[String]) {

    val path = parentPath + restPathParts(0)

    if( client.exists(path, false) == null ){
      //must create it
      client.create( path, null, List(new ACL()), CreateMode.PERSISTENT)
    }


    val rest = restPathParts.toList.slice(1, restPathParts.length - 1)

    if( rest.length > 0 ){
      validateAndCreate( path + "/", rest.toArray)
    }

  }

  override def getSLs(id : ServiceId) : ServiceLocation = {

    throw new Exception("not impl yet");
  }


  private def getServiceLocationAsString( sl : ServiceLocation) : String = {
    return "test"
  }

  private def getServiceLocationFromString( s : String) : ServiceLocation = {
    return ServiceLocation(null, null, null, 0.0, false,null)
  }



  

}


