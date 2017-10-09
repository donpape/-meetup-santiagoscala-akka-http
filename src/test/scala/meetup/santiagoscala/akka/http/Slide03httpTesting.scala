package meetup.santiagoscala.akka.http

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server.Directives._
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by papelacho on 2017-10-08.
  */
class Slide03httpTesting extends WordSpec with Matchers with ScalatestRouteTest {


  /**
    * DSL => Domain Specific Language
    *
    * RoutingDSL: Permite expresar rutas de forma simple y detallada usando Directives
    * Route: type Route = RequestContext => Future[RouteResult]
    * RequestContext: Toda la informacion que acompaña al HttpRequest
    * RouteResult: basicamente un Try[HttpResponse]
    * Directive: Unidad basica de interaccion con el request http
    *
    */


  val route = get {
    pathSingleSlash {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<html><body>Hello world!</body></html>"))
    } ~
      path("ping") {
        complete("PONG!")
      } ~
      path("pathParam" / Segment) { pathParam =>
        complete(pathParam)
      } ~
      path("urlParam") {
        parameter('param.as[String]) { param =>
          complete(param)
        }
      } ~
      path("header") {
        headerValueByName("header") { header =>
          complete(header)
        }
      }
  }

  "route" should {
    "servir el hola mundo" in {
      Get("/") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual "<html><body>Hello world!</body></html>"
      }
    }

    "pathParam" in {
      Get("/pathParam/valor") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual "valor"
      }
    }
    "urlParam" in {
      Get("/urlParam?param=valor") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual "valor"
      }
    }
    "header" in {
      Get("/header") ~> RawHeader("header", "valor") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual "valor"
      }
    }
  }
}
