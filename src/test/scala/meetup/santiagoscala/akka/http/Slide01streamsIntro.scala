package meetup.santiagoscala.akka.http

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by papelacho on 2017-10-08.
  */
object Slide01streamsIntro extends App {

  /**
    * Stream:
    *  - Nativo Java 8
    *  - Nativo Scala
    *  - Version nueva en Java 9
    *
    * Source --> magia --> Sink
    *
    */

  val magia: Int => String = (i) => s"hola $i"

  println("hola soy un stream")
  1.until(10).toStream // source
    .map(magia) // magia
    .foreach(println) // sink


  /**
    * Akka Stream:
    *  - basado en ideologia reactive streams
    *  - construido sobre actores
    *
    * Source --> magia enchulada --> Sink
    *
    * enchules mas notables:
    *  - un poco mas complejo que stream, pero mas poderoso
    *  - manejo detallado de paralelismo
    *  - backpreassure
    *
    */

  // se necesita un actory system
  implicit val system = ActorSystem("santaigoscala")

  // el materializer es el que crea actores y devuelve valores
  implicit val materializer = ActorMaterializer()


  println("yo soy un akka stream")

  val source = Source.fromIterator(() => 1.until(10).toIterator)

  val magiaEnchulada: Flow[Int, String, _] = Flow[Int].map(magia)

  val sink = Sink.foreach(println)

  val stream1 = source.via(magiaEnchulada).toMat(sink)(Keep.right)

  Await.ready(stream1.run(), 1.minute)


  // apagar la maquina entregando el pool de ejecucion del future...
  system.terminate().onComplete(_ => System.exit(0))(scala.concurrent.ExecutionContext.Implicits.global)
}
