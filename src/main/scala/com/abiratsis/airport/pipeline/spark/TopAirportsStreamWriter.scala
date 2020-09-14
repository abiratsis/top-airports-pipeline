package com.abiratsis.airport.pipeline.spark

import org.apache.spark.sql.SparkSession

class TopAirportsStreamWriter private(val spark: SparkSession,
                              val sourcePath: String,
                              val destination: String)
  extends TopAirportsWriter {

  import org.apache.spark.sql.functions.{count, desc, current_timestamp, window}
  import org.apache.spark.sql.streaming.StreamingQuery
  import spark.implicits._

  def getTop10Airports(): StreamingQuery = {
    spark
      .readStream
      .schema(routeSchema) // Specify schema of the csv files
      .option("delimiter", ",")
      .csv(sourcePath)
      .withColumn("processingTime",current_timestamp())
      .where($"sourceAirportId".isNotNull) // we found some null values
      .groupBy(
        window($"processingTime", "10 milliseconds"),
        $"sourceAirportId",
        $"sourceAirportCode"
      )
      .agg(count("sourceAirportId").as("sourceCount"))
      .orderBy(desc("sourceCount"))
      .limit(10)
      .writeStream
      .format("memory")
      .queryName(destination)
      .outputMode("complete")
      .start
  }
}

object TopAirportsStreamWriter {

  import com.abiratsis.airport.pipeline.exceptions.NullOrEmptyArgumentException
  import com.abiratsis.airport.pipeline.common.String

  def apply(sourcePath: String, destination: String)(implicit spark: SparkSession):
  TopAirportsStreamWriter = {

    if (String.isNullOrEmpty(sourcePath))
      throw new NullOrEmptyArgumentException("sourcePath")

    if (String.isNullOrEmpty(destination))
      throw new NullOrEmptyArgumentException("destination")

    new TopAirportsStreamWriter(spark, sourcePath, destination)
  }
}
