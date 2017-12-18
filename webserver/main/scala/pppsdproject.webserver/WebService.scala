package pppsdproject.webserver
import pppsdproject.core.model._
import pppsdproject.dbservice._
import pppsdproject.webserver._

abstract class WebService
  extends WebProtocol {
  /**
    * Добавляет задачу по описанию, имени листа и борды
    * @param data request
    * @return id добавленной задачи
    */
  def addTask(data: AddTaskRequest): Int

  def deleteTask(taskId: Int): Unit

  /**
    * Перемещает задачу внутри борды
    * @param data request
    */
  def moveTask(data: MoveTaskRequest): Unit

  def getListsOnBoard(boardName: String): Seq[String]

  def getTasksOnListInBoard(listName: String, boardName: String): Seq[Int]

  def getTaskById(taskId: Int): TaskDB
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
    dbs.deleteTask(taskId)
  }

  override def moveTask(data: MoveTaskRequest): Unit = {
    dbs.moveTask(data.taskId, data.listName)
  }

  override def getListsOnBoard(boardName: String): Seq[String] = {
    val lists = dbs.getListsByBoard(boardName)
    lists.map(x => x.name)
  }

  override def getTasksOnListInBoard(listName: String, boardName: String): Seq[Int] = {
    val tasks = dbs.getTasksByList(listName, boardName)
    tasks.map(x => x.id.get)
  }

  override def getTaskById(taskId: Int) = {
    dbs.getTaskById(taskId)
  }
}
