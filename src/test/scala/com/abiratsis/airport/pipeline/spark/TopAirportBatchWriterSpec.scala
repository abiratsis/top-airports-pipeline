package com.abiratsis.airport.pipeline.spark

import com.abiratsis.airport.pipeline.exceptions.NullOrEmptyArgumentException
import org.scalatest.flatspec.AnyFlatSpec

class TopAirportBatchWriterSpec extends AnyFlatSpec with SharedSparkSession{
  val exportPath = "/tmp/export"

  "exportTopSourceAirports" should "export top 10 source airports in FS" in{
    import spark.implicits._
    import com.abiratsis.airport.pipeline.common.Util.deleteDir
    val inputPath = "src/test/resources/test_routes.csv"

    TopAirportsBatchWriter(inputPath, "/tmp/export").saveTop10Airports()

    val actualDf = spark.read.parquet(exportPath)
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
    assert(deleteDir(exportPath), s"Failed deleting directory $exportPath")
  }

  "exportTopSourceAirports" should "ignore rows with empty sourceAirportId" in{
    import com.abiratsis.airport.pipeline.common.Util.deleteDir
    val inputPath = "src/test/resources/test_routes_emptyid.csv"

    TopAirportsBatchWriter(inputPath, "/tmp/export").saveTop10Airports()

    val exportPath = "/tmp/export"
    val actualCount = spark.read.parquet(exportPath).count()
    val expectedCount = 6

    assert(actualCount == expectedCount)
    assert(deleteDir(exportPath), s"Failed deleting directory $exportPath")
  }

  "exportTopSourceAirports" should "should ignore rows with invalid schema" in{
    import spark.implicits._
    import com.abiratsis.airport.pipeline.common.Util.deleteDir

    val inputPath = "src/test/resources/test_routes_invalidschema.csv"

    TopAirportsBatchWriter(inputPath, "/tmp/export").saveTop10Airports()

    val exportPath = "/tmp/export"
    val actualDf = spark.read.parquet(exportPath).cache()
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
    assert(deleteDir(exportPath), s"Failed deleting directory $exportPath")
  }

  "apply" should "should throw NullOrEmptyArgumentException given null for empty paths and format" in{
    var message = intercept[NullOrEmptyArgumentException] {
      TopAirportsBatchWriter("", exportPath)
    }.getMessage

    assert(message == s"sourcePath can't be null or empty.")

    message = intercept[NullOrEmptyArgumentException] {
      TopAirportsBatchWriter("/some/input", null)
    }.getMessage

    assert(message == s"destination can't be null or empty.")

    message = intercept[NullOrEmptyArgumentException] {
      TopAirportsBatchWriter("/some/input", exportPath, "")
    }.getMessage

    assert(message == s"format can't be null or empty.")
  }
}
