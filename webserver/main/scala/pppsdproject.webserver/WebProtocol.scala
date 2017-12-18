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

  case class GetTasksInListRequest (listName: String, boardName: String)

  case class TaskDescription (name: String, description: Option[String])


  implicit val TaskDBFormat = jsonFormat4(TaskDB)
  implicit val MoveTaskRequestFormat = jsonFormat2(MoveTaskRequest)
  implicit val TaskDescriptionFormat = jsonFormat2(TaskDescription)
  implicit val AddTaskRequestFormat = jsonFormat3(AddTaskRequest)
  implicit val getTasksInListRequestFormat = jsonFormat2(GetTasksInListRequest)
  implicit val EnvelopedTaskRequestFormat = jsonFormat3(WebResponse[AddTaskRequest])
  implicit val EnvelopedTaskDescription = jsonFormat3(WebResponse[TaskDescription])
  implicit val EnvelopedError = jsonFormat3(WebResponse[Int])
  implicit val EnvelopedTaskDBFormat = jsonFormat3(WebResponse[TaskDB])
  implicit val EnvelopedSeqInt = jsonFormat3(WebResponse[Seq[Int]])

}
