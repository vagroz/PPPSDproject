package pppsdproject.webserver

import org.scalatest.{ Matchers, FlatSpec }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import pppsdproject.core.model.TaskDB


class WebServerTest
  extends FlatSpec
    with Matchers
    with ScalatestRouteTest
    with WebProtocol{

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

  it should "return task id's in list" in {
    Get("/task?list=list1&board=board1") ~> route ~> check {
      entityAs[WebResponse[Seq[Int]]].payload.get should contain only(1117, 1118, 1119)
    }
  }

  it should "add task and return its id" in {
    val request = AddTaskRequest(TaskDescription("Task1", Some("Very important task")), "list1", "board1")
    Post("/task", request) ~> route ~> check {
      entityAs[WebResponse[Int]].payload.get shouldBe 1117
      status shouldBe StatusCodes.Created
    }
  }

  it should "return task by Id" in {
    val id = 56789
    Get(s"/task/$id") ~> route ~> check {
      entityAs[WebResponse[TaskDB]].payload.get.id shouldBe Some(id)
    }
    Get("/task/0") ~> Route.seal(route) ~> check {
      status shouldBe StatusCodes.NotFound
    }
  }

}
