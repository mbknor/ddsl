package com.kjetland.ddsl

/**
 * Created by IntelliJ IDEA.
 * User: mortenkjetland
 * Date: 1/16/11
 * Time: 6:21 PM
 * To change this template use File | Settings | File Templates.
 */

object SlListOptimizer{

  /**
   * Sorts the list of available service locations.
   *
   * The sorting is done by quality, higher the better, but if multiple sls have same quality,
   * sls with identical 'ip' as client is prefered.
   *
   * Inside each identical group of sls, we sort them random. this random is done
   * to introduce some random loadbalancing.
   *
   *
   *
   */
  def optimize( clientId : ClientId, sls : Array[ServiceLocation]) : Array[ServiceLocation] = {

    return sls
  }
}