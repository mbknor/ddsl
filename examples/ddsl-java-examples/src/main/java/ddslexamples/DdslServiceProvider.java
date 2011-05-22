package ddslexamples;

import java.util.Random;
import java.net.*;
import com.kjetland.ddsl.*;
import org.joda.time.DateTime;
import java.io.*;
import com.kjetland.ddsl.model.*;


public class DdslServiceProvider {
    
    
    public static void main(String[] args) {
        
        try {
        
            //adding java property to force usage of our configfile
            System.setProperty("DDSL_CONFIG_PATH", "ddsl_config.properties");

            //Create DdslClient that connects to zookeeper-network
            DdslClient client = new DdslClientImpl();

            //This example is for sure running in out test environment
            String environment = "test";

            //define serviceId that describes which service we need
            ServiceId serviceId = new ServiceId( environment, "telnet", "telnetServer", "0.1");

            //pick a port to listen on
            int port = new Random().nextInt( 100 ) + 40000;
            System.out.println("Going to listen on port " + port);

            //prepare our location
            ServiceLocation ourLocation = new ServiceLocation( "telnet://localhost:"+port, 0.0, new DateTime(), null);

            ServerSocket listenSocket = new ServerSocket( port );

            //must tell ddsl that we're online
            client.serviceUp( new Service( serviceId, ourLocation));;

            while( true ){
              System.out.println("Waiting for connection");
              Socket s = listenSocket.accept();
              try{
                System.out.println("Got connection");
                BufferedWriter out = new BufferedWriter( new OutputStreamWriter(s.getOutputStream() ) );
                BufferedReader in = new BufferedReader( new InputStreamReader( s.getInputStream()));

                while( true ){
                  //wait for command from client
                  String command = in.readLine();
                  if (command == null) {
                      s.close();
                      break;
                  }
                  System.out.println("Received: " + command);

                  //send greeting
                  String msg = "Hello from server at port " + port + " ("+System.currentTimeMillis()+")";
                  System.out.println("Sending: " + msg);
                  out.write(msg + "\n");
                  out.flush();
                }
              }catch(Exception e){
                System.out.println("Lost connection");
              }



            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }
    
}