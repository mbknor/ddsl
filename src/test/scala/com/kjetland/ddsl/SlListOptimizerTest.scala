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
    val sl2 = ServiceLocation("u2", "tu2", DdslDefaults.DEFAULT_QUALITY-1.0, new DateTime(), "ip2")
    val sl3 = ServiceLocation("u3", "tu3", DdslDefaults.DEFAULT_QUALITY, new DateTime(), "x")


    val list = List(sl1, sl2 )
    
    assertEquals( List(sl1, sl2 ), SlListOptimizer.optimize( "x", List(sl1, sl2 ).toArray).toList )
    assertEquals( List(sl1, sl2 ), SlListOptimizer.optimize( "x", List(sl2, sl1 ).toArray).toList )

    //test priority when same ip
    assertEquals( List(sl3, sl1, sl2 ), SlListOptimizer.optimize( "x", List(sl1, sl2, sl3 ).toArray).toList )
  }

}