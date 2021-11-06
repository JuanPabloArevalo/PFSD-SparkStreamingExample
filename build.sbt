
name := "Proyecto"

version := "0.1"

scalaVersion := "2.13.6"

val sparkVersion = "3.2.0"

mainClass in Compile := Some("eci.edu.co.Exercise01")

libraryDependencies ++= Seq( "org.apache.spark" %% "spark-core" % sparkVersion,
"org.apache.spark" %% "spark-sql" % sparkVersion,
"org.apache.spark" %% "spark-streaming" % sparkVersion,
"org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
"org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}