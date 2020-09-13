package com.abiratsis.airport.pipeline.spark

import org.scalatest.flatspec.AnyFlatSpec

class TopAirportStreamWriterSpec extends AnyFlatSpec with SharedSparkSession{
  "getTop10Airports" should "return top 10 source airports from memory" in{
    import spark.implicits._
    val inputPath = "src/test/resources/test_routes.csv*"

    val memoryTable = "top10_memory_test"
    val streamQuery = TopAirportsStreamWriter(inputPath, memoryTable).getTop10Airports()
    streamQuery.processAllAvailable()

    val actualDf = spark.sql(s"select * from $memoryTable").drop("window")

    assert(true)
    val expectedDf = Seq(
      (1611, "LNZ", 1),
      (347, "NUE", 1),
      (1418, "NTE", 1),
      (1335, "LYS", 1),
      (4317, "SAW", 1),
      (371, "PAD", 1),
      (350, "STR", 1),
      (348, "LEJ", 1),
      (421, "HEL", 3),
      (346, "MUC", 3)
    ).toDF

    assert(expectedDf.except(actualDf).isEmpty)
  }

}
