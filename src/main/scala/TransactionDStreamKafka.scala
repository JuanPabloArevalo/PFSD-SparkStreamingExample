import eci.edu.co.SparkSessionWrapper
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._


object TransactionDStreamKafka extends SparkSessionWrapper {

  def main(args: Array[String]): Unit = {
    run()
  }

  def run(): Unit = {

    /*

    val df = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9093")
      .option("subscribe", "transactions-topic")
      .option("startingOffsets", "earliest") // From starting
      .load()

    df.printSchema()
*/

    val spark = SparkSession.builder()
      .master("local[2]")
      .appName("streaming").getOrCreate()
    val sc = spark.sparkContext
    sc.setLogLevel("ERROR")
    val ssc = new StreamingContext(sc, Seconds(10))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9093",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[ByteArrayDeserializer],
      "group.id" -> "group001",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("transactions-topic")
    val stream = KafkaUtils.createDirectStream[String, Array[Byte]](
      ssc,
      PreferConsistent,
      Subscribe[String, Array[Byte]](topics, kafkaParams)
    )

    val lines = stream.map(record => Transaction.fromTransactionMessage(TransactionMessage.parseFrom(record.value())).category)
    val wordCounts = lines.map(x=>(x, 1L)).reduceByKey(_+_)
    wordCounts.print()
    ssc.start()
    ssc.awaitTermination()
  }

}
