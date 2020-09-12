package com.abiratsis.airport.pipeline.common

object Util {
  import java.io.File
  import scala.reflect.io.Directory

  def deleteDir(dir: String): Boolean = {
    val directory = new Directory(new File(dir))

    if(directory.exists)
      directory.deleteRecursively()
    else
      false
  }

  def deleteFile(path: String) = new File(path).delete()

//  def isNullOrEmpty[T](s: Iterable[T]) = s match {
//    case null => true
//    case _ => s.toSeq.isEmpty
//  }
}

object String {
  def isNullOrEmpty(str: java.lang.String) : Boolean = {
    str == null || str.isEmpty
  }
}
