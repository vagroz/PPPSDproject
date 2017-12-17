package pppsdproject.webserver
import pppsdproject.dbservice._

abstract class WebService(dbs: DataBaseService)
  extends WebProtocol {
  def addTask(data: AddTaskRequest): Int

  def deleteTask(taskId: Int): Unit
}
