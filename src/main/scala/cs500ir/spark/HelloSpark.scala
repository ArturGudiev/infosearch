package cs500ir.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.sql.functions._

object HelloSpark {
  def main(args: Array[String]): Unit = {
    LocalUtils.setStreamingLogLevels()
    val searchStr = "css"

    val sparkConf = new SparkConf().setAppName("infoSearch").setMaster("local");
    val sc = new SparkContext(sparkConf)

    val spark = SparkSession
      .builder()
      .appName("Spark SQL basic example")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    //get best reviewer(через файл Артура)
    val df = spark.read.json(sc.wholeTextFiles("contributors2.json").values)
    df.show()

    val finaldf = df.select("repository", "contributors")
      .withColumn("additions", explode(col("contributors.additions")))
      .withColumn("login", explode(col("contributors.login")))
      .drop("contributors").drop("repository")
      .groupBy("login").sum("additions")
      .orderBy(desc("sum(additions)"))
      .take(5)


//    println(finaldf)
    println(finaldf.deep.mkString("\n"))


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
