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

  case class AddTaskRequest (task: TaskDescription, listName: String, boardName: String)

  case class MoveTaskRequest (taskId: Int, listName: String)

  case class TaskDescription (name: String, description: Option[String])


  implicit val MoveTaskRequestFormat = jsonFormat2(MoveTaskRequest)
  implicit val TaskDescriptionFormat = jsonFormat2(TaskDescription)
  implicit val EnvelopedTaskRequestFormat = jsonFormat3(WebResponse[AddTaskRequest])
  implicit val EnvelopedTaskDescription = jsonFormat3(WebResponse[TaskDescription])

}
