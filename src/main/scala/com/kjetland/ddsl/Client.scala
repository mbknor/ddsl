package com.kjetland.ddsl

import org.apache.log4j.Logger
import java.net.InetAddress
import org.joda.time.DateTime
import collection.mutable.HashMap

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
  def disconnect()

  def getBestServiceLocation(sr : ServiceRequest) : ServiceLocation = {
    getServiceLocations(sr)(0)//should always at least contain one element - pick the first/best one

  }


}




class DdslClientImpl( config : DdslConfig) extends DdslClient{

  //default config is sys env
  def this() = this( new DdslConfigSysEnvImpl )

  private val log = Logger.getLogger( getClass )

  val dao = new ZDao( config.hosts )

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

      if( fixedSls.size == 0) throw new Exception("ZooKeeper works ok - but no available serviceLocation found")

      return fixedSls
    }catch{
      case e: Exception => {
        log.error("Error resolving ServiceLocations for '"+sr+"'. trying fallbacksollution.", e)
        return FallbackClient.resolveServiceLocations( sr )
      }
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


class DdslClientOnlyFallbackImpl extends DdslClient {

  private val log = Logger.getLogger(getClass)

  override def serviceUp( s : Service) : Boolean = {
    log.info("Ignoring serviceUp: " + s)
    false
  }

  override def serviceDown( s : Service ) : Boolean = {
    log.info("Ignoring serviceDown: " + s)
    false
  }

  override def getServiceLocations(sr : ServiceRequest) : Array[ServiceLocation] = {
    FallbackClient.resolveServiceLocations( sr )
  }

  override def disconnect() = {}//nothing to do

}


/**
 * This is a cache that caches read results for some time..
 * Convenient to use when you don't want to lookup serviceLocation
 * all the time put Don't want to mess with when to refresh serviceLocations
 */
class DdslClientCacheReadsImpl( realClient : DdslClient, ttl_mills : Long) extends DdslClient {

  private val log = Logger.getLogger( getClass )

  var lastCacheClear  = System.currentTimeMillis
  val cache = new HashMap[String, ServiceLocation]



  override def serviceUp( s : Service) : Boolean = realClient.serviceUp( s )

  override def serviceDown( s : Service ) : Boolean = realClient.serviceDown( s )

  override def getServiceLocations(sr : ServiceRequest) : Array[ServiceLocation] = realClient.getServiceLocations( sr)

  override def getBestServiceLocation(sr : ServiceRequest) : ServiceLocation = {
    checkAndInvalidateCache

    //look in cache
    val key = sr.toString

    cache.get( key ) match {
      case Some(sl : ServiceLocation) => {
        log.debug("Returning cached result for " + sr + ": " + sl)
        sl
      }
      case None => {
        log.debug( "Querying real sl for " + sr)
        val sl = realClient.getBestServiceLocation( sr )
        log.debug( "Got real sl for " + sr + ": " + sl)
        sl

      }
    }
  }

  private def checkAndInvalidateCache {
    val now = System.currentTimeMillis
    val millsSinceLastCacheClear = now - lastCacheClear
    if( millsSinceLastCacheClear > ttl_mills ){
      log.info("Clearing cache")
      cache.clear
      lastCacheClear = System.currentTimeMillis
    }
  }
  
  override def disconnect() = realClient.disconnect

}