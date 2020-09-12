name := "top-airports-pipeline"

version := "0.1"

scalaVersion := "2.11.12"

val sparkVersion = "2.4.4"

//resolvers ++= Seq(
//  "bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven/",
//  "maven" at "https://repo1.maven.org/maven2/",
//  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
//)

libraryDependencies ++= Seq(
  //Spark
  "org.apache.spark"     %% "spark-core" % sparkVersion,
  "org.apache.spark"     %% "spark-sql"  % sparkVersion,
  "org.apache.spark"     %% "spark-hive" % sparkVersion,

  //Testing
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "org.scalactic" %% "scalactic" % "3.1.1",

  //Command-line
  "org.rogach" %% "scallop" % "3.4.0"
)