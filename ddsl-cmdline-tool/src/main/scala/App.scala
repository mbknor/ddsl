package com.kjetland.ddslcmdlinetool




case class DdslError(val msg: String ) extends Exception

case class CmdBase()

case class CmdParsed(val cmd:String, val data:Option[String])

case class Result(val success : Boolean, val msg : String)

case class OkResult(override val msg : String) extends Result(true, msg)
case class ErrorResult(override val msg : String) extends Result(false, msg)
case class ExitResult() extends OkResult("Quiting")
object App {

  def main(args: Array[String]) {
    println("ddsl-cmdline-tool started. waiting for commands. Use help if needed.");
    while ( true ) {
      val cmdString = readLine()
      val cmd = parseCmd(cmdString)
      val r : Result = cmd.cmd match {
        case "exit" => ExitResult()
        case "help" => processHelp()
        case _ => ErrorResult("Unknown cmd: " + cmd.cmd)
      }

      sendResponse(r.success, r.msg)
      if ( r.isInstanceOf[ExitResult]) {
        // Quiting
        return
      }

    }
  }

  def processHelp() : Result = {
    return OkResult("See documentation..")
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
