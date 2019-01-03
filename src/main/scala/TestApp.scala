import java.io.{BufferedWriter, File, FileWriter}

import com.kodekutters.stix._
import com.kodekutters.taxii._

import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

// tests
object TestApp {

  def main(args: Array[String]): Unit = {
    test1()
    test2()
  }

  def test1(): Unit = {
    // a connection object with a 10 seconds timeout
    val conn = new TaxiiConnection("https://limo.anomali.com/api/v1/taxii2", "guest", "guest", 10)
    // the server endpoint
    val server = Server("/taxii/", conn)
    // async discovery
    server.discovery.map(d => println("\n---> discovery " + Json.prettyPrint(Json.toJson(d))))
    // async api roots and collections
    server.apiRoots().map(apirootList => {
      apirootList.foreach(api => {
        println("....apirootList api=" + api)
        val cols = Collections(api.api_root, conn)
        println("....apirootList cols=" + cols)
        // async collections
        cols.collections() onComplete {
          case Success(theList) => println("....collections=" + theList + "\n cols.thePath=" + cols.thePath + "\n")
          case Failure(e) => println(".... test1 Failure result " + e)
        }
      })
    })
  }

  def test2(): Unit = {
    // a collection endpoint
    val col = new Collection("107", "https://limo.anomali.com/api/v1/taxii2/feeds/", "guest", "guest")
    // get the objects from the collection
    col.getObjects().map {
      case None => println("---> no bundle in this collection"); System.exit(1)
      case Some(bundle) =>
        println("---> number of stix: " + bundle.objects.length)
        println("---> number of indicator: " + bundle.objects.count(_.`type` == Indicator.`type`))
        // print all "indicator" objects
        var count = 0
        bundle.objects.foreach(stix => {
          count += 1
          if (stix.`type` == Indicator.`type`) println("---> stix " + count + ": " + Json.toJson(stix))
         })
        // create file to write the stix to
        val bw = new BufferedWriter(new FileWriter(new File("testfile")))
        // write all indicators stix to file
        try {
          bundle.objects.foreach(stix => if (stix.`type` == Indicator.`type`) bw.write(Json.prettyPrint(Json.toJson(stix))))
        } catch {
          case e: Exception => e.printStackTrace()
        }
        finally {
          bw.close()
        }
        System.exit(1)
    }
  }

}