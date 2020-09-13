package com.abiratsis.airport.pipeline.common

import org.rogach.scallop._

class CommandLineHandler(args: Seq[String]) extends ScallopConf(args) {
  version("top-airports 0.1.0 (c) 2020 abiratsis")
  banner(
    """Usage: tapipe -m <processing_mode> {b batch|s stream}  -i <input_file> -d <destination>
      |Optional: [-f format(only in batch mode)] [--download-data download_data]
      |
      |tapipe is a program for extracting the top 10 source airports from routes.dat.
      |
      |Options:
      |""".stripMargin)

  val mode = choice(choices = List("b", "s"), required = true, short = 'm', descr = "Batch or streaming mode.")
  val inputFile = opt[String](short = 'i', required = true, descr = "The source file, i.e routes.dat.")
  val destination = opt[String](short = 'd', required = true, descr = "The destination file(batch) or table name(streaming).")

  val format = choice(choices = List("csv", "parquet", "avro", "text"), default = Some("parquet"), short = 'f', descr = "The format of the exported file in batch mode.")
  val downloadData = opt[Boolean](default = Some(false), noshort = true, descr = "Whether we should download or not the source data, i.e: routes.dat")

  verify()
}

