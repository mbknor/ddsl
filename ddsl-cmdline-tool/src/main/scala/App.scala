package com.kjetland.ddslcmdlinetool

import com.kjetland.ddsl.{DdslClient, DdslClientImpl}
import com.kjetland.ddsl.model._
import org.slf4j.LoggerFactory
import com.kjetland.ddsl.model.ServiceId
import com.kjetland.ddsl.model.ServiceRequest
import com.kjetland.ddsl.model.ClientId
import scala.Some
import com.kjetland.ddsl.model.ServiceLocation
import org.joda.time.DateTime
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import java.text.SimpleDateFormat
import com.kjetland.ddsl.config.DdslConfig
import scala.collection.JavaConverters._

case class DdslError(val msg: String ) extends Exception

case class CmdBase()

case class CmdParsed(val cmd:String, val data:Option[String])

trait Result {
  def msg : String
}

trait SuccessResult extends Result {}

case class OkResult(val msg : String) extends SuccessResult
case class ErrorResult(val msg : String) extends Result
case class ExitResult(val msg : String = "Quiting") extends Result
object App extends DdslConfig {

  lazy val client : DdslClient = new DdslClientImpl(this)
  val logger = LoggerFactory.getLogger(classOf[App])

  val json = new ObjectMapper()
  json.registerModule(DefaultScalaModule)
  json.registerModule(new JodaModule())
  json.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"))

  val defaultZookeeperHosts = List("localhost:2181")
  var zookeeperHosts : Option[List[String]] = None // should be configured/changed by client

  def hosts: String = {
    if ( zookeeperHosts.isEmpty ) {
      zookeeperHosts = Some(defaultZookeeperHosts)
    }
    val hostsString = zookeeperHosts.get.mkString(",")
    logger.info("Using zookeeperHosts: " + hostsString)
    hostsString
  }

  var fallbackUrls = Map[String, String]()

  // This method is called when we where unable to find the service online.
  // Trying this method before giving up..
  def getStaticUrl(sid: ServiceId): String = {
    fallbackUrls.getOrElse(sid.toString, {throw new Exception("Did not find "+sid+" in fallbackUrls-map")})
  }

  def setZookeeperHosts(data: Option[String]): Result = {
    if ( data.isEmpty ) {
      return ErrorResult("Missing arg")
    }

    // Can only do this if it has not been done before.. and if the default has not been used before..
    if ( !zookeeperHosts.isEmpty) {
      return ErrorResult("Too late.. You have to set this before executing any DDSL-calls")
    }

    val list : List[String] = json.readValue(data.get, classOf[List[String]])

    zookeeperHosts = Some( list )
    OkResult("ZookeeperHosts-list has been configured")
  }

  def setFallbackUrlsMap(data:Option[String]): Result = {
    if ( data.isEmpty ) {
      return ErrorResult("Missing arg")
    }

    this.fallbackUrls = json.readValue(data.get, classOf[Map[String, String]])
    OkResult("fallbackUrls-map has been updated: " + json.writeValueAsString(this.fallbackUrls))
  }

  def getBestServiceLocation(data: Option[String]): Result = {
    if ( data.isEmpty ) {
      return ErrorResult("Missing arg")
    }

    val sr = json.readValue( data.get, classOf[ServiceRequest] )

    val sl : ServiceLocation = client.getBestServiceLocation( sr)

    OkResult(json.writeValueAsString(sl))
  }

  def getServiceLocations(data: Option[String]): Result = {
    if ( data.isEmpty ) {
      return ErrorResult("Missing arg")
    }

    val sr = json.readValue(data.get, classOf[ServiceRequest])

    val sls = client.getServiceLocations( sr)

    OkResult(json.writeValueAsString(sls))
  }

  def getAllAvailableServices(data: Option[String]): Result = {
    val r = client.getAllAvailableServices()
    OkResult(json.writeValueAsString(r))
  }

