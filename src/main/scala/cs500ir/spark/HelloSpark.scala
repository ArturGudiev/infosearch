package cs500ir.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

object HelloSpark {
  def main(args: Array[String]): Unit = {
//    val spark = SparkSession
//      .builder()
//      .appName("Spark SQL basic example")
//      .config("spark.some.config.option", "some-value")
//      .getOrCreate()

    LocalUtils.setStreamingLogLevels()
    val searchStr = "css"

    val sparkConf = new SparkConf().setAppName("infoSearch")
    val sc = new SparkContext(sparkConf)
//    val ssc = new StreamingContext(sparkConf, Seconds(20))
//    val customReceiverStream: DStream[Commit] = ssc.receiverStream(new CommitsReceiver(searchStr))
//    customReceiverStream.foreachRDD { rdd =>
//      val commit = rdd
//
//    }
//    ssc.start()
//    ssc.awaitTermination()
    // todo create RDD

    // todo transformations and actions

    // todo print result
    println("Result is")
  }
}
