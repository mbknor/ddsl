package com.kjetland.ddsl

import org.scalatest.junit.{JUnitSuite, AssertionsForJUnit}
import org.junit.Test
import org.junit.Assert._
import org.joda.time.DateTime

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/16/11
 * Time: 6:23 PM
 * To change this template use File | Settings | File Templates.
 */

class SlListOptimizerTest extends AssertionsForJUnit with JUnitSuite{


  @Test def verifyOptimizing(){

    val sl1 = ServiceLocation("u1", "tu1", DdslDefaults.DEFAULT_QUALITY, new DateTime(), "ip1")
    val sl2 = ServiceLocation("u1", "tu1", DdslDefaults.DEFAULT_QUALITY-1.0, new DateTime(), "ip2")


    val list = List(sl1, sl2 )
    
    assertEquals( List(sl1, sl2 ), SlListOptimizer.optimize( "x", List(sl1, sl2 ).toArray) )
    assertEquals( List(sl1, sl2 ), SlListOptimizer.optimize( "x", List(sl2, sl1 ).toArray) )
  }

}