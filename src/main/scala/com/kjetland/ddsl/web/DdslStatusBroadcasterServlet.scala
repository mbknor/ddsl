package com.kjetland.ddsl.web

import javax.servlet.http.HttpServlet
import javax.servlet.ServletConfig
import org.apache.log4j.Logger
import com.kjetland.ddsl._
import org.joda.time.DateTime

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

trait DdslServiceInfoProvider {
  def getService : Service
}

/**
 * Resolves Urls from sys env config and gets ServiceId supplied
 */
abstract class DdslServiceInfoProviderImpl extends DdslServiceInfoProvider {

  val serviceLocation = resolveServiceLocation

  /**
   * resolve serviceLocation via sys env
   */
  private def resolveServiceLocation : ServiceLocation = {
    val config = new DdslConfigSysEnvReloading
    val urls = config.getStaticUrls( getServiceId )

    ServiceLocation( urls.url, urls.testUrl, DdslDefaults.DEFAULT_QUALITY, new DateTime(), null)

  }

  /**
   * This must be overrided to return correect ServiceId
   */
  def getServiceId : ServiceId

  override def getService = Service( getServiceId, serviceLocation )


}



class DdslStatusBroadcasterServlet extends HttpServlet {

  val log = Logger.getLogger( getClass )

 // val ddslConfig = new DdslConfigSysEnvReloading

  val ddslClient = new DdslClientImpl

  val ddslServiceProvider_propertyName = "DdslServiceInfoProviderClass"

  var service : Service = null


  override def init(config: ServletConfig) = {
    service = getDdslServiceIdProvider( config ).getService

    log.info("Marking service as UP for " + service)
    ddslClient.serviceUp( service )


  }

  override def destroy = {
    log.info("Marking service as DOWN for " + service)
    ddslClient.serviceDown( service )
    ddslClient.disconnect
  }



  private def getDdslServiceIdProvider( sc : ServletConfig) : DdslServiceInfoProvider = {
    val className = sc.getInitParameter( ddslServiceProvider_propertyName )

    if( className == null || className.isEmpty) throw new Exception("Missing servlet config param: " + ddslServiceProvider_propertyName)

    //instanicate the class
     Class.forName( className).newInstance.asInstanceOf[DdslServiceInfoProvider]

  }


}