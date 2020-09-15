## Top10SourceAirports

### Why

The application uses airlines/routes data taken from [https://openflights.org/data.html](https://openflights.org/data.html) 
and computes the top 10 most used source airports.

### How

The application provides both batch and stream processing via Apache Spark. The two features are provided through
two basic classes, TopAirportsBatchWriter for batch and TopAirportsStreamWriter for stream processing. Both classes
implement TopAirportsWriter trait.

The streaming processor writes the aggregated results in memory using `format("memory")`, the main reasons behind this decision are:
 - The memory sink is easily testable via the Spark SQL API. 
 - We know that the memory table will be always very small, only 10 rows.
 - It supports the `complete` output mode.

Additionally, we used `complete` as the output mode, since it supports all the streaming features that we need for our application
i.e: orderBy, aggregation. Finally, we know that given the size of the dataset (~2MB), it is safe to assume that we don't expect to face any OOM error.

As we will see next, there two basic usages of the solution. The first is to use as a library (i.e import it in databricks), and the second as an executable command-line program.

### Getting Started

#### Prerequisites
These are the mandatory packages that should be installed in your system before running the application:

1. [Java 8](https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html)
2. [SBT 1.3.13](http://eed3si9n.com/sbt-1.3.13)

#### Building the project
As mentioned above the application uses SBT. To build the project navigate to the project folder and execute the shell command:

```commandline
sbt clean compile package
```
This will compile the code and generate `top-airports-pipeline_2.12-0.1.jar` which you can import in order to use the provided API. 
If you decide to use the solution as a library, this is only required step. For instance, in order to use the batch API from databricks
you will need to attach `top-airports-pipeline_2.12-0.1.jar` to your cluster, and then make an API call as below:

```scala
import com.abiratsis.airport.pipeline.spark.TopAirportsBatchWriter

TopAirportsBatchWriter("dbfs:/FileStore/top-airports/routes.dat", exportPath)(spark).saveTop10Airports()
```

If you are willing to run the application in an environment where Scala and Spark are not installed, you will need to generate an executable (fat) jar. 
To create the fat jar follow the next steps:
- First navigate to the root folder of the project
- Execute the command: ``sbt assembly``

The command will build the code and generate a fat jar using the [sbt-assembly](https://github.com/sbt/sbt-assembly) plugin. The command will generate `tapipe.jar` and `tapipe.sh` under the folder `target/scala-2.12`.
We will need these two files, to be able to use the solution as an executable program, as we will see next.

### Running the application

To access the API in databricks please use the published notebook [here](https://databricks-prod-cloudfront.cloud.databricks.com/public/4027ec902e239c93eaaa8714f173bcfc/4632767432671086/2506865405105341/3460815830528730/latest.html)

As mentioned, users are able to use the application through the command line. To do so please follow the next steps:

- Navigate under the root folder i.e: `/Users/you/Documents/top-airports-pipeline`
- Execute `sbt assembly` from command line (if not already executed on the previous step)
- Navigate under `target\scala-2.12`
- Grant execute permission to `tapipe.sh` with `chmod +x tapipe.sh`

Now you are ready to run the program.

As discussed, there are two available modes for running the program, the batch and the streaming mode. Each mode, uses the corresponding 
arguments as seen below:

```commandline
  -d, --destination  <arg>   The destination file(batch) or table
                             name(streaming).
      --download-data        Whether we should download or not the source data,
                             i.e: routes.dat
  -f, --format  <arg>        The format of the exported file in batch mode.
                             Choices: csv, parquet, avro, text
  -i, --input-file  <arg>    The source file, i.e routes.dat.
  -m, --mode  <arg>          Batch or streaming mode. Choices: b, s
  -h, --help                 Show help message
  -v, --version              Show version of this program
```

Example1 streaming mode:
```commandline
./tapipe.sh -m "s" -i "/tmp/routes*" -d "memory_results"
```

Starting with, notice the `-m "s"` which indicates the streaming mode. Next we specify the input source with `-i "/tmp/routes*"` and 
eventually the destination(memory) with `-d "memory_results"`.

The command should print a similar output:
```commandline
Streaming results at 2020-09-15T15:28:05.074:

+---------------+-----------------+-----------+
|sourceAirportId|sourceAirportCode|sourceCount|
+---------------+-----------------+-----------+
|3682           |ATL              |915        |
|3830           |ORD              |558        |
|3364           |PEK              |535        |
|507            |LHR              |527        |
|1382           |CDG              |524        |
|340            |FRA              |497        |
|3484           |LAX              |492        |
|3670           |DFW              |469        |
|3797           |JFK              |456        |
|580            |AMS              |453        |
+---------------+-----------------+-----------+
```

Example2 batch mode:
```commandline
./tapipe.sh -m "b" -i "/tmp/routes.dat" -d "/tmp/memory_results_export" -f "parquet"
```
Similarly to the previous example we set the source with `-i "/tmp/routes.dat"` and specify the desired destination with 
`-d "/tmp/memory_results_export"`. Finally, in batch mode we can also specify the results format with `-f "parquet"`. 

Expected output:
```commandline
Batch results:
+---------------+-----------------+-----------+
|sourceAirportId|sourceAirportCode|sourceCount|
+---------------+-----------------+-----------+
|3682           |ATL              |915        |
|3830           |ORD              |558        |
|3364           |PEK              |535        |
|507            |LHR              |527        |
|1382           |CDG              |524        |
|340            |FRA              |497        |
|3484           |LAX              |492        |
|3670           |DFW              |469        |
|3797           |JFK              |456        |
|580            |AMS              |453        |
+---------------+-----------------+-----------+
```
## API/Classes
The main classes of the provided API are:
- [TopAirportsStreamWriter](src/main/scala/com/abiratsis/airport/pipeline/spark/TopAirportsStreamWriter.scala)
- [TopAirportsBatchWriter](src/main/scala/com/abiratsis/airport/pipeline/spark/TopAirportsBatchWriter.scala)
- [DataDownloader](src/main/scala/com/abiratsis/airport/pipeline/common/DataDownloader.scala)

### Built With

- [Java8](https://www.oracle.com/java/technologies/java8.html)
- [Scala 2.12.8](https://github.com/scala/scala/releases/tag/v2.12.11)
- [SBT 1.3.13](http://eed3si9n.com/sbt-1.3.13)
- [IntelliJ IDEA 2020.1.2 (Community Edition)](https://www.jetbrains.com/idea/download/?_ga=2.54098216.1255651148.1594796297-1235052800.1592331776&_gac=1.254147066.1594206455.Cj0KCQjw3ZX4BRDmARIsAFYh7ZKe6Vms4zLztf-tE5k8X8ZU1Nb0h9W724j94ou4mIF6At_fGaHTsbYaAi6gEALw_wcB#section=mac)

### Dependencies/packages

The application uses the next packages:
- [Spark 2.4.4](https://spark.apache.org/releases/spark-release-2-4-4.html)
- [scalatest 0.9.1](https://www.scalatest.org/release_notes/3.2.0)
- [scallop 3.5.1](https://github.com/scallop/scallop)

## Testing

To execute all the available tests navigate under the project root folder and execute the `test` command from SBT.

Or execute a specific test class using:
```commandline
testOnly *TopAirportBatchWriterSpec
```

**Thank you! :)**
