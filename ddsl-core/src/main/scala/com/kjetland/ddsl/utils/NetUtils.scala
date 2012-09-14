package com.kjetland.ddsl.utils

import java.net.{Inet4Address, NetworkInterface}

object NetUtils {

  def resolveLocalPublicIP() : String = {
    // Need to resolve the local IP address.
    // Must find a none-localhost, none-virtual IP address
    val e = NetworkInterface.getNetworkInterfaces()
    while( e.hasMoreElements() ) {
      val ni = e.nextElement()
      if ( !ni.isLoopback() && ni.isUp() && !ni.isVirtual() ) {
        val ip = ni.getInetAddresses()
        while ( ip.hasMoreElements()) {
          val a = ip.nextElement()
          if ( a.isInstanceOf[Inet4Address]) {
            val resolvedIp = a.getHostAddress()
            return resolvedIp
          }
        }
      }
    }
    throw new Exception("Error solving local IP address")
  }

}
