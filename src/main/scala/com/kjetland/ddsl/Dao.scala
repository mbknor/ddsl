package com.kjetland.ddsl

import org.joda.time.DateTime
import collection.mutable.HashMap
import scala.collection.JavaConversions._


/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/13/11
 * Time: 9:06 PM
 * To change this template use File | Settings | File Templates.
 */

case class ServiceId(environment : String, serviceType : String, name : String, version : String)
case class ServiceLocation( id: ServiceId, url : String, testUrl : String, quality : Double, up : Boolean, lastUpdated : DateTime)

trait Dao{

  def update( sl : ServiceLocation)
  def getSLs(id : ServiceId) : ServiceLocation
  def getSLs(id : ServiceId, includeDowns : Boolean) : ServiceLocation

}


