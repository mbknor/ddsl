package ddslexamples;

import com.kjetland.ddsl.*;
import java.net.*;
import java.io.*;
import com.kjetland.ddsl.model.*;

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

public class DdslServiceConsumer{

  public static void main( String[] args) {
      
      try {

            //adding java property to force usage of our configfile
            System.setProperty("DDSL_CONFIG_PATH", "ddsl_config.properties");

            //Create DdslClient that connects to zookeeper-network
            DdslClient client = new DdslClientImpl();

            //This example is for sure running in out test environment
            String environment = "test";

            //defining our ClientId - so the system knows who we are
            ClientId clientId = new ClientId( environment, "telnet", "telnetClient", "0.1" );

            //define serviceId that describes which service we need
            ServiceId serviceId = new ServiceId( environment, "telnet", "telnetServer", "0.1");

            while( true ){
              try {

                //getting the best serviceLocation
                ServiceLocation location = client.getBestServiceLocation( new ServiceRequest(serviceId, clientId ));

                System.out.println("Best location for service: " + location.url());

                HostAndPort hostPort = parseUrl(location.url());

                System.out.println("Connecting to server");


                Socket socket = new Socket( hostPort.host, hostPort.port);
                BufferedWriter out = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream() ) );
                BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream()));

                String msg = "hi server! ("+System.currentTimeMillis()+")";
                System.out.println("Sending: " + msg);
                out.write(msg + "\n");
                out.flush();
                String response = in.readLine();
                System.out.println("Received: " + response);
                //closing connection
                socket.close();

              } catch (Exception e){
                System.out.println("Error communicating");
              }
              Thread.sleep(1500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

  }
  
  public static class HostAndPort {
      public String host;
      public int port;
      
      public HostAndPort(String host, int port) {
          this.host = host;
          this.port = port;
      }
  }

  private static HostAndPort parseUrl( String url) {
    //extract host and port from url-string on format: telnet://host:port
    int i = url.indexOf("://");
    String[] parts = url.substring(i+3).split(":");
    
    return new HostAndPort(parts[0], Integer.parseInt(parts[1]));
  }


}