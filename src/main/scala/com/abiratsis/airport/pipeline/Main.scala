package com.abiratsis.airport.pipeline

import org.apache.spark.sql.SparkSession

object Main extends App {

  lazy val spark = SparkSession
    .builder()
    .appName("Top 10 airports")
    .master("local[*]")
    .getOrCreate()

  FileDownloader(
    "https://raw.githubusercontent.com/jpatokal/openflights/master/data/routes.dat",
    "/tmp/routes.dat")
    .download


}
