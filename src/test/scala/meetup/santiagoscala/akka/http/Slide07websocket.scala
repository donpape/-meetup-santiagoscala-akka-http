package meetup.santiagoscala.akka.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Random, Success}

/**
  * Created by papelacho on 2017-10-08.
  */
object Slide07websocket extends App with LazyLogging {

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

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  import scala.concurrent.ExecutionContext.Implicits.global


  def randomFlow: Flow[Message, Message, Any] =
    Flow[Message].flatMapConcat {
      case tm: TextMessage =>
        tm.textStream.map(s => s.toLong).flatMapConcat { qty =>
          Source(randomIterable).take(qty).map(_.toString)
        } map { s =>
          TextMessage(Source.single(s))
        }
      case bm: BinaryMessage =>
        // ignore binary messages but drain content to avoid the stream being clogged
        bm.dataStream.runWith(Sink.ignore)
        Source.empty
    }

  val route = pathPrefix("websocket") {
    handleWebSocketMessages(randomFlow)
  } ~ pathEndOrSingleSlash {
    getFromFile("src/main/resources/html/index.html")
  }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080).onComplete {
    case Success(binding) => logger.info("binded to {}", binding)
    case Failure(error) => system.terminate().onComplete {
      _ => System.exit(1)
    }
  }

}
