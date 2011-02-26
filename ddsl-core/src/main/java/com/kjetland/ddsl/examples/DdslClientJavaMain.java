package com.kjetland.ddsl.examples;

import com.kjetland.ddsl.*;
import com.kjetland.ddsl.config.DdslConfig;
import com.kjetland.ddsl.config.DdslConfigManualImpl;
import com.kjetland.ddsl.model.*;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/18/11
 * Time: 7:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class DdslClientJavaMain {

    private static Logger log = Logger.getLogger(DdslClientJavaMain.class);

    public static void main(String[] args){
        try{
            new DdslClientJavaMain().doStuff();

        }catch(Exception e){
            log.error("Got error in java", e);
        }
    }


    public void doStuff(){

        ServiceId sid = new ServiceId("test", "http", "testJavaService", "1.0");
        log.info("sid: " + sid);

        ServiceLocation sl = new ServiceLocation("url", 0.0, new DateTime(), null);
        log.info("sl: " + sl);

        ClientId cid = new ClientId("test", "testClient", "0.1", null);
        log.info("cid: " + cid);


        DdslConfig ddslConfig = new DdslConfigManualImpl( "localhost:2181" );
        DdslClient ddslClient = new DdslClientImpl( ddslConfig );


        Service s = new Service(sid, sl);

        ddslClient.serviceUp( s );

        ServiceRequest sr = new ServiceRequest(sid, cid);

        ServiceLocation[] sls = ddslClient.getServiceLocations( sr );

        for( ServiceLocation item : sls ){
            log.info(" sl: " + item);
            log.info(" sl: " + item);
        }

        ServiceLocation q = ddslClient.getBestServiceLocation( sr );
        log.info("best sl: " + q);

        log.info("!");

        
    }
}
