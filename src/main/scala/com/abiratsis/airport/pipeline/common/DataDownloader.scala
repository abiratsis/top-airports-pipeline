package com.abiratsis.airport.pipeline.common

class DataDownloader private(url: String, filename: String) {
  import java.io.File
  import java.net.URL
  import scala.sys.process._

  def download() : Unit = {
    new URL(url) #> new File(filename) !!
  }
}

object DataDownloader{
  import com.abiratsis.airport.pipeline.exceptions.NullOrEmptyArgumentException
  import com.abiratsis.airport.pipeline.common.String.isNullOrEmpty

  def apply(url: String, filename: String): DataDownloader = {
    if(isNullOrEmpty(url))
      throw new NullOrEmptyArgumentException("url")

    if(isNullOrEmpty(filename))
      throw new NullOrEmptyArgumentException("filename")

    new DataDownloader(url, filename)
  }
}
