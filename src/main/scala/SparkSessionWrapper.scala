package eci.edu.co

import org.apache.spark.sql.SparkSession

trait SparkSessionWrapper extends Serializable {

  // Starting point of any application
  lazy val spark: SparkSession = {
    SparkSession
      .builder()
      .master("local[*]")
      .appName("sparkLocalSessionStream")
      .getOrCreate()
  }
}
