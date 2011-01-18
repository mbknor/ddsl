package com.kjetland.ddsl

import org.apache.log4j.Logger
import java.net.InetAddress
import org.joda.time.DateTime

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/16/11
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */


trait DdslClient {

  def serviceUp( s : Service) : Boolean
  def serviceDown( s : Service ) : Boolean
  def getServiceLocations(sr : ServiceRequest) : Array[ServiceLocation]
  def getBestServiceLocation(sr : ServiceRequest) : ServiceLocation
  def disconnect()

}

//TODO: Create conveniance client that cashed result for some time..

//TODO: create another higherlevel failover impl:
// which uses presupplied url if no ddsl-config is pressent
//not sure if that is needed - it would work to use the regular override mechanism..


class DdslClientImpl(hosts : String) extends DdslClient{

  def this() = this( null )

  private val log = Logger.getLogger( getClass )

  val hostsEnvName = "ZOOKEEPER_HOSTS"

  val dao = new ZDao( verifyHosts(hosts) )


  private def verifyHosts( hosts : String) : String = {

    val hostsToUse = if( hosts == null ){
      log.info("Hosts not specified. Reading from system environment variable '"+hostsEnvName+"'")
      //first try java properties
      val hostsFromEnv = System.getProperty( hostsEnvName) match {
        case s : String => s
        case _ => System.getenv( hostsEnvName )
      }

      if( hostsFromEnv == null ){
        throw new Exception("Cannot continue without hosts-list: not supplied and not found in system env '"+hostsEnvName+"'")
      }
      hostsFromEnv
      
    }else{
      hosts
    }

    log.info("Using hostslist: " + hostsToUse)

    hostsToUse

  }


  override def serviceUp( service : Service) : Boolean = {
    try{
      val s = checkAndFillInHostIp( service )
      log.info("Marking service up: " + s)
      dao.serviceUp( s )
      return true
    }catch{
      case e : Exception => {
        log.error("Error marking service '"+service+"' as up", e)
        return false
      }
    }
  }

  override def serviceDown( service : Service ) : Boolean = {
    try{
      val s = checkAndFillInHostIp( service )
      log.info("Marking service down: " + s)
      dao.serviceDown( s )
      return true
    }catch{
      case e: Exception => {
        log.error("Error marking service '"+service+"' as down", e)
        return false
      }
    }
  }


  private def checkAndFillInHostIp(s : Service) : Service = {

    return if( s.sl.ip == null ){
      log.info("Must resolve local ip")
      val ip = checkAndResolveLocalIp( null )
      val newSl = s.sl.copy( ip = ip)
      Service(s.id, newSl)
    }else{
      s
    }
  }

  private def checkAndResolveLocalIp( ip : String) : String = {

    return if( ip == null ){
      val resolvedIp = InetAddress.getLocalHost().getHostAddress
      log.info("Resolved local ip: " + resolvedIp)
      resolvedIp
    }else{
      ip
    }

  }




  override def getServiceLocations(sr : ServiceRequest) : Array[ServiceLocation] = {
    try{
      log.info("Client "+sr.cid+" asking for Service "+sr.sid)
      val sls = dao.getSLs(sr.sid)



      val clientIp = checkAndResolveLocalIp( sr.cid.ip )

      val fixedSls = SlListOptimizer.optimize( clientIp, sls)

      //log.info("ServiceLocations: " + fixedSls)

      //TODO: if we get no error but no result we should try fallback client

      return fixedSls
    }catch{
      case e: Exception => {
        log.error("Error resolving ServiceLocations for '"+sr+"'. trying fallbacksollution.", e)
        return FallbackClient.resolveServiceLocations( sr )
      }
    }
  }

  override def getBestServiceLocation(sr : ServiceRequest) : ServiceLocation = {
    val sls = getServiceLocations(sr)
    if( sls.isEmpty ){
      null
    }else{
      sls(0)
    }
  }

  override def disconnect() {
    try{
      log.info("Disconnecting")
      dao.disconnect
    }catch{
      case e : Exception => log.error("Error disconnecting", e)
    }
  }


}


