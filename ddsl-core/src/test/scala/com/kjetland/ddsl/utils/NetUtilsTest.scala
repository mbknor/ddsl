package com.kjetland.ddsl.utils

import org.scalatest.{Matchers, FunSuite}

class NetUtilsTest extends FunSuite with Matchers {


  test("testResolveLocalPublicIP") {
    val ip = NetUtils.resolveLocalPublicIP()
    assert(ip != null)
    assert(ip.length >0)
    System.out.println("local ip:" + ip)
  }

}
