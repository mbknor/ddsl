package com.kjetland.ddslcmdlinetool




case class DdslError(val msg: String ) extends Exception

case class CmdBase()

case class CmdParsed(val cmd:String, val data:String) extends CmdBase

object App {

  def main(args: Array[String]) {
    println("ddsl-cmdline-tool started. waiting for commands. Use help if needed.");
    while ( true ) {
      val cmdString = readLine()
      val cmd = parseCmd(cmdString)
      cmd match {
        case Some(x: CmdParsed) => {
          sendResponse(true, "echo: " + x.cmd + " " + x.data)
        }
        case _ => {
          sendResponse(false, "error parsing command")
        }
      }
    }
  }

  def sendResponse(success:Boolean, msg : String) {
    val status = if (success) "ok " else "error "
    println( status + msg )
  }

  def parseCmd(cmdString : String) : Option[CmdBase] = {
    val i = cmdString.indexOf(' ')
    if ( i <= 0) {
      return None
    }
    return Some(CmdParsed(cmdString.substring(0,i), cmdString.substring(i+1)))
  }

}
