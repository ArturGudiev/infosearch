package cs500ir.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

object HelloSpark {
  def main(args: Array[String]): Unit = {
//    val spark = SparkSession
//      .builder()
//      .appName("Spark SQL basic example")
//      .config("spark.some.config.option", "some-value")
//      .getOrCreate()

    val sparkConf = new SparkConf().setAppName("infoSearch")
    val sc = new SparkContext(sparkConf)

    // todo create RDD

    // todo transformations and actions

    // todo print result
    println("Result is")
  }
}
