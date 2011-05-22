package ddslexamples

import java.util.Random
import java.net.ServerSocket
import com.kjetland.ddsl._
import org.joda.time.DateTime
import java.io.{InputStreamReader, BufferedReader, BufferedWriter, OutputStreamWriter}
import com.kjetland.ddsl.model._

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/21/11
 * Time: 11:02 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * See README for more info
 */
object DdslServiceProvider{

  def main( args : Array[String]){

    //adding java property to force usage of our configfile
    System.setProperty(DdslDefaults.configSystemEnvironmentName, "ddsl_config.properties")

    //Create DdslClient that connects to zookeeper-network
    val client = new DdslClientImpl

    //This example is for sure running in out test environment
    val environment = "test"

    //define serviceId that describes which service we need
    val serviceId = ServiceId( environment, "telnet", "telnetServer", "0.1")

    //pick a port to listen on
    val port = new Random().nextInt( 100 ) + 40000
    println("Going to listen on port " + port)

    //prepare our location
    val ourLocation = ServiceLocation( "telnet://localhost:"+port, 0.0, new DateTime(), null)

    val listenSocket = new ServerSocket( port )

    //must tell ddsl that we're online
    client.serviceUp( Service( serviceId, ourLocation))

    while( true ){
      println("Waiting for connection")
      val s = listenSocket.accept
      try{
        println("Got connection")
        val out = new BufferedWriter( new OutputStreamWriter(s.getOutputStream ) )
        val in = new BufferedReader( new InputStreamReader( s.getInputStream))

        var continue = true
        while( continue ){
          //wait for command from client
          val command = in.readLine
          if (command == null) {
              s.close();
              continue = false
          } else {
              println("Received: " + command)

              //send greeting
              val msg = "Hello from server at port " + port + " ("+System.currentTimeMillis+")"
              println("Sending: " + msg)
              out.write(msg + "\n");
              out.flush
          }
        }
      }catch{
        case _ => println("Lost connection")
      }



    }


  }

}