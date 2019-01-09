import java.io.{BufferedWriter, File, FileWriter}
//import java.security.Security

import com.kodekutters.stix._
import com.kodekutters.taxii._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global

// tests
object TestApp {

  def main(args: Array[String]): Unit = {
    // for (SSL) TLS-1.2 in https
    // Security.setProperty("crypto.policy", "unlimiteΩd")
    test1()
    test2()
  }

  def test1(): Unit = {
    // a connection object with a 10 seconds timeout
    //   val conn = new TaxiiConnection("https://cti-taxii.mitre.org", "guest", "guest", 10)
    val conn = new TaxiiConnection("https://limo.anomali.com/api/v1/taxii2", "guest", "guest", 10)
    // the server endpoint
    val server = Server("/taxii/", conn)
    // async discovery
    server.discovery.map(d => println("\n---> test1 discovery " + Json.prettyPrint(Json.toJson(d))))
    // async api roots and collections
    server.apiRoots().map(apirootList => {
      apirootList.foreach(api => {
        val cols = Collections(api.api_root, conn)
        // async collections
        cols.collections().map(theList => println("....test1 collections=" + theList + "\n cols.thePath=" + cols.thePath + "\n"))
      })
    })
  }

  def test2(): Unit = {
    // a collection endpoint
    //  val col = new Collection("062767bd-02d2-4b72-84ba-56caef0f8658", "https://cti-taxii.mitre.org/stix/", "guest", "guest")
    val col = new Collection("107", "https://limo.anomali.com/api/v1/taxii2/feeds/", "guest", "guest")

    println("---> test2 collection path: " + col.thePath)

    // get the objects from the collection
    col.getObjects().map {
      case None => println("---> no bundle in this collection"); System.exit(1)
      case Some(bundle) =>
        println("---> test2 number of stix: " + bundle.objects.length)
        println("---> test2 number of indicator: " + bundle.objects.count(_.`type` == Indicator.`type`))
        // print all "indicator" objects
        bundle.objects.foreach(stix => if (stix.`type` == Indicator.`type`) println("---> test2 stix: " + Json.toJson(stix)))
        // create file to write the stix to
        val bw = new BufferedWriter(new FileWriter(new File("testfile.json")))
        // write the stix to file
        try {
          //  bundle.objects.foreach(stix => if (stix.`type` == Indicator.`type`) bw.write(Json.prettyPrint(Json.toJson(stix))))
          bw.write(Json.prettyPrint(Json.toJson(bundle)))
        } catch {
          case e: Exception => e.printStackTrace()
        }
        finally {
          bw.close()
          col.conn.close()
        }
        System.exit(1)
    }
  }

}