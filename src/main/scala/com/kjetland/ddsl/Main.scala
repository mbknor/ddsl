package com.kjetland.ddsl

import org.joda.time.DateTime
import org.apache.log4j.Logger


/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/15/11
 * Time: 8:10 PM
 * To change this template use File | Settings | File Templates.
 */

object Main{

  def main ( args : Array[String]){
    val log = Logger.getLogger( getClass )
    try{
      doStuff
    }catch{
      case x:Exception => log.error("error", x)
    }


  }

  def doStuff {
    println("hw")

    val hosts = "localhost:2181"
    val dao = new ZDao( hosts )

    val sid = ServiceId("test", "http", "testService", "1.0")
    val sl = ServiceLocation(sid, "http://localhost/url", "http://localhost/test", 10.0, new DateTime())
    Thread.sleep( 100 )
    val sl2 = ServiceLocation(sid, "http://localhost:90/url", "http://localhost:90/test", 9.0, new DateTime())

    dao.update( sl )
    dao.update( sl2 )

    dao.getSLs( sid).foreach{println( _ )}

    Thread.sleep(100000)
  }
}