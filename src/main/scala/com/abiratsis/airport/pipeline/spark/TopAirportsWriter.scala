package com.abiratsis.airport.pipeline.spark

trait TopAirportsWriter {
  import org.apache.spark.sql.SparkSession
  import org.apache.spark.sql.types.StructType

  protected val spark : SparkSession
  protected val sourcePath: String
  protected val destination: String

  val routeSchema = new StructType()
    .add("airlineCode", "string")
    .add("airLineId", "int")
    .add("sourceAirportCode", "string")
    .add("sourceAirportId", "int")
    .add("destAirportCode", "string")
    .add("destAirportId", "int")
    .add("codeshare", "string")
    .add("stops", "int")
    .add("equipments", "string")
}
