package meetup.santiagoscala.akka.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

/**
  * Created by papelacho on 2017-10-08.
  */
object Slide02httpIntro extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future map/flatmap in the end
  implicit val executionContext = system.dispatcher

  /**
    * Akka Http:
    *  - Construido sobre Akka Streams
    *  - Tiene modo cliente y modo servidor
    *  - Modo servidor tiene api de bajo nivel y de alto nivel
    *  - soporte para websockets
    *
    * cual es el brillo?
    *  - Gente inteligente invento toda esta cosa
    *  - Reutilizacion de codigo
    *  - Tenemos backpreassure
    *  - Formas de probar todo
    *
    *
    * Cliente y servidor de Akka Http no son mas que Flow[HttpRequest, HttpResponse, _]
    *
    * HttpRequest ---> Magia Http Enchulada ---> HttpResponse
    *
    */

  // Servidor api bajo nivel
  val magia: HttpRequest => HttpResponse = {
    case HttpRequest(GET, Uri.Path("/"), _, _, _) => HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, "<html><body><img src='http://www.reactiongifs.com/r/mgc.gif'></body></html>"))
    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) => HttpResponse(entity = "PONG!")
    case HttpRequest(GET, Uri.Path("/crash"), _, _, _) => sys.error("BOOM!")
    case r: HttpRequest =>
      r.discardEntityBytes() // important to drain incoming HTTP Entity stream
      HttpResponse(404, entity = "Unknown resource!")
  }
  val bindingFuture = Http().bindAndHandleSync(magia, "localhost", 8080)


  //  // Modo Cliente
  //  println(Await.result(Http().singleRequest(HttpRequest(uri = "http://localhost:8080/ping")), 1.minute))
  //  println(Await.result(Http().singleRequest(HttpRequest(uri = "http://localhost:8080/crash")), 1.minute))
  //
  //  // apagar la maquina entregando el pool de ejecucion del future...
  //  system.terminate().onComplete(_ => System.exit(0))(scala.concurrent.ExecutionContext.Implicits.global)

}
