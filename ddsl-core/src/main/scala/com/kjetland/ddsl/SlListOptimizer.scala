package com.kjetland.ddsl

import collection.mutable.{ListBuffer, HashMap}
import java.util.Random

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
  def optimize( clientIp : String, sls : Array[ServiceLocation]) : Array[ServiceLocation] = {

    //to prioritize local services with equal quality, we add some small amount to quality
    //when sl.ip == clientIp

    //create a map of quality->list, then sort by quality.
    //also randomize content of each list..


    //distribute Sls according to quality
    val q2list = new HashMap[Double, ListBuffer[ServiceLocation]]

    sls.foreach( { sl : ServiceLocation => {
      val quality = if( sl.ip == clientIp){
          sl.quality + 0.00001
        }else{
          sl.quality
        }
      
      q2list.get( quality ) match {
        case Some(list : ListBuffer[ServiceLocation]) => list += sl
        case None => {
          val list = new ListBuffer[ServiceLocation]
          list += sl
          q2list.put( quality, list)
        }
      }
    }
    })

    //randomize content of each list

    q2list.values.foreach( { list: ListBuffer[ServiceLocation] => {


      //swap org list with random
      val randomizedList = randomizeList( list )
      list.clear
      list appendAll randomizedList


    }} )

    //sort key-list by quality

    val sortedQuality = q2list.keySet.toList.sorted.reverse

    //create new list in this order
    val finalList = new ListBuffer[ServiceLocation]
    sortedQuality.foreach { quality : Double => {
      val list = q2list.get(quality).get
      finalList appendAll list
    }}

    finalList.toArray

  }

  def randomizeList[T]( orgList : ListBuffer[T]) : ListBuffer[T] = {
    val list = new ListBuffer[T]
    list appendAll orgList
    val randomizedList = new ListBuffer[T]
    while( !list.isEmpty ){
      val index = (scala.math.random * list.size).toInt
      val sl = list.remove( index)
      randomizedList += sl
    }

    return randomizedList
  }
}