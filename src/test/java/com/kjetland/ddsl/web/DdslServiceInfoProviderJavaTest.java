package com.kjetland.ddsl.web;

import com.kjetland.ddsl.ServiceId;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/21/11
 * Time: 7:21 AM
 * To change this template use File | Settings | File Templates.
 * //TODO: This java test is not executed in test-phase in sbt
 */
public class DdslServiceInfoProviderJavaTest{


    /**
     * Have seen problems when extending DdslServiceInfoProvider in java.
     * Must verify it
     */
    @Test
    public void verifyExtendingInJava() throws Exception{

        ServiceId sid = new ServiceId("test", "http", "testService", "1.0");

        System.setProperty("DDSL_CONFIG_PATH", "ddsl_config.properties");//to prevent config from complaining
        DdslServiceInfoProviderImpl p = new DdslServiceInfoProviderImpl(sid);
        Assert.assertEquals(sid, p.serviceId());
    }
}
