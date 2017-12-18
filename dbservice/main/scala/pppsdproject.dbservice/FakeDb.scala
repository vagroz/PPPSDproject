package pppsdproject.dbservice
import pppsdproject.core.model
import pppsdproject.core.exceptions._

class FakeDb
  extends DataBaseService {

  override def getListByBoard(listName: String, boardName: String): model.ListDB = {
    model.ListDB(Some(117), "testList", 117)
  }


  override def addTask(task: model.TaskDB): model.TaskDB = {
    if (task.name.toLowerCase() == "none")
      throw InternalError("Couldn't create task", null)
    task.copy(id = Some(1117))
  }

  override def deleteTask(taskId: Int): Unit = {
    println (s"tipa Task $taskId deleted ")
  }

  override def getListsByBoard(boardName: String): Seq[model.ListDB] = {
    if (boardName.toUpperCase() == "NONE")
      throw new BoardNotFoundException(s"Board with name=$boardName doesn't exist", null)
    Seq (
      model.ListDB(Some(117), "testList", 117),
      model.ListDB(Some(118), "testList1", 117)
    )
  }

  override def getTasksByList(listName: String, boardName: String): Seq[model.TaskDB] = {
    Seq (
      model.TaskDB(Some(1117), "task1", 117, None),
      model.TaskDB(Some(1118), "task2", 117, Some("Very important task")),
      model.TaskDB(Some(1119), "task3", 117, Some("Note important`"))
    )
  }

  override def getTaskById(taskId: Int): model.TaskDB = {
    if (taskId == 0)
      throw TaskNotFountException(s"Task with id=$taskId doesn't exist", null)
    model.TaskDB(Some(taskId), s"task$taskId", 117, None)
  }

  override def moveTask(taskId: Int, listName: String): Unit = {
    println (s"tipa Task $taskId moved in $listName")
  }
}
