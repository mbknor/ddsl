package com.kjetland.ddsl

import org.scalatest.junit.{AssertionsForJUnit, JUnitSuite}
import org.junit.Test
import org.junit.Assert._
import org.joda.time.DateTime

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/16/11
 * Time: 1:06 PM
 * To change this template use File | Settings | File Templates.
 */

class DdslDataConverterTest  extends AssertionsForJUnit with JUnitSuite{


  @Test def verifyConverting(){
    val sid = ServiceId("test", "http", "testService", "1.0")
    val sl = ServiceLocation(sid, "http://localhost/url", "http://localhost/test", 10.0, new DateTime(2011, 1, 16, 13, 10, 1,0))

    val s = DdslDataConverter.getServiceLocationAsString( sl)
    val sl2 = DdslDataConverter.getServiceLocationFromString( s )

    assertEquals( sl, sl2)

  }
}