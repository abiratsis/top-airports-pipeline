package com.abiratsis.airport.pipeline.spark

trait TopAirportsWriter {
  import org.apache.spark.sql.SparkSession
  import org.apache.spark.sql.types.StructType

  val spark : SparkSession
  val sourcePath: String
  val exportPath: String
  val format: String

  def exportTop10SourceAirports: Unit

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

object TopAirportsWriter{
  val validFormats = Set("parquet", "text", "csv", "avro")
}
