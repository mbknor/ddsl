package com.kjetland.ddslcmdlinetool

import com.kjetland.ddsl.{DdslClient, DdslClientImpl}
import com.codahale.jerkson.Json
import com.kjetland.ddsl.model._
import org.slf4j.LoggerFactory
import com.kjetland.ddslcmdlinetool.ErrorResult
import com.kjetland.ddsl.model.ServiceId
import com.kjetland.ddslcmdlinetool.OkResult
import com.kjetland.ddslcmdlinetool.Result
import com.kjetland.ddslcmdlinetool.CmdParsed
import com.kjetland.ddsl.model.ServiceRequest
import com.kjetland.ddsl.model.ClientId
import com.kjetland.ddslcmdlinetool.ExitResult
import scala.Some
import com.kjetland.ddsl.model.ServiceLocation
import org.joda.time.DateTime


case class DdslError(val msg: String ) extends Exception

case class CmdBase()

case class CmdParsed(val cmd:String, val data:Option[String])

case class Result(val success : Boolean, val msg : String)

case class OkResult(override val msg : String) extends Result(true, msg)
case class ErrorResult(override val msg : String) extends Result(false, msg)
case class ExitResult() extends OkResult("Quiting")
object App {

  val client : DdslClient = new DdslClientImpl
  val logger = LoggerFactory.getLogger(classOf[App])

  def getBestServiceLocation(data: Option[String]): Result = {
    if ( data.isEmpty ) {
      return ErrorResult("Missing arg")
    }

    val sr = Json.parse[ServiceRequest](data.get)

    val sl : ServiceLocation = client.getBestServiceLocation( sr)

    OkResult(Json.generate(sl))
  }

  def getServiceLocations(data: Option[String]): Result = {
    if ( data.isEmpty ) {
      return ErrorResult("Missing arg")
    }

    val sr = Json.parse[ServiceRequest](data.get)

    val sls = client.getServiceLocations( sr)

    OkResult(Json.generate(sls))
  }

  def getAllAvailableServices(data: Option[String]): Result = {
    val r = client.getAllAvailableServices()
    OkResult(Json.generate(r))
  }

  def serviceUp(data: Option[String], persistent:Boolean): Result = {

    if ( data.isEmpty ) {
      return ErrorResult("Missing arg")
    }

    val s = Json.parse[Service](data.get)

    val sls = client.serviceUp( s, persistent )

    OkResult(Json.generate(sls))
  }

  def serviceDown(data: Option[String]): Result = {

    if ( data.isEmpty ) {
      return ErrorResult("Missing arg")
    }

    val s = Json.parse[Service](data.get)

    val sls = client.serviceDown( s )

    OkResult(Json.generate(sls))
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


        sendResponse(r.success, r.msg)
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
    sb.append("getBestServiceLocation " + Json.generate(sr) + "\n")
    sb.append("getServiceLocations " + Json.generate(sr) + "\n")
    sb.append("getAllAvailableServices\n")
    val s = Service(ServiceId("test", "http", "cmd-tool", "0.1"), ServiceLocation("http://localhost:4321/hi", 1.0, new DateTime, "127.0.0.1"))
    sb.append("serviceUp " + Json.generate(s) + "\n")
    sb.append("serviceUpPersistent " + Json.generate(s) + "\n")
    sb.append("serviceDown " + Json.generate(s) + "\n")
    sb.append("help\n")
    sb.append("exit")


    return OkResult("Available commands:\n" + sb)
  }

  def sendResponse(success:Boolean, msg : String) {
    val status = if (success) "ok " else "error "
    println( status + msg )
  }

  def parseCmd(cmdString : String) : CmdParsed = {
    val i = cmdString.indexOf(' ')
    if ( i <= 0) {
      return CmdParsed(cmdString, None)
    }
    return CmdParsed(cmdString.substring(0,i), Some(cmdString.substring(i+1)))
  }

}
