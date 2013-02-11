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
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.datatype.joda.JodaModule
import java.text.SimpleDateFormat

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
object App {

  val client : DdslClient = new DdslClientImpl
  val logger = LoggerFactory.getLogger(classOf[App])

  val json = new ObjectMapper()
  json.registerModule(DefaultScalaModule)
  json.registerModule(new JodaModule())
  json.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"))

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
            case "exit" => ExitResult()
            case "help" => processHelp()
            case "getBestServiceLocation" => getBestServiceLocation(cmd.data)
            case "getServiceLocations" => getServiceLocations(cmd.data)
            case "getAllAvailableServices" => getAllAvailableServices(cmd.data)
            case "serviceUp" => serviceUp(cmd.data, false)
            case "serviceUpPersistent" => serviceUp(cmd.data, true)
            case "serviceDown" => serviceDown(cmd.data)
            case _ => ErrorResult("Unknown cmd: " + cmd.cmd)
          }
        } catch {
          case e : Exception => {
            logger.error("Got error", e)
            ErrorResult(e.toString)
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

  def processHelp() : Result = {
    val sb = new StringBuilder
    val sr = ServiceRequest(ServiceId("test","telnet","telnetServer","0.1"), ClientId("Client env", "client name", "version", "ip-address" ))
    sb.append("getBestServiceLocation " + json.writeValueAsString(sr) + "\n")
    sb.append("getServiceLocations " + json.writeValueAsString(sr) + "\n")
    sb.append("getAllAvailableServices\n")
    val s = Service(ServiceId("test", "http", "cmd-tool", "0.1"), ServiceLocation("http://localhost:4321/hi", 1.0, new DateTime, "127.0.0.1"))
    sb.append("serviceUp " + json.writeValueAsString(s) + "\n")
    sb.append("serviceUpPersistent " + json.writeValueAsString(s) + "\n")
    sb.append("serviceDown " + json.writeValueAsString(s) + "\n")
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
