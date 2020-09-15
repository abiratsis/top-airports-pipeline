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
}
