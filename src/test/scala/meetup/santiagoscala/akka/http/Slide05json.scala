package meetup.santiagoscala.akka.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.LongNumber
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import spray.json.DefaultJsonProtocol

import scala.collection.mutable

/**
  * Created by papelacho on 2017-10-08.
  */
class Slide05json extends WordSpec with Matchers with ScalatestRouteTest {

  import DefaultJsonProtocol._
  import SprayJsonSupport._

  case class Contacto(nombre: String, apellido: String)

  implicit val cFormat = jsonFormat2(Contacto)


  val store = mutable.Map[Long, Contacto]()


  val route = pathEndOrSingleSlash {
    get {
      complete(store.toList.sortBy(_._1).map(_._2))
    } ~ post {
      entity(as[List[Contacto]]) { body =>
        store.clear()
        store ++= body.zipWithIndex.map(t => t._2.toLong -> t._1)
        complete(StatusCodes.OK)
      }
    } ~ delete {
      store.clear()
      complete(StatusCodes.OK)
    }
  } ~ pathPrefix(LongNumber) { id =>
    get {
      store.get(id) match {
        case Some(contact) => complete(contact)
        case None => complete(StatusCodes.NotFound)
      }
    } ~ post {
      entity(as[Contacto]) { body =>
        store += (id -> body)
        complete(StatusCodes.OK)
      }
    } ~ delete {
      store -= id
      complete(StatusCodes.OK)
    }
  }

  "json" should {
    "crear contactos" in {
      Post("/1", Contacto("papelucho", "nomas")) ~> route ~> check {
        status shouldEqual StatusCodes.OK
      }
      Post("/2", Contacto("ayleen", "rock")) ~> route ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "listar contactos" in {
      Get("/") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[List[Contacto]] shouldEqual List(Contacto("papelucho", "nomas"), Contacto("ayleen", "rock"))
      }
    }

  }

}
