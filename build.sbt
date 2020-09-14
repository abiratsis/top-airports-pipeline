
name := "top-airports-pipeline"

version := "0.1"

scalaVersion := "2.12.8"
val sparkVersion = "2.4.4"

libraryDependencies ++= Seq(
  //Spark
  "org.apache.spark"     %% "spark-sql"  % sparkVersion,
  "org.apache.spark"     %% "spark-sql"  % sparkVersion,

  //Testing
  "org.scalatest" %% "scalatest" % "3.2.0" % "test"

  //Command-line
//  "org.rogach" %% "scallop" % "3.5.1"
)

assemblyJarName in assembly:= "tapipe.jar"
scalacOptions := Seq("-unchecked", "-deprecation")

// Deduplication error, check the link below for more information
// https://stackoverflow.com/questions/25144484/sbt-assembly-deduplication-found-error
//assemblyMergeStrategy in assembly := {
//  case PathList("META-INF", _*) => MergeStrategy.discard
//  case _ => MergeStrategy.first
//}

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

exportJars := true