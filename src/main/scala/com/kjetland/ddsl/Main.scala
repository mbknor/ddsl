package com.kjetland.ddsl

import org.joda.time.DateTime

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/15/11
 * Time: 8:10 PM
 * To change this template use File | Settings | File Templates.
 */

object Main{

  def main ( args : Array[String]){
    println("hw")

    val hosts = "localhost:2181"
    val dao = new ZDao( hosts )

    val sid = ServiceId("test", "http", "testService", "1.0")
    val sl = ServiceLocation(sid, "http://localhost/url", "http://localhost/test", 10.0, new DateTime())

    dao.update( sl )

    Thread.sleep(100000)


  }
}