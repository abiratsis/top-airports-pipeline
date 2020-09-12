package com.abiratsis.airport.pipeline.common

import java.io.File
import java.nio.file.Paths

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

import scala.collection.immutable
import scala.reflect.io.Directory

//object Util {
//  /**
//   * Converts case classes to map, taken from https://gist.github.com/lauris/7dc94fb29804449b1836#file-cctomap-scala
//   *
//   * @param cc The case class
//   * @return The map that contains the class members
//   */
//  def ccToMap(cc: AnyRef) =
//    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {
//      (a, f) =>
//        f.setAccessible(true)
//        a + (f.getName -> f.get(cc))
//    }
//
//  /**
//   * Extracts the file name from the given url.
//   *
//   * @param url
//   * @return The file name
//   */
//  def getFileNameFromUrl(url : String) : String  = {
//    Paths.get(url).getFileName.toString
//  }
//
//  def deleteDir(dir: String) = {
//    val directory = new Directory(new File(dir))
//
//    if(directory.exists) {
//      println(s"$dir was found. Deleting it...")
//      val res = directory.deleteRecursively()
//
//      if(res)
//        println(s"$dir deleted....")
//      else
//        println(s"$dir deletion failed...")
//    }
//    else{
//      println(s"$dir not found!")
//    }
//  }
//
//  def deleteFile(path: String) = new File(path).delete()
//
//  def isNullOrEmpty[T](s: Iterable[T]) = s match {
//    case null => true
//    case _ => s.toSeq.isEmpty
//  }
//}

object String {
  def isNullOrEmpty(str: java.lang.String) : Boolean = {
    str == null || str.isEmpty
  }
}
