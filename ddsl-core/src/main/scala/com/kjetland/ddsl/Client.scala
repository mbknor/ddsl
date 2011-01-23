package com.kjetland.ddsl

import exceptions.NoDDSLServiceLocationFoundException
import org.apache.log4j.Logger
import java.net.InetAddress
import org.joda.time.DateTime
import collection.mutable.HashMap
import com.kjetland.ddsl.model._
import com.kjetland.ddsl.config._
import com.kjetland.ddsl.dao._
import com.kjetland.ddsl.optimizing.SlListOptimizer

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/16/11
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */


//TODO: create option to "late-publish" servliceLocation if it fails initially




trait DdslClient {

  def serviceUp( s : Service) : Boolean
  def serviceDown( s : Service ) : Boolean

  @throws(classOf[NoDDSLServiceLocationFoundException])
  def getServiceLocations(sr : ServiceRequest) : Array[ServiceLocation]
  def disconnect()

  @throws(classOf[NoDDSLServiceLocationFoundException])
  def getBestServiceLocation(sr : ServiceRequest) : ServiceLocation = {
    val sls = getServiceLocations(sr)

    if( sls.isEmpty ) throw new NoDDSLServiceLocationFoundException
    
    //return the best sl on the top of the list
    sls(0)//should always at least contain one element - pick the first/best one

  }


}




class DdslClientImpl( config : DdslConfig) extends DdslClient{

  //default config is sys env
  def this() = this( new DdslConfigSysEnvReloading )

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



  @throws(classOf[NoDDSLServiceLocationFoundException])
  override def getServiceLocations(sr : ServiceRequest) : Array[ServiceLocation] = {
    try{
      log.info("Client "+sr.cid+" asking for Service "+sr.sid)
      val sls = dao.getSLs(sr.sid)



      val clientIp = checkAndResolveLocalIp( sr.cid.ip )

      val fixedSls = SlListOptimizer.optimize( clientIp, sls)

      if( fixedSls.size == 0) throw new NoDDSLServiceLocationFoundException("ZooKeeper works ok - but no available serviceLocation found")

      return fixedSls
    }catch{
      case e: Exception => {
        log.error("Error resolving ServiceLocations for '"+sr+"'. trying fallbacksollution.", e)
        return new DdslClientOnlyFallbackImpl(config).getServiceLocations( sr )
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

/**
 * Since we migt have a situation where our ddsl / zookeeper solution is broken or down
 * we have to have a solution where we can override it.
 *
 * This FallbackClient gets a path from sys env, then tries to load that property file.
 * then tries to look up the requested sid from this file. if success, this url is returned.
 *
 * It's critical that this FallbackClient impl logs a lot about what it is looking for (and possible using).
 *
 * This makes it possible, in an error situation, to look at the client logfile and see what to do, to update
 * the fallback info to make(hack) the system to work again...
 */
class DdslClientOnlyFallbackImpl( ddslConfig : DdslConfig) extends DdslClient {

  private val log = Logger.getLogger(getClass)

  override def serviceUp( s : Service) : Boolean = {
    log.info("Ignoring serviceUp: " + s)
    false
  }

  override def serviceDown( s : Service ) : Boolean = {
    log.info("Ignoring serviceDown: " + s)
    false
  }

  @throws(classOf[NoDDSLServiceLocationFoundException])
  override def getServiceLocations(sr : ServiceRequest) : Array[ServiceLocation] = {
    log.info("Looking up serviceLocation '"+sr.sid+"' in configFile")

    try{
      val sl = createFallbackSl( ddslConfig.getStaticUrls( sr.sid) )
      List(sl).toArray
    }catch{
      case e:Exception => {
        throw new NoDDSLServiceLocationFoundException("Failed to find serviceLocation for " + sr + " using fallback impl", e)
      }
    }
  }

  override def disconnect() = {}//nothing to do


  private def createFallbackSl( urls : DdslUrls ) : ServiceLocation = {
    ServiceLocation( urls.url, urls.testUrl, DdslDefaults.DEFAULT_QUALITY, new DateTime(), "unknown")
  }


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

  @throws(classOf[NoDDSLServiceLocationFoundException])
  override def getServiceLocations(sr : ServiceRequest) : Array[ServiceLocation] = realClient.getServiceLocations( sr)

  @throws(classOf[NoDDSLServiceLocationFoundException])
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