package pppsdproject.webserver

import org.scalatest.{ Matchers, FlatSpec }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._


class WebServerTest
  extends FlatSpec
    with Matchers
    with ScalatestRouteTest{

  val route = WebServer.getRoute()

  "The server" should "say hello" in {
    Get("/hello") ~> route ~> check {
      responseAs[String] shouldEqual "<h1>Hello, XXI Century World!!!</h1>"
    }
  }
  it should "delete and move task" in {
    Put("/task/56?list=list1") ~> route ~> check {
      status shouldBe StatusCodes.NoContent
    }

    Delete("/task/56") ~> route ~> check {
      status shouldBe StatusCodes.NoContent
    }
  }

}
