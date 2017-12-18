package pppsdproject.webserver

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.io.StdIn
import pppsdproject.dbservice.FakeDb

import scala.concurrent.Future
import scala.util.{Failure, Success}

object WebServer
  extends Directives
    with WebProtocol {

  def main(args: Array[String]): Unit = {
    val webservice = new WebServiceImpl(new FakeDb)
    runWithService(webservice)
  }

  def runWithService (srv: WebService) {
    implicit val system = ActorSystem("pppsdproject")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    implicit def myExceptionHandler: ExceptionHandler = ExceptionHandler {
      case th: Throwable =>
        complete (422, WebResponse[Int](WebStatus.Error, Some(th.getMessage), None))
    }

    val route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      } ~
      path("task" / IntNumber) { id =>
        get {
          val futuResult = Future(srv.getTaskById(id))
            .map (task => WebResponse(WebStatus.Ok, None, Some(task)))
          onComplete(futuResult){ result =>
              complete(result)
          }
        } ~
        delete {
          val futuResult = Future(srv.deleteTask(id))
          onSuccess(futuResult){
              complete(HttpResponse(204))
          }
        }
      } ~
      path("task") {
        get {
          entity(as[GetTasksInListRequest]) { inputData =>
            val futureResult = Future(srv.getTasksOnListInBoard(inputData.listName, inputData.boardName))
              .map { result =>
                WebResponse(WebStatus.Ok, None, Some(result))
              }
            onComplete(futureResult){ result =>
              complete(result)
            }
          }
        }
      }

    def failureComplete(th: Throwable) = {
      WebResponse[Int](WebStatus.Error, Some(th.getMessage), None)
    }
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}