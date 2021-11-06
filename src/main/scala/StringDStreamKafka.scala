import eci.edu.co.SparkSessionWrapper
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._

object StringDStreamKafka extends SparkSessionWrapper {

  def main(args: Array[String]): Unit = {
    run()
  }

  def run(): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("streaming").getOrCreate()

    val sc = spark.sparkContext

    sc.setLogLevel("ERROR")
    val streamCont = new StreamingContext(sc, Seconds(1))



    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9093",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "group001",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("topic")
    val stream = KafkaUtils.createDirectStream[String, String](
      streamCont, PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val lines = stream.map(record => record.value())
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x=>(x, 1L)).reduceByKey(_+_)
    wordCounts.print()

    streamCont.start()
    streamCont.awaitTermination()
  }

}
