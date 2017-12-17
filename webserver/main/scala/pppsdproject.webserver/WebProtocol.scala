package pppsdproject.webserver
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import pppsdproject.core.model._

trait WebProtocol extends SprayJsonSupport{
  import DefaultJsonProtocol._

  object WebStatus {
    val Ok = "OK"
    val Error = "Error"
  }

  case class WebResponse[T](status: String, message: Option[String], payload: Option[T])

  case class IdRequest(id: Int)

  case class AddTaskRequest (task: TaskDescription, listName: String, boardName: String)

  case class MoveTaskRequest (taskId: Int, listName: String, boardName: String)

  case class TaskDescription (name: String, description: Option[String])

}