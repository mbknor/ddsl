package com.kjetland.ddsl.optimizing

import org.scalatest.junit.{JUnitSuite, AssertionsForJUnit}
import org.junit.Test
import org.junit.Assert._
import org.joda.time.DateTime
import collection.mutable.ListBuffer
import com.kjetland.ddsl.model._

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/16/11
 * Time: 6:23 PM
 * To change this template use File | Settings | File Templates.
 */

class SlListOptimizerTest extends AssertionsForJUnit with JUnitSuite{


  @Test def verifyOptimizing(){

    val sl1 = ServiceLocation("u1", DdslDefaults.DEFAULT_QUALITY, new DateTime(), "ip1")
    val sl2 = ServiceLocation("u2", DdslDefaults.DEFAULT_QUALITY-1.0, new DateTime(), "ip2")
    val sl3 = ServiceLocation("u3", DdslDefaults.DEFAULT_QUALITY, new DateTime(), "x")


    val list = List(sl1, sl2 )
    
    assertEquals( List(sl1, sl2 ), SlListOptimizer.optimize( "x", List(sl1, sl2 ).toArray).toList )
    assertEquals( List(sl1, sl2 ), SlListOptimizer.optimize( "x", List(sl2, sl1 ).toArray).toList )

    //test priority when same ip
    assertEquals( List(sl3, sl1, sl2 ), SlListOptimizer.optimize( "x", List(sl1, sl2, sl3 ).toArray).toList )
    
  }

  @Test def verifyRandomizeList{

    //try x times to get a list that is not equal to org list
    //must do this since the randomlist might bee equal to orglist
    def randomizeAndCheckUniqness( maxTryCount : Int, orgList : ListBuffer[Int]) : ListBuffer[Int] = {
      if( orgList.size <= 1) return orgList
      if( maxTryCount <= 0) throw new Exception("Unable to get different randomlist")
      val randList = SlListOptimizer.randomizeList( orgList )
      return if( randList == orgList ){
        //retry
        randomizeAndCheckUniqness( maxTryCount - 1, orgList)
      }else randList
    }

    def testRandom( orgList : List[Int]){
      val lb = new ListBuffer[Int]
      lb appendAll orgList
      val randList = randomizeAndCheckUniqness( 10, lb )

      assertEquals( lb.size, randList.size)

      assertEquals( lb.sorted, randList.sorted)
    }

    testRandom( List(1,2,3,4,5,6,7,8,9,10) )

    testRandom( List(1,2,3) )

    testRandom( List(1,2) )
    testRandom( List(1) )
    testRandom( List() )

  }

}