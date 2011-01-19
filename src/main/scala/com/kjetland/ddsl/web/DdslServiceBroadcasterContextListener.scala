package com.kjetland.ddsl.web

import javax.servlet.{ServletContextEvent, ServletContextListener}

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/19/11
 * Time: 11:02 AM
 *
 * Include this ContextListener in your web.xml to "broadcast" to ddsl that your service
 * is up as long as your webapp is deployed.
 *
 * This impl resolves the path (basepath) to your app: http://hostname:port/contextPath
 *
 * This basePath is sent as your "url" to ddsl
 *
 *
 */

class DdslServiceBroadcasterContextListener extends ServletContextListener {




  def contextInitialized(sce: ServletContextEvent) = {

    val sc = sce.getServletContext



    serviceUp
  }

  def contextDestroyed(p1: ServletContextEvent) = serviceDown


  /**
   * Service is up - register to ddsl
   */
  def serciceUp {

  }


  /**
   * service is going down - remove from ddsl
   */
  def serviceDown {

  }




}