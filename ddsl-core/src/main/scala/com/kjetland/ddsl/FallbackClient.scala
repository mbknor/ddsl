package com.kjetland.ddsl

import org.apache.commons.codec.net.URLCodec
import org.apache.log4j.Logger
import java.util.Properties
import java.io.{InputStream, FileInputStream, File}
import org.joda.time.DateTime

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/17/11
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */


/**
 * Since we migt have a situation where our ddsl / zookeeper solution is broken or down
 * we have to have a solution where we can override it.
 *
 * This FallbackClient gets a path from sys env, then tries to load that property file.
 * then tries to look up the requested sid from this file. if success, this url is returned.
 *
 * It's critical that this FallbackClient impl logs a lot about what it is looking for (and possible using).
 *
 * This makes it possible, in an error situation, to look at the client logfile and see what to do, to update
 * the fallback info to make(hack) the system to work again...
 */


object FallbackClient{

  val log = Logger.getLogger( getClass )

  def resolveServiceLocations( ddslConfig : DdslConfig, sr : ServiceRequest) : Array[ServiceLocation] = {
    log.info("Looking up serviceLocation '"+sr.sid+"' from configFile")
    val sl = createFallbackSl( ddslConfig.getStaticUrls( sr.sid) )
    List(sl).toArray
  }



  def createFallbackSl( urls : DdslUrls ) : ServiceLocation = {
    ServiceLocation( urls.url, urls.testUrl, DdslDefaults.DEFAULT_QUALITY, new DateTime(), "unknown")
  }
}