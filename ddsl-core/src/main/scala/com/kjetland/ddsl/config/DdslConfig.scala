package com.kjetland.ddsl.config

import collection.immutable.HashMap
import java.util.Properties
import java.io.{FileInputStream, File}
import com.kjetland.ddsl.model._
import org.slf4j.LoggerFactory

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/18/11
 * Time: 10:25 PM
 * To change this template use File | Settings | File Templates.
 */

trait DdslConfig {
  def hosts : String
  def getStaticUrl( sid : ServiceId) : String
}

/**
 * Use this config class when you want to specify all config manually
 */
class DdslConfigManualImpl(val hosts : String, urlsMap : Map[ServiceId, String]) extends DdslConfig {

  def this( hosts : String ) = this( hosts, Map[ServiceId, String]() )

  if( hosts == null ) throw new Exception("hosts cannot be null")

  override def getStaticUrl( sid : ServiceId) : String = {
    urlsMap.getOrElse(sid, {
      throw new Exception("Cannot find urls for " + sid)
    })
  }

}

class DdslConfigFromFile( file : File) extends DdslConfig {

  val log = LoggerFactory.getLogger( getClass)

  log.info("Loading ddslConfig from " + file)

  private val props = loadProps( file)

  override def hosts = getValue("zookeeper_hosts")

  private def getValue( key : String) = {
    props.getProperty( key ) match {
      case null => throw new Exception("Cannot find '"+key+"' in " + file)
      case x:String => x
    }
  }

  override def getStaticUrl( sid : ServiceId) : String = {
    val key = sid.getMapKey
    val url = getValue( key + "_url" )
    return url
  }


  private def loadProps( file : File) : Properties = {
    if( !file.exists || file.isDirectory) throw new Exception("Not a file: " + file)
    var in : FileInputStream = null
    try{
      in = new FileInputStream( file )
      val props = new Properties
      props.load( in )

      return props

    }finally{
      //close it silently
      try{
        if( in != null) in.close
      }catch{
        case _ : Exception => None//do nothing
      }
    }
  }
}


/**
 * finds path to config from sys env or java prop, and uses DdslConfigFromFile to load config.
 * Reloads file each time..
 */
class DdslConfigSysEnvReloading extends DdslConfig {

  val log = LoggerFactory.getLogger(getClass)

  val envName = DdslDefaults.configSystemEnvironmentName

  val pathToConfig = resolvePath

  override def hosts : String = loadConfig.hosts

  override def getStaticUrl( sid : ServiceId) : String = loadConfig.getStaticUrl( sid )

  private def loadConfig = new DdslConfigFromFile( pathToConfig )

  private def resolvePath : File = {
    log.info("Looking for java/sysEnv param named " + envName)
    val path = System.getProperty(envName) match {
      case null => {
        //must look in system env.
        System.getenv( envName) match {
          case null => throw new Exception("Parameter " + envName + " not found as Java properties or system environment")
          case s : String => s
        }
      }
      case s : String => s
    }
    return new File( path )
  }
  
}
