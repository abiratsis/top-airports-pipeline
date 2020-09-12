package com.abiratsis.airport.pipeline.spark

import org.apache.spark.sql.SparkSession

trait SharedSparkSession {
  implicit lazy val spark = SparkSession
    .builder()
    .appName("Top 10 airports")
    .master("local[2]")
    .getOrCreate()

  spark.sparkContext.setLogLevel("WARN")
}
