package cs500ir.spark

import java.io.{BufferedReader, InputStreamReader}
import java.net.{HttpURLConnection, URL}

import org.apache.spark.internal.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver

import scala.util.parsing.json.JSON

case class Order(total: Double, items: List[Commit] = null)
case class Commit(commitId: String, authorId: Int)

class CommitsReceiver(searchStr: String) extends Receiver[Commit](StorageLevel.MEMORY_AND_DISK_2) with Logging {

  def onStart() {
    println("CommitsReceiver is starting...")
    // Start the thread that receives data over a connection
    new Thread("Socket Receiver") {
      override def run() { receive() }
    }.start()
  }

  def onStop(): Unit = stop("CommitsReceiver is stopping")


  /** Create a socket connection and receive data until receiver is stopped */
  private def receive() {
    var userInput: String = null
    var currentOrder: Order = null

    try {

      val page = 1
      val url = new URL("https://api.github.com/search/commits?q="+searchStr+"&page="+page+"&per_page=100")
      val conn = url.openConnection.asInstanceOf[HttpURLConnection]
      conn.setRequestMethod("GET")
      conn.setRequestProperty("Accept", "application/vnd.github.cloak-preview")

      if (conn.getResponseCode != 200) throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode)

      // Until stopped or connection broken continue reading
      val reader = new BufferedReader(new InputStreamReader(conn.getInputStream))
      userInput = reader.readLine()
      if (userInput == null) stop("Stream has ended")
      while (!isStopped() && userInput != null) {
        val jsonObject = JSON.parseFull(userInput)

        val globalMap = jsonObject.get.asInstanceOf[Map[String, Any]]
        val totalCount = globalMap.get("total_count").get.asInstanceOf[Double]
        val items = globalMap.get("items").get.asInstanceOf[List[Any]]

        for (item <- items){
          val parsedItem = item.asInstanceOf[Map[String, Any]]
          val commitId = parsedItem.get("sha").get.asInstanceOf[String]
          val commiter = parsedItem.get("committer").get.asInstanceOf[Map[String, Any]]
          if(commiter != null){
            val commiterId =  commiter.get("id").get.asInstanceOf[Double].intValue()
            store(Commit(commitId, commiterId))
          }
        }
      }
      reader.close()

      // Restart in an attempt to connect again when server is active again
      restart("Trying to connect again")
    } catch {
      case e: java.net.ConnectException =>
        // restart if could not connect to server
        restart("Error connecting to github", e)
      case t: Throwable =>
        // restart if there is any other error
        restart("Error receiving data", t)
    }
  }
}
