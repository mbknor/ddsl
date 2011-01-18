package com.kjetland.ddsl.javaclient;

import com.kjetland.ddsl.*;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/18/11
 * Time: 7:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class DdslClientJavaWrapperMain {

    private static Logger log = Logger.getLogger(DdslClientJavaWrapperMain.class);

    public static void main(String[] args){
        try{
            new DdslClientJavaWrapperMain().doStuff();

        }catch(Exception e){
            log.error("Got error in java", e);
        }
    }


    public void doStuff(){

        ServiceIdJava sid = new ServiceIdJava("test", "http", "testJavaService", "1.0");
        log.info("sid: " + sid);

        ServiceLocationJava sl = new ServiceLocationJava("url", "testUrl", 0.0, new DateTime(), null);
        log.info("sl: " + sl);

        ClientIdJava cid = new ClientIdJava("test", "testClient", "0.1", null);
        log.info("cid: " + cid);


        DdslConfig ddslConfig = new DdslConfigManualImpl( "localhost:2181" );
        DdslClient ddslClient = new DdslClientImpl( ddslConfig );

        DdslClientJavaWrapper c = new DdslClientJavaWrapper( ddslClient );
        c.serviceUp(sid, sl);

        ServiceLocationJava[] sls = c.getServiceLocations(sid, cid);

        for( ServiceLocationJava item : sls ){
            log.info(" sl: " + item);
        }

        ServiceLocationJava q = c.getBestServiceLocation(sid, cid);
        log.info("best sl: " + q);

        log.info("!");

        
    }
}
