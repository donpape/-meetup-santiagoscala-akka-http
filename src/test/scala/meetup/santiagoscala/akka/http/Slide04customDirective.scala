package meetup.santiagoscala.akka.http

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsMissing
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Directive1}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server.Directives._
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by papelacho on 2017-10-08.
  */
class Slide04customDirective extends WordSpec with Matchers with ScalatestRouteTest {


  val tokenFromHeader: Directive1[Option[String]] = optionalHeaderValueByName("token")
  val tokenFromParam: Directive1[Option[String]] = parameterMap.map(map => map.get("token"))

  val hasToken: Directive1[String] = (tokenFromHeader & tokenFromParam) tflatMap {
    case (Some(token), _) => provide(token)
    case (_, Some(token)) => provide(token)
    case _ => reject
  }

  val route = hasToken { token =>
    complete(token)
  }

  "route" should {
    "extract header" in {
      Get("/") ~> RawHeader("token", "valor") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual "valor"
      }
    }

    "extract param" in {
      Get("/?token=valor") ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual "valor"
      }
    }

    "reject none" in {
      Get("/") ~> route ~> check {
        handled shouldEqual false
      }
    }
  }

}
