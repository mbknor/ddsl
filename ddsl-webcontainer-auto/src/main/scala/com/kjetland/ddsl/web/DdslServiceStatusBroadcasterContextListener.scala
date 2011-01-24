package com.kjetland.ddsl.web

import javax.servlet.http.HttpServlet
import org.apache.log4j.Logger
import com.kjetland.ddsl._
import org.joda.time.DateTime
import com.kjetland.ddsl.model._
import com.kjetland.ddsl.config._
import com.kjetland.wcie.{WCInfoExtractor, WCInfo}
import java.net.InetAddress
import javax.servlet.{ServletContext, ServletContextEvent, ServletContextListener, ServletConfig}

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/19/11
 * Time: 11:02 AM
 *
 */


trait DdslServiceBroadcaster {

  val log = Logger.getLogger( getClass )

  val ddslClient = new DdslClientImpl

  /**
   * Resolved later when possible
   */
  var service : Service = null

  def serviceUp( ) {
    log.info("Marking service as UP for " + service)
    ddslClient.serviceUp( service )

  }

  def serviceDown() {
    log.info("Marking service as DOWN for " + service)
    ddslClient.serviceDown( service )
    ddslClient.disconnect

  }

  /**
   * extracts which port and contextPath your app is mounted on.
   */
  def getWcInfo( servletContext : ServletContext) : WCInfo = {
    WCInfoExtractor.extractWCInfo( servletContext )
  }

  /**
   * Generates base url to you app by resolving, IP, port and context
   */
  def generateBaseUrl( servletContext : ServletContext) : String = {
    val resolvedIp = InetAddress.getLocalHost().getHostAddress
    val wcInfo = getWcInfo( servletContext )

    "http://" + resolvedIp + ":" + wcInfo.getPort + wcInfo.getContextPath
  }

  /**
   * Must override this method and resolve/build service-object
   */
  def resolveService( servletContext : ServletContext ) : Service

}

/**
 * Include this ContextListener in your web.xml to "broadcast" to ddsl that your service
 * is up as long as your webapp is deployed.
 *
 *
 * This impl needs to get config info about what ServiceId it is broadcasting status
 * about.
 *
 *
 * To make it the most flexible with how this info is supplied,
 * hardcoded? loaded from props, dynamic loaded from props based on run environment, etc
 * I've decided that this impl get's configed with a class-name.
 *
 * When initing an instance of this class (with default constructor) is created..
 * This class must impl specific interface/trait DdslServiceIdProvider..
 *
 * ServiceId is retrieved from this class..
 *
 *
 */
abstract class DdslServiceStatusBroadcasterContextListener extends ServletContextListener with DdslServiceBroadcaster{


  def contextInitialized(sce: ServletContextEvent) = {
    service = resolveService( sce.getServletContext)
    serviceUp( )
  }

  def contextDestroyed(sce: ServletContextEvent) = {
    serviceDown( )
  }

}

abstract class DdslStatusBroadcasterServlet extends HttpServlet with DdslServiceBroadcaster{

  override def init(config: ServletConfig) = {
    service = resolveService( config.getServletContext )
    serviceUp( )
  }

  override def destroy = {
    serviceDown( )
  }

}