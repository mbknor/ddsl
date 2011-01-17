package com.kjetland.ddsl

import org.apache.commons.codec.net.URLCodec
import org.apache.log4j.Logger
import java.util.Properties
import java.io.{InputStream, FileInputStream, File}
import org.joda.time.DateTime

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/17/11
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */


/**
 * Since we migt have a situation where our ddsl / zookeeper solution is broken or down
 * we have to have a solution where we can override it.
 *
 * This FallbackClient gets a path from sys env, then tries to load that property file.
 * then tries to look up the requested sid from this file. if success, this url is returned.
 *
 * It's critical that this FallbackClient impl logs a lot about what it is looking for (and possible using).
 *
 * This makes it possible, in an error situation, to look at the client logfile and see what to do, to update
 * the fallback info to make(hack) the system to work again...
 */


object FallbackClient{

  val log = Logger.getLogger( getClass )
  val pathSystemEnvName = "DDSL_FALLBACK_FILE"
  val defaultPath = """c:\ddsl_fallback.properties"""

  def resolveServiceLocations( sr : ServiceRequest) : Array[ServiceLocation] = {



    val sidKey = getServiceIdKey( sr.sid )

    log.error("Trying fallback solution - reading url from properties-file on disk.\n" +
      "path: sysEnv: '"+pathSystemEnvName+"' or default '"+defaultPath+"'.\n" +
      "key: '"+sidKey+"'")

    try{

      val path = getPath
      log.error("Using path: " + path)
      val props = getProps( path )
      val url = getUrl( props, sidKey )

      log.error("Success resolving url '"+url+"' using fallback solution")

      val sl = createFallbackSl( url )

      return List(sl).toArray

    }catch{
      case e: Exception => {
        val msg = "Error getting url using fallback solution"
        log.error(msg, e)
        throw new Exception(msg, e)
      }
    }
  }

  protected def getServiceIdKey( sid : ServiceId) : String = {
    //return new URLCodec().encode( sid.toString )
    return sid.toString.replaceAll(" ", """\_""").replaceAll("""\=""", "")
  }

  def getPath : String = {
    val path = System.getProperty( pathSystemEnvName)
    return if( path != null) path else {
      log.error("system env variable named '"+pathSystemEnvName+"' does not exists. using default path: " + defaultPath)
      defaultPath
    }
  }

  def getProps( path : String ) : Properties = {
    val file = new File(path)
    if( !file.exists ) throw new Exception("File does not exists: " + path)
    if( file.isDirectory) throw new Exception("File is directory: " + path)


    try{

      var in : InputStream = null
      try{
        in = new FileInputStream( file )
        val props = new Properties
        props.load( in )
        return props
      }finally{
        //silently close it
        try{
         if( in != null) in.close
        }
      }


    }catch{
      case e:Exception => throw new Exception("Error loading properties file from " + path, e)
    }
  }

  def getUrl( props : Properties, sidKey : String ) : String = {
    val url = props.getProperty( sidKey)
    return if( url != null ) url else {
      throw new Exception("property with name '"+sidKey+"' not found")
    }
  }

  def createFallbackSl( url : String) : ServiceLocation = {
    ServiceLocation( url, "unknown", DdslDefaults.DEFAULT_QUALITY, new DateTime(), "unknown")
  }
}