package com.kjetland.ddsl.utils

import org.junit.Test
import org.scalatest.junit.{JUnitSuite, AssertionsForJUnit}
import org.junit.Assert._

class NetUtilsTest extends AssertionsForJUnit with JUnitSuite {


  @Test def testResolveLocalPublicIP() {
    val ip = NetUtils.resolveLocalPublicIP()
    assertNotNull(ip)
    assertTrue(ip.length >0)
    System.out.println("local ip:" + ip)
  }

}