  def serviceUp(data: Option[String], persistent:Boolean): Result = {

    if ( data.isEmpty ) {
      return ErrorResult("Missing arg")
    }

    val s = json.readValue(data.get, classOf[Service])

    val sls = client.serviceUp( s, persistent )

    OkResult(json.writeValueAsString(sls))
  }

  def serviceDown(data: Option[String]): Result = {

    if ( data.isEmpty ) {
      return ErrorResult("Missing arg")
    }

    val s = json.readValue(data.get, classOf[Service])

    val sls = client.serviceDown( s )

    OkResult(json.writeValueAsString(sls))
  }


  def main(args: Array[String]) {
    println("ok ddsl-cmdline-tool started. waiting for commands. Use help if needed.");
    try {
      while ( true ) {
        val cmdString = readLine()
        val cmd = parseCmd(cmdString)

        val r : Result = try {
          cmd.cmd match {
            case "setZookeeperHosts" => setZookeeperHosts(cmd.data)
            case "exit" => ExitResult()
            case "help" => processHelp()
            case "getBestServiceLocation" => getBestServiceLocation(cmd.data)
            case "getServiceLocations" => getServiceLocations(cmd.data)
            case "getAllAvailableServices" => getAllAvailableServices(cmd.data)
            case "serviceUp" => serviceUp(cmd.data, false)
            case "serviceUpPersistent" => serviceUp(cmd.data, true)
            case "serviceDown" => serviceDown(cmd.data)
            case "setFallbackUrlsMap" => setFallbackUrlsMap(cmd.data)
            case _ => ErrorResult("Unknown cmd: " + cmd.cmd)
          }
        } catch {
          case e : Exception => {
            logger.error("Got error", e)
            ErrorResult( exception2String(e))
          }
        }


        sendResponse(r)
        if ( r.isInstanceOf[ExitResult]) {
          // Quiting
          return
        }

      }
    } finally {
      client.disconnect()
    }
  }

  def exception2String(e:Throwable):String = {
    if ( e == null){
      ""
    } else {
      e.toString() + exception2String(e.getCause)
    }
  }

  def processHelp() : Result = {
    val sb = new StringBuilder
    val sr = ServiceRequest(ServiceId("test","telnet","telnetServer","0.1"), ClientId("Client env", "client name", "version", "ip-address" ))
    sb.append("setZookeeperHosts " + json.writeValueAsString( List("localhost:2181","some.other.server.com:2181")) + "\n")
    sb.append("getBestServiceLocation " + json.writeValueAsString(sr) + "\n")
    sb.append("getServiceLocations " + json.writeValueAsString(sr) + "\n")
    sb.append("getAllAvailableServices\n")
    val s = Service(ServiceId("test", "http", "cmd-tool", "0.1"), ServiceLocation("http://localhost:4321/hi", 1.0, new DateTime, "127.0.0.1"))
    sb.append("serviceUp " + json.writeValueAsString(s) + "\n")
    sb.append("serviceUpPersistent " + json.writeValueAsString(s) + "\n")
    sb.append("serviceDown " + json.writeValueAsString(s) + "\n")
    sb.append("setFallbackUrlsMap " + json.writeValueAsString(Map(ServiceId("test","telnet","telnetServer","0.1").toString -> "http://example.com/foo", ServiceId("test","http","BarServer","1.0").toString -> "http://example.com/bar")) + "\n")
    sb.append("help\n")
    sb.append("exit")


    return OkResult("Available commands:\n" + sb)
  }

  def sendResponse( r: Result) {
    val status = if (r.isInstanceOf[SuccessResult]) "ok " else "error "
    println( status + r.msg )
  }

  def parseCmd(cmdString : String) : CmdParsed = {
    val i = cmdString.indexOf(' ')
    if ( i <= 0) {
      return CmdParsed(cmdString, None)
    }
    return CmdParsed(cmdString.substring(0,i), Some(cmdString.substring(i+1)))
  }

}
