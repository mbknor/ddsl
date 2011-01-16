package com.kjetland.ddsl

import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/16/11
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */


trait DdslClient {

  def serviceUp( s : Service)
  def serviceDown( s : Service )
  def getServiceLocations(sr : ServiceRequest) : Array[ServiceLocation]
  def disconnect()

}

class DdslClientImpl(hosts : String) extends DdslClient{

  def this() = this( null )

  private val log = Logger.getLogger( getClass )

  val hostsEnvName = "ZOOKEEPER_HOSTS"

  val dao = new ZDao( verifyHosts(hosts) )


  private def verifyHosts( hosts : String) : String = {

    return if( hosts == null ){
      log.info("Hosts not specified. Reading from system environment variable '"+hostsEnvName+"'")
      val hostsFromEnv = System.getProperty( hostsEnvName)
      if( hostsFromEnv == null ){
        throw new Exception("Cannot continue without hosts-list: not supplied and not found in system env '"+hostsEnvName+"'")
      }
      log.info("Found hostslist: " + hostsFromEnv)
      hostsFromEnv
      
    }else{
      hosts
    }

  }


  override def serviceUp( s : Service) {
    log.info("Marking service up: " + s)
    dao.serviceUp( s )
  }

  override def serviceDown( s : Service ) {
    log.info("Marking service down: " + s)
    dao.serviceDown( s )
  }

  override def getServiceLocations(sr : ServiceRequest) : Array[ServiceLocation] = {
    log.info("Client "+sr.cid+" asking for Service "+sr.sid)
    val sls = dao.getSLs(sr.sid)

    val fixedSls = SlListOptimizer.optimize( sr.cid, sls)

    log.info("ServiceLocations: " + fixedSls)
    return fixedSls
  }

  override def disconnect() {
    log.info("Disconnecting")
    dao.disconnect
  }




}