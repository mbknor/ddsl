package com.kjetland.ddsl.exceptions

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/23/11
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Exception thrown if DDSL not was able to find any ServiceLocations for
 * the service beeing asked for
 */
class NoDDSLServiceLocationFoundException(msg : String, cause: Throwable) extends RuntimeException(msg, cause) {

  def this( cause : Throwable) = this(null, cause)
  def this( msg : String) = this(msg, null)
  def this() = this(null, null)

}