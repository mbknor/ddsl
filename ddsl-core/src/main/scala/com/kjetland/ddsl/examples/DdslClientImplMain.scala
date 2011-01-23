package com.kjetland.ddsl.examples

import org.joda.time.DateTime
import org.apache.log4j.Logger
import com.kjetland.ddsl._
import com.kjetland.ddsl.model._
import com.kjetland.ddsl.config._

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/18/11
 * Time: 8:05 AM
 * To change this template use File | Settings | File Templates.
 */

object DdslClientImplMain extends ExampleLogging{

  def main(args : Array[String]){
    val log = Logger.getLogger(getClass)
    try{
      doStuff
    }catch{
      case e:Exception => log.error("error", e)
    }
  }

  def doStuff(){
    val client = new DdslClientImpl( new DdslConfigManualImpl("localhost:2181"))

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



    //Thread.sleep(100000)

  }


}