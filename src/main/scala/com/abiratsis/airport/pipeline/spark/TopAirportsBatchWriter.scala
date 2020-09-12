package com.abiratsis.airport.pipeline.spark
import org.apache.spark.sql.SparkSession

class TopAirportsBatchWriter private(val spark: SparkSession,
                                      val sourcePath: String,
                                      val exportPath: String,
                                      val format: String)
  extends TopAirportsWriter {
  import org.apache.spark.sql.functions.{count, desc}

  def exportTopSourceAirports(): Unit = {
    import spark.implicits._

    spark.read.schema(routeSchema).csv(sourcePath)
      .where($"sourceAirportId".isNotNull) // we found some null values
      .groupBy("sourceAirportId", "sourceAirportCode")
      .agg(count("sourceAirportId").as("sourceCount"))
      .orderBy(desc("sourceCount"))
      .write
      .format(format)
      .mode("overwrite")
      .save(exportPath)
  }
}

object TopAirportsBatchWriter{
  import com.abiratsis.airport.pipeline.exceptions.NullOrEmptyArgumentException
  import com.abiratsis.airport.pipeline.common.String

  val validFormats = Set("parquet", "text", "csv", "avro")
  def apply(sourcePath: String, exportPath: String, format: String = "parquet")(implicit spark: SparkSession):
    TopAirportsBatchWriter = {

    if(String.isNullOrEmpty(sourcePath))
      throw new NullOrEmptyArgumentException("sourcePath")

    if(String.isNullOrEmpty(exportPath))
      throw new NullOrEmptyArgumentException("exportPath")

    if(String.isNullOrEmpty(format))
      throw new NullOrEmptyArgumentException("format")

    if (!validFormats.contains(format))
      throw new IllegalArgumentException(s"Format should be one of the:${validFormats.mkString(",")}")

    new TopAirportsBatchWriter(spark, sourcePath, exportPath, format)
  }
}

