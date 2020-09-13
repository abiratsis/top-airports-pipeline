package com.abiratsis.airport.pipeline

import com.abiratsis.airport.pipeline.common.FileDownloader
import com.abiratsis.airport.pipeline.spark.TopAirportsBatchWriter
import org.apache.spark.sql.SparkSession

object Main extends App {

  implicit lazy val spark = SparkSession
    .builder()
    .appName("Top 10 airports")
    .master("local[*]")
    .getOrCreate()

//  FileDownloader(
//    "https://raw.githubusercontent.com/jpatokal/openflights/master/data/routes.dat",
//    "/tmp/routes.dat")
//    .download

  TopAirportsBatchWriter("/tmp/routes.dat", "/tmp/export_top10_airports").saveTop10Airports()
}
