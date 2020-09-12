package com.abiratsis.airport.pipeline

import sys.process._
import java.net.URL
import java.io.File

import exceptions.NullOrEmptyArgumentException

/**
 * Based on: https://alvinalexander.com/scala/scala-how-to-download-url-contents-to-string-file/
 *
 * @param url
 * @param filename
 */
class FileDownloader(url: String, filename: String) {
  def download() : Unit = {
    new URL(url) #> new File(filename) !!
  }
}

object FileDownloader{
  import com.abiratsis.airport.pipeline.common.String._

  def apply(url: String, filename: String): FileDownloader = {
    if(isNullOrEmpty(url))
      throw new NullOrEmptyArgumentException("url")

    if(isNullOrEmpty(filename))
      throw new NullOrEmptyArgumentException("filename")

    new FileDownloader(url, filename)
  }
}
