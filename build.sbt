
name := "top-airports-pipeline"

version := "0.1"

scalaVersion := "2.12.8"
val sparkVersion = "2.4.4"

libraryDependencies ++= Seq(
  //Spark
  "org.apache.spark" %% "spark-sql"  % sparkVersion,
  "org.apache.spark" %% "spark-sql"  % sparkVersion,

  //Testing
  "org.scalatest" %% "scalatest" % "3.2.0" % "test",

  //Command-line
  "org.rogach" %% "scallop" % "3.5.1",
)

assemblyJarName in assembly:= "tapipe.jar"
scalacOptions := Seq("-unchecked", "-deprecation")

/**
 * Dependency conflicts, Deduplication error, check the links below for more information.
 *
 * https://stackoverflow.com/questions/30446984/spark-sbt-assembly-deduplicate-different-file-contents-found-in-the-followi
 * https://queirozf.com/entries/creating-scala-fat-jars-for-spark-on-sbt-with-sbt-assembly-plugin
 * https://stackoverflow.com/questions/25144484/sbt-assembly-deduplication-found-error
 */

assemblyMergeStrategy in assembly := {
  case PathList("org","aopalliance", _*) => MergeStrategy.last
  case PathList("javax", "inject", _*) => MergeStrategy.last
  case PathList("javax", "servlet", _*) => MergeStrategy.last
  case PathList("javax", "activation", _*) => MergeStrategy.last
  case PathList("org", "apache", _*) => MergeStrategy.last
  case PathList("com", "google", _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", _*) => MergeStrategy.last
  case PathList("com", "codahale", _*) => MergeStrategy.last
  case PathList("com", "yammer", _*) => MergeStrategy.last
  case PathList("io", "netty", _*) => MergeStrategy.last
  case PathList("com", "google", "code", "findbugs", _*) => MergeStrategy.first

  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case "git.properties" => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

lazy val postBuild  = taskKey[Unit]("post build")
postBuild := {
  val log = streams.value.log

  val execScriptSource = (baseDirectory.value / "scripts/tapipe.sh")
  val execScriptTarget = crossTarget.value / "tapipe.sh"

  log.info(s"Copying ${execScriptSource.getPath} to ${execScriptTarget.getPath}")
  IO.copyFile(execScriptSource, execScriptTarget)
  None
}

Compile / packageBin := (Compile / packageBin dependsOn postBuild).value
mainClass in assembly := Some("com.abiratsis.airport.pipeline.Main")
test in assembly := {}

exportJars := true