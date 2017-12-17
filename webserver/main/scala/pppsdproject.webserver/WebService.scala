package pppsdproject.webserver
import pppsdproject.core.model._
import pppsdproject.dbservice._

abstract class WebService
  extends WebProtocol {
  /**
    * Добавляет задачу по описанию, имени листа и борды
    * @param data request
    * @return id добавленной задачи
    */
  def addTask(data: AddTaskRequest): Int

  def deleteTask(taskId: Int): Unit

  def moveTask(data: MoveTaskRequest): Int
}

class WebServiceImpl (dbs: DataBaseService)
  extends WebService {

  override def addTask(data: AddTaskRequest): Int = {
    val listId = dbs.getListByBoard(data.listName, data.boardName).id
    val task = TaskDB(None, data.task.name, listId, data.task.description)
    val addedTask = dbs.addTask(task)
    addedTask.id.get
  }

  override def deleteTask(taskId: Int): Unit = {

  }

  override def moveTask(data: MoveTaskRequest): Int = ???
}
