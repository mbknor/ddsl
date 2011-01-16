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

  def serviceUp( s : Service)
  def serviceDown( s : Service )
  def getServiceLocations(sr : ServiceRequest) : Array[ServiceLocation]
  def getBestServiceLocation(sr : ServiceRequest) : ServiceLocation
  def disconnect()

}

private object MainDdslClient{


  def main(args : Array[String]){
    val log = Logger.getLogger(getClass)
    try{
      doStuff
    }catch{
      case e:Exception => log.error("error", e)
    }
  }

  def doStuff(){
    val client = new DdslClientImpl

    val sid = ServiceId("test", "http", "testService", "1.0")
    val sl = ServiceLocation("http://localhost/url", "http://localhost/test", 10.0, new DateTime(),null)//ip == null -> will be resolved
    val sl2 = ServiceLocation("http://localhost:90/url", "http://localhost:90/test", 9.0, new DateTime(), "127.0.0.1")

    client.serviceUp( Service(sid, sl))

    Thread.sleep(1000)

    val clientId = ClientId( "test", "testApp", "0.1", null)
    val sls = client.getServiceLocations( ServiceRequest(sid, clientId))
    println("*** sls: " + sls.toList)

    client.serviceUp( Service(sid, sl2))

    println("*** sls: " + client.getServiceLocations( ServiceRequest(sid, clientId)).toList)

    println("*** best: " + client.getBestServiceLocation( ServiceRequest(sid, clientId)))

    Thread.sleep(1000)

    println("**********************")

    val sid2 = ServiceId("test", "http", "testServiceX", "1.0")
    val sl3 = ServiceLocation("http://localhost/url", "http://localhost/test", 10.0, new DateTime(),null)//ip == null -> will be resolved
    val sl4 = ServiceLocation("http://localhost:90/url", "http://localhost:90/test", 10.0, new DateTime(), null)

    client.serviceUp( Service(sid2, sl3))
    client.serviceUp( Service(sid2, sl4))

    println("*** sls: " + client.getServiceLocations( ServiceRequest(sid2, clientId)).toList)
    println("*** best: " + client.getBestServiceLocation( ServiceRequest(sid2, clientId)))
    println("*** sls: " + client.getServiceLocations( ServiceRequest(sid2, clientId)).toList)
    println("*** best: " + client.getBestServiceLocation( ServiceRequest(sid2, clientId)))
    println("*** sls: " + client.getServiceLocations( ServiceRequest(sid2, clientId)).toList)
    println("*** best: " + client.getBestServiceLocation( ServiceRequest(sid2, clientId)))
    println("*** sls: " + client.getServiceLocations( ServiceRequest(sid2, clientId)).toList)
    println("*** best: " + client.getBestServiceLocation( ServiceRequest(sid2, clientId)))
    println("*** sls: " + client.getServiceLocations( ServiceRequest(sid2, clientId)).toList)
    println("*** best: " + client.getBestServiceLocation( ServiceRequest(sid2, clientId)))
    println("*** sls: " + client.getServiceLocations( ServiceRequest(sid2, clientId)).toList)
    println("*** best: " + client.getBestServiceLocation( ServiceRequest(sid2, clientId)))



    Thread.sleep(100000)

  }


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


  override def serviceUp( service : Service) {
    val s = checkAndFillInHostIp( service )
    log.info("Marking service up: " + s)
    dao.serviceUp( s )
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



  override def serviceDown( service : Service ) {
    val s = checkAndFillInHostIp( service )
    log.info("Marking service down: " + s)
    dao.serviceDown( s )
  }

  override def getServiceLocations(sr : ServiceRequest) : Array[ServiceLocation] = {
    log.info("Client "+sr.cid+" asking for Service "+sr.sid)
    val sls = dao.getSLs(sr.sid)



    val clientIp = checkAndResolveLocalIp( sr.cid.ip )

    val fixedSls = SlListOptimizer.optimize( clientIp, sls)

    log.info("ServiceLocations: " + fixedSls)
    return fixedSls
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
    log.info("Disconnecting")
    dao.disconnect
  }




}