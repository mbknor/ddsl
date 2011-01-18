package com.kjetland.ddsl

import org.scalatest.junit.{AssertionsForJUnit, JUnitSuite}
import org.junit.Assert._
import org.junit.{After, Before, Test}

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/17/11
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */

class FallbackClientTest extends AssertionsForJUnit with JUnitSuite{

  val testPath = """src/test/resources/ddsl_fallback.properties"""

  var orgEnvValue : String = null

  @Before def setup {
    //store env value
    orgEnvValue = System.getProperty( FallbackClient.pathSystemEnvName )
  }

  @After def teardown {
    if( orgEnvValue != null ){
      //restore env value
      System.setProperty( FallbackClient.pathSystemEnvName, orgEnvValue)
    }

  }

  @Test def verifyPathResolving{
    System.setProperty( FallbackClient.pathSystemEnvName, "")
    assertEquals( FallbackClient.defaultPath, FallbackClient.getPath)

    System.setProperty(FallbackClient.pathSystemEnvName, testPath)
    assertEquals( testPath, FallbackClient.getPath)
  }


  @Test def verifyGetProps_and_getUrl{
    val props = FallbackClient.getProps( testPath )
    assertNotNull( props )
    assertEquals( "someTestUrl", FallbackClient.getUrl( props, "testUrl"))
  }

  @Test def verifyCreateFallbackSl{
    val url = "myUrl"
    val sl = FallbackClient.createFallbackSl( url )
    assertEquals( url, sl.url)
  }


  @Test def verifyResolveServiceLocations{

    System.setProperty(FallbackClient.pathSystemEnvName, testPath)

    val sr = ServiceRequest( ServiceId("test", "http", "testService", "1.0"), ClientId("test", "testClient", "1.0", "127.0.0.1"))
    val srs = FallbackClient.resolveServiceLocations( sr )
    
    assertEquals( 1, srs.size)
    assertEquals( "serviceUrlFromFile", srs(0).url)


  }


}