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

    //get best reviewer(через файл Артура)
    /*val df = spark.read.json(spark.sparkContext.wholeTextFiles("contributors2.json").values)
    df.show()

    val finaldf = df.select("repository", "contributors")
      .withColumn("additions", explode(col("contributors.additions")))
      .withColumn("login", explode(col("contributors.login")))
      .drop("contributors").drop("repository")
      .groupBy("login").sum("additions")
      .orderBy(desc("sum(additions)"))
      .take(1).last
    println(finaldf)*/

    LocalUtils.setStreamingLogLevels()
    val searchStr = "css"

    val sparkConf = new SparkConf().setAppName("infoSearch")
    val sc = new SparkContext(sparkConf)

    // сейчас эта часть работает, но если брать данные из файла, как предложил Артур, это не надо использовать
    /*
    val ssc = new StreamingContext(sparkConf, Seconds(20))
    val customReceiverStream: DStream[Commit] = ssc.receiverStream(new CommitsReceiver(searchStr))

    val commits = customReceiverStream.map(item => (item.authorId, 1))
    val commitsCounts = commits.reduceByKey(_ + _)
    commitsCounts.print()

    ssc.start()
    ssc.awaitTermination()
    */
    // todo create RDD

    // todo transformations and actions

    // todo print result
    println("Result is")
  }
}
