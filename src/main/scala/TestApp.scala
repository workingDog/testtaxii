import java.io.{BufferedWriter, File, FileWriter}
import java.security.Security

import com.kodekutters.stix._
import com.kodekutters.taxii._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global


// test
object TestApp {

  def main(args: Array[String]): Unit = {
    // needed for (SSL) TLS-1.2 in https, requires jdk1.8.0_152
    Security.setProperty("crypto.policy", "unlimited")
    // the collection endpoint
    val col = new Collection("34f8c42d-213a-480d-9046-0bd8a8f25680", "https://test.freetaxii.com:8000/osint/", "user", "psw")
    // get the objects from the server collection
    col.getObjects().map {
      case None => println("---> no bundle in this collection")
      case Some(bundle) =>
        println("---> number of stix: " + bundle.objects.length)
        println("---> number of indicator: " + bundle.objects.count(_.`type` == Indicator.`type`))
        // print all "indicator" objects
        bundle.objects.foreach(stix => if (stix.`type` == Indicator.`type`) println("---> stix: " + Json.toJson(stix)))
        // write all indicators stix to file
        try {
          val bw = new BufferedWriter(new FileWriter(new File("testfile")))
          bundle.objects.foreach(stix =>
            if (stix.`type` == Indicator.`type`) bw.write(Json.prettyPrint(Json.toJson(stix)))
          )
          bw.close()
        } catch {
          case e: Exception => e.printStackTrace()
        }
    }
  }

}