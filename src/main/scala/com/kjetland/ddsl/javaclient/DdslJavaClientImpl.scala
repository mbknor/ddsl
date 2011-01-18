package com.kjetland.ddsl.javaclient

import org.joda.time.DateTime
import com.kjetland.ddsl._

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/17/11
 * Time: 2:39 PM
 * To change this template use File | Settings | File Templates.
 */

class DdslJavaClientImpl( hosts : String){

  def this() = this(null)

  private val c = new DdslClientImpl( hosts )

  implicit private def sidj2sid( s :ServiceIdJava) = {
    ServiceId(s.environment, s.serviceType, s.name, s.version)
  }

  implicit private def cidj2cid( c : ClientIdJava) = {
    ClientId( c.environment, c.name, c.version, c.ip)
  }

  implicit private def slj2sl( s : ServiceLocationJava) = {
    ServiceLocation(s.url, s.testUrl, s.quality, s.lastUpdated, s.ip)
  }


  def serviceUp( sid : ServiceIdJava, sl:ServiceLocationJava) : Boolean = c.serviceUp( Service(sid, sl) )
  def serviceDown( sid : ServiceIdJava, sl:ServiceLocationJava) : Boolean = c.serviceDown( Service(sid, sl) )

  def getServiceLocations(sid : ServiceIdJava, cid : ClientIdJava) : Array[ServiceLocationJava] = {
    val srs = c.getServiceLocations( ServiceRequest( sid, cid))

    return srs.map( {sl : ServiceLocation =>
      sl2slj(sl)} ).toArray
  }

  def getBestServiceLocation(sid : ServiceIdJava, cid : ClientIdJava) : ServiceLocationJava = {
    val sl = c.getBestServiceLocation( ServiceRequest(sid, cid) )
    return sl2slj( sl)
  }

  def disconnect() = c.disconnect



  private def sl2slj(sl : ServiceLocation) : ServiceLocationJava = {
    new ServiceLocationJava(sl.url, sl.testUrl, sl.quality, sl.lastUpdated, sl.ip)
  }




}


//case class ServiceId(environment : String, serviceType : String, name : String, version : String)
class ServiceIdJava(val environment : String, val serviceType : String, val name : String, val version : String){

  //create getters for java
  def getEnvironment() = environment
  def getServiceType() = serviceType
  def getName() = name
  def getVersion() = version

  def getCC = ServiceId( environment, serviceType, name, version )

  override def toString() = getCC.toString
}

//
//case class ServiceLocation( url : String, testUrl : String, quality : Double, lastUpdated : DateTime, ip : String)
class ServiceLocationJava(val url : String, val testUrl : String, val quality : Double, val lastUpdated : DateTime, val ip : String){

  //create getters for java
  def getUrl() = url
  def getTestUrl() = testUrl
  def getQuality() = quality
  def getLastUpdated() = lastUpdated
  def getIp() = ip

  def getCC = ServiceLocation(url, testUrl, quality, lastUpdated, ip)

  override def toString() = getCC.toString
  

}
//case class ClientId(environment : String, name : String, version : String, ip : String)
class ClientIdJava(val environment : String, val name : String, val version : String, val ip : String){

  //create getters for java
  def getEnvironment() = environment
  def getName() = name
  def getVersion() = version
  def getIp() = ip

  def getCC = ClientId(environment, name, version, ip)

  override def toString = getCC.toString
}
