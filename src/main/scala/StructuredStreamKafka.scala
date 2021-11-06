package eci.edu.co

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger


object StructuredStreamKafka extends SparkSessionWrapper {

  def main(args: Array[String]): Unit = {
    run()
  }

  def run(): Unit = {
    spark.sparkContext.setLogLevel("Error")

    val df: DataFrame = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9093")
      .option("subscribe", "topic")
      .load()

    val df1 = df.select(
      col("key").cast("string").as("key"),
      col("value").cast("string").as("value"))
      //.groupBy(col("value")).count()

    df1.writeStream
      .format("console")
      .option("truncate", "false")
      .outputMode("append")
      .trigger(Trigger.ProcessingTime("10 seconds"))
      .start()
      .awaitTermination()
  }
}
