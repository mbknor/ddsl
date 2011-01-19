package com.kjetland.ddsl.web

import javax.servlet.{ServletContextEvent, ServletContextListener}
import com.kjetland.ddsl.{ServiceId, DdslConfigSysEnvReloading}

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/19/11
 * Time: 11:02 AM
 *
 * Include this ContextListener in your web.xml to "broadcast" to ddsl that your service
 * is up as long as your webapp is deployed.
 *
 * I have not found any way of resolving the url your app is mounted on
 * when the app is deployed - need a request to do that...
 *
 * therefor this impl loads the url from DdslConfig - the same way
 * FallbackClient does it when finding remote serviceLocations..
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

trait DdslServiceIdProvider {
  def getSerciceId : ServiceId
}


//TODO: Think this must be a servlet instead of ContextListener
//need a name (servletname) to make it possible to load specific class for each instance of
//this servlet....


class DdslServiceBroadcasterContextListener extends ServletContextListener {


  def contextInitialized(sce: ServletContextEvent) = {

    val sc = sce.getServletContext

    serviceUp
  }

  def contextDestroyed(p1: ServletContextEvent) = serviceDown


  /**
   * Service is up - register to ddsl
   */
  def serviceUp {

  }


  /**
   * service is going down - remove from ddsl
   */
  def serviceDown {

  }




}