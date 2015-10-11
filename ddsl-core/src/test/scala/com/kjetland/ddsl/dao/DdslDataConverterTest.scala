package com.kjetland.ddsl.dao

import org.scalatest.FunSuite
import org.scalatest.junit.{AssertionsForJUnit, JUnitSuite}
import org.joda.time.DateTime
import com.kjetland.ddsl.model._

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/16/11
 * Time: 1:06 PM
 * To change this template use File | Settings | File Templates.
 */

class DdslDataConverterTest  extends FunSuite{


  test("verifyConverting"){
    val sl = ServiceLocation("http://localhost/url", 10.0, new DateTime(2011, 1, 16, 13, 10, 1,0), "127.0.0.1")

    val s = DdslDataConverter.getServiceLocationAsString( sl)
    val sl2 = DdslDataConverter.getServiceLocationFromString( s )

    assert( sl == sl2)

  }
}