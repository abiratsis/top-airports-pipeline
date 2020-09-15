package com.abiratsis.airport.pipeline

import com.abiratsis.airport.pipeline.common.{CommandLineHandler, DataDownloader, UserInput}
import com.abiratsis.airport.pipeline.spark.{TopAirportsBatchWriter, TopAirportsStreamWriter}
import java.util.concurrent.ThreadLocalRandom

import org.apache.spark.sql.SparkSession

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import sun.misc.Signal

import scala.util.{Failure, Success}

object Main{
  implicit lazy val spark = SparkSession
    .builder()
    .appName("Top 10 airports")
    .master("local[*]")
    .getOrCreate()

  @volatile var stop = false

  def main(args: Array[String]): Unit = {
    val cmd = CommandLineHandler(args)
    val userInput = UserInput(cmd)

    if (userInput.downloadData) {
      DataDownloader(
        "https://raw.githubusercontent.com/jpatokal/openflights/master/data/routes.dat",
        userInput.inputFile
      ).download
    }

    spark.sparkContext.setLogLevel("WARN")
    Signal.handle(new Signal("INT"), (_: Signal) => stop = true)

    userInput.mode match {
      case "b" => {
        executeBatch(userInput)
      }
      case "s" => {
        executeStreaming(userInput)
      }
      case _ => throw new Exception("Unhandled case")
    }
  }

  private def executeBatch(userInput: UserInput) = {
    TopAirportsBatchWriter(
      userInput.inputFile,
      userInput.destination,
      userInput.format
    ).saveTop10Airports()

    println("Batch results:")
    spark.read.format(userInput.format).load(userInput.destination).show(false)
  }

  def executeStreaming(userInput: UserInput) : Unit = {
    val memoryTableName = userInput.destination
    val streamQuery = TopAirportsStreamWriter(
      userInput.inputFile,
      memoryTableName
    ).getTop10Airports()

    val f = Future {
      while (!stop) {
        val resultsDf = spark.sql(s"select * from $memoryTableName").drop("window")

        if (!resultsDf.isEmpty) {
          //clean the screen
          System.out.print("\u001b[H\u001b[2J");
          System.out.flush();

          println(s"Streaming results at ${java.time.LocalDateTime.now}:\n")
          resultsDf.show(10, false)
          println("\nPress CTL+R to terminate the program.")
        }

        val random: ThreadLocalRandom = ThreadLocalRandom.current()
        val r = random.nextLong(100, 1000 + 1)
        Thread.sleep(r)
      }
      "Future was successfully terminated."
    }
    f onComplete {
      case Success(result) => {
        streamQuery.stop()
        println(s"Success: $result")
      }
      case Failure(t) => {
        streamQuery.stop()
        println("An error has occurred: " + t.getMessage)
      }
    }

    streamQuery.awaitTermination()
  }
}
