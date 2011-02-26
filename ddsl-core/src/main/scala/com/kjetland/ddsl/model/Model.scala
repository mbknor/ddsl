package com.kjetland.ddsl.model

import org.joda.time.DateTime

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/16/11
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */


/**
 * ServiceId is used as a unique ID to a specific service (plus environment and version).
 *
 * ServiceIs used by services publishing their location and by clients asking for service locations.
 *
 * '''environment''' is a string specifying the environment the service lives in. It could be prod, test, preprod etc.
 *
 * '''serviceType''' is a string describing which type of service it is.. It is not used in any other form then to identify the service.
 *  This could be usefull if you have both a SOAP and a REST version of your service at the same time.
 *
 * '''name''' is the name of your service
 *
 * '''version''' is the version of your service
 *
 */
case class ServiceId(environment : String, serviceType : String, name : String, version : String){

  def getMapKey : String = {
    //return new URLCodec().encode( sid.toString )
    return this.toString.replaceAll(" ", """\_""").replaceAll("""\=""", "")
  }
}

/**
 * ServiceLocation represents one single instance/deployment of a particular server(specified with ServiceId).
 *
 * DDSL does not care how you specify your urls... It can be a propper formed url like http://myserver:90/basePath or it can be just an hostname, etc...
 * All you have to care about is that your client knows how to use the url-information.
 *
 * '''url''' is a string that tells the client where to find this particular instance of the service
 *
 * '''quality''' is a double-value specifying the quality of this particular service. The higher number the better. For instance,
 * you could inject a proxy-service just by telling DDSL that you have higher quality than the other instances of an particular service.. This will
 * make all clients use your location instead.
 *
 * '''ip''' is the IP of the machine this instance of the service is running on. This is used to make it possible for clients to prefaere local service locations.
 *
 * if ''ip'' is not specified, it will be resolved.
 *
 */
case class ServiceLocation( url : String, quality : Double, lastUpdated : DateTime, ip : String)


/**
 * Specifies a specific serviceLocation for a specific serviceId
 */
case class Service( id : ServiceId, sl:ServiceLocation)

/**
 * Utility case class holding info about one specific serviceId and all its present available serviceLocations
 *
 */
case class ServiceWithLocations( id : ServiceId, locations : Array[ServiceLocation])

/**
 * When a client asks for a serviceLocation, it must supply a clientId - This is used to make it possible to log/track which clients is using which services..
 *
 * '''environment''' is a string specifying the environment the client lives in. It could be prod, test, preprod etc. It does not have to be
 * the same environment as the service it is asking for..
 *
 * '''name''' is the name of your client
 *
 * '''version''' is the version of your client
 *
 * '''ip''' is the IP of the machine this instance of the client is running on. This is used to make it possible for clients to prefaere local service locations.
 *
 * if ''ip'' is not specified, it will be resolved.
 *
 *
 */
case class ClientId(environment : String, name : String, version : String, ip : String)

/**
 * Used when a specific client is requesting a specific service
 */
case class ServiceRequest( sid : ServiceId, cid : ClientId )

object DdslDefaults{
  val DEFAULT_QUALITY = 0.0

  val configSystemEnvironmentName = "DDSL_CONFIG_PATH"
}


