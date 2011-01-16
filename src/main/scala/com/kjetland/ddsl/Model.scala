package com.kjetland.ddsl

import org.joda.time.DateTime

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/16/11
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */

case class ServiceId(environment : String, serviceType : String, name : String, version : String)

case class ServiceLocation( url : String, testUrl : String, quality : Double, lastUpdated : DateTime, ip : String)

case class Service( id : ServiceId, sl:ServiceLocation)

case class ClientId(environment : String, name : String, version : String, ip : String)

case class ServiceRequest( sid : ServiceId, cid : ClientId )

object DdslDefaults{
  val DEFAULT_QUALITY = 0.0
}

