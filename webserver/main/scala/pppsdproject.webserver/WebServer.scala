package pppsdproject.webserver

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.io.StdIn
import pppsdproject.dbservice.FakeDb
import pppsdproject.core.exceptions._

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
      case th @ (TaskNotFountException(_,_) | ListNotFoundException(_,_) | BoardNotFoundException(_,_) )=>
        complete (404, WebResponse[Int](WebStatus.Error, Some(th.getMessage), None))
      case th: InternalError =>
        complete (500, WebResponse[Int](WebStatus.Error, Some(th.getMessage), None))
      case th: Throwable =>
        complete (520, WebResponse[Int](WebStatus.Error, Some(th.getMessage), None))
    }

    val route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Hello, XXI Century World!!!</h1>"))
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
        } ~
        put {
          parameters('list.as[String]) { listName =>
            val moveReq = MoveTaskRequest(id, listName)
            val futureResult = Future(srv.moveTask(moveReq))
            onSuccess(futureResult){
              complete(HttpResponse(204))
            }

          }
        }
      } ~
      path("task") {
        get {
          parameters('list.as[String], 'board.as[String]) {(listName, boardName) =>
            val futureResult = Future(srv.getTasksOnListInBoard(listName, boardName))
              .map { result =>
                WebResponse(WebStatus.Ok, None, Some(result))
              }
            onComplete(futureResult){ result =>
              complete(result)
            }
          }
        } ~
        post {
          entity(as[AddTaskRequest]) { inputData =>
            val futureResult = Future(srv.addTask(inputData))
              .map { res =>
                WebResponse(WebStatus.Ok, None, Some(res))
              }
            onComplete(futureResult){ result =>
              complete(201, result)
            }
          }
        }
      } ~
      path ("list") {
        parameters('board.as[String]){ board =>
          val futureResult = Future(srv.getListsOnBoard(board))
            .map { res =>
              WebResponse(WebStatus.Ok, None, Some(res))
            }
          onComplete(futureResult){ result =>
            complete(result)
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}