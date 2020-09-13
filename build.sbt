
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
  "org.apache.spark"     %% "spark-sql"  % sparkVersion,
  "org.apache.spark"     %% "spark-hive" % sparkVersion,

  //Testing
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",

  //Command-line
  "org.rogach" %% "scallop" % "3.4.0"
)

assemblyJarName in assembly:= "tapipe.jar"

// Deduplication error, check the link below for more information
// https://stackoverflow.com/questions/25144484/sbt-assembly-deduplication-found-error
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
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

exportJars := true