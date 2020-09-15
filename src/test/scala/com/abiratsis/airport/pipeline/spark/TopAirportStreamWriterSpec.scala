package com.abiratsis.airport.pipeline.spark

import com.abiratsis.airport.pipeline.exceptions.NullOrEmptyArgumentException
import org.scalatest.flatspec.AnyFlatSpec

class TopAirportStreamWriterSpec extends AnyFlatSpec with SharedSparkSession{

  val memoryTable = "top10_memory_test"
  "getTop10Airports" should "return top 10 source airports from memory" in{
    import spark.implicits._
    val inputPath = "src/test/resources/test_routes.csv*"

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
    ).toDF("sourceAirportId", "sourceAirportCode", "sourceCount")

    assert(expectedDf.except(actualDf).isEmpty)
    streamQuery.stop()
  }

  "getTop10Airports" should "ignore rows with empty sourceAirportId" in{
    val inputPath = "src/test/resources/test_routes_emptyid.csv*"

    val streamQuery = TopAirportsStreamWriter(inputPath, memoryTable).getTop10Airports()
    streamQuery.processAllAvailable()

    val actualDf = spark.sql(s"select * from $memoryTable").drop("window")

    val actualCount = actualDf.count()
    val expectedCount = 6

    assert(actualCount == expectedCount)
    streamQuery.stop()
  }

  "getTop10Airports" should "should ignore rows with invalid schema" in{
    import spark.implicits._
    val inputPath = "src/test/resources/test_routes_invalidschema.csv*"

    val streamQuery = TopAirportsStreamWriter(inputPath, memoryTable).getTop10Airports()
    streamQuery.processAllAvailable()

    val actualDf = spark.sql(s"select * from $memoryTable").drop("window")
    val expectedDf = Seq(
      (347, "NUE", 1),
      (1418, "NTE", 1),
      (1335, "LYS", 1),
      (4317, "SAW", 1),
      (371, "PAD", 1),
      (350, "STR", 1),
      (348, "LEJ", 1),
      (421, "HEL", 2),
      (346, "MUC", 2)
    ).toDF("sourceAirportId", "sourceAirportCode", "sourceCount")

    assert(actualDf.count() == 9)
    assert(expectedDf.except(actualDf).isEmpty)
    streamQuery.stop()
  }

  "apply" should "should throw NullOrEmptyArgumentException given null or empty paths" in{
    var message = intercept[NullOrEmptyArgumentException] {
      TopAirportsStreamWriter("", memoryTable)
    }.getMessage

    assert(message == s"sourcePath can't be null or empty.")

    message = intercept[NullOrEmptyArgumentException] {
      TopAirportsStreamWriter("/some/input", null)
    }.getMessage

    assert(message == s"destination can't be null or empty.")
  }
}
