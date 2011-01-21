package ddslexamples

import com.kjetland.ddsl._
import java.net.Socket
import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter, BufferedWriter}

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/21/11
 * Time: 11:00 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * See README for more info
 */

object DdslServiceConsumer{

  def main( args : Array[String]){
    println("Assuming sys env variable " + DdslDefaults.configSystemEnvironmentName + " points to ddsl-config-file")

    //Create DdslClient that connects to zookeeper-network
    val client = new DdslClientImpl

    //This example is for sure running in out test environment
    val environment = "test"

    //defining our ClientId - so the system knows who we are
    val clientId = ClientId( environment, "telnet", "telnetClient", "0.1" )

    //define serviceId that describes which service we need
    val serviceId = ServiceId( environment, "telnet", "telnetServer", "0.1")

    while( true ){
      try{

        //getting the best serviceLocation
        val location = client.getBestServiceLocation( ServiceRequest(serviceId, clientId ))

        println("Best location for service: " + location.url)

        val hostPort = parseUrl(location.url)

        println("Connecting to server")


        val socket = new Socket( hostPort._1, hostPort._2)
        val out = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream ) )
        val in = new BufferedReader( new InputStreamReader( socket.getInputStream))

        val msg = "hi server! ("+System.currentTimeMillis+")"
        println("Sending: " + msg)
        out.write(msg + "\n")
        out.flush
        val response = in.readLine
        println("Received: " + response)
        //closing connection
        socket.close

      }catch{
        case _ => println("Error communicating")
      }
      Thread.sleep(1500)

    }



  }

  def parseUrl( url : String) : Pair[String, Int] = {
    //extract host and port from url-string on format: telnet://host:port
    val rx = """.*//(.+):(.+)""".r
    url match {
      case rx(host, port) => Pair(host, port.toInt)
      case _ => throw new Exception("Invalid url")
    }
  }


}