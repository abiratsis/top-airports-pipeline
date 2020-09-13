package com.abiratsis.airport.pipeline

import java.util.concurrent.ThreadLocalRandom

import com.abiratsis.airport.pipeline.common.{CommandLineHandler, FileDownloader}
import com.abiratsis.airport.pipeline.spark.{TopAirportsBatchWriter, TopAirportsStreamWriter}
import org.apache.spark.sql.SparkSession
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import sun.misc.Signal
import sun.misc.SignalHandler

object Main extends App {

  val userInput = new CommandLineHandler(args)

  if(userInput.downloadData.toOption.get) {
    FileDownloader(
      "https://raw.githubusercontent.com/jpatokal/openflights/master/data/routes.dat",
      userInput.inputFile.toOption.get
    ).download
  }

  implicit lazy val spark = SparkSession
    .builder()
    .appName("Top 10 airports")
    .master("local[*]")
    .getOrCreate()

  @volatile var stop = false
  userInput.mode.toOption match {
    case Some("b") => {
      TopAirportsBatchWriter(
        userInput.inputFile.toOption.get,
        userInput.destination.toOption.get,
        userInput.format.toOption.get
      ).saveTop10Airports()
    }
    case Some("s") => {
      val memoryTableName = userInput.destination.toOption.get
      val streamQuery = TopAirportsStreamWriter(
        userInput.inputFile.toOption.get,
        memoryTableName
      ).getTop10Airports()

      val f = Future {
        val exists = spark.sqlContext.tableNames().contains(memoryTableName)

        while (!stop && exists) {
          println(s"Table $memoryTableName exists.")
//          spark.sql(s"select * from ${memoryTableName}")
//            .drop("window")
//            .show(10, false)

          val random: ThreadLocalRandom = ThreadLocalRandom.current()
          val r = random.nextLong(10, 100 + 1)
          Thread.sleep(r)
        }
      }
      f onSuccess {
        case result => {
          streamQuery.stop()
          println(s"Success: $result")
        }
      }
      f onFailure {
        case t => {
          streamQuery.stop()
          println(s"Exception: ${t.getMessage}")
        }
      }

      streamQuery.awaitTermination()
    }

      Signal.handle(new Signal("INT"), new SignalHandler() {
        def handle(sig: Signal) {
          println("CTL+C was caught!")
          stop = true
        }
      })
  }

//  val streamQuery = TopAirportsStreamWriter("/tmp/routes.dat", "/tmp/export_top10_airports").getTop10Airports()
//  streamQuery.processAllAvailable()

//  TopAirportsBatchWriter("/tmp/routes.dat", "/tmp/export_top10_airports").saveTop10Airports()
//
//  spark.read.parquet("/tmp/export_top10_airports").show(false)

  spark.stop()
}
