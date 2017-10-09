package meetup.santiagoscala.akka.http

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Random, Success}

/**
  * Created by papelacho on 2017-10-08.
  */
object Slide06streaming extends App with LazyLogging {

  val random = Random
  val randomIterable = new scala.collection.immutable.Iterable[Long] {

    override def iterator = new Iterator[Long] {
      override def hasNext = true

      override def next() = {
        val ret = random.nextLong()
        logger.info(s"next random: $ret")
        ret
      }
    }
  }

  val route = pathPrefix(LongNumber) { qty =>
    val byteSource: Source[ByteString, NotUsed] = Source(randomIterable).take(qty).map(l => s"$l\n").map(ByteString(_))
    complete(HttpEntity(`text/plain(UTF-8)`, byteSource))
  }


  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  import scala.concurrent.ExecutionContext.Implicits.global

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080).onComplete {
    case Success(binding) => logger.info("binded to {}", binding)
    case Failure(error) => system.terminate().onComplete { _ => System.exit(1) }
  }


  // curl -sS localhost:8080/1000000
  // C:\bin\ProcessExplorer
}
