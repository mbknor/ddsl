package com.kjetland.ddsl

import config.DdslConfigManualImpl
import model.{ClientId, ServiceRequest, ServiceId}
import org.scalatest.{Matchers, FunSuite}
import org.scalatest.junit.{AssertionsForJUnit, JUnitSuite}

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/23/11
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */

class DdslClientOnlyFallbackImplTest extends FunSuite with Matchers {

  test("verifyResolveServiceLocations"){


    val sid = ServiceId("test", "http", "testService", "1.0")
    val sr = ServiceRequest( sid, ClientId("test", "testClient", "1.0", "127.0.0.1"))

    val url = "myUrl"

    val config = new DdslConfigManualImpl( "hostList", Map[ServiceId, String]( sid -> url))


    val srs = new DdslClientOnlyFallbackImpl(config).getServiceLocations(sr )

    assert( 1 == srs.size)
    assert( url == srs(0).url)


  }


}