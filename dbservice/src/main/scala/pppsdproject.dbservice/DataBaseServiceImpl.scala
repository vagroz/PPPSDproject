import pppsdproject.core.exceptions._
import pppsdproject.core.model._
import pppsdproject.dbservice.DataBaseService
import pppsdproject.dbservice.tables._

import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration._

class DataBaseServiceImpl extends DataBaseService {
  //  надо еще обернуть все функции в try catch
  lazy val boards = TableQuery[BoardTable]
  lazy val lists = TableQuery[ListTable]
  lazy val tasks = TableQuery[TaskTable]

  val db = Database.forConfig("databaseAdmin")

  //  Как вариант - возвращать везде Future и пусть web сторона сама его обрабатывает. Но не сегодня
  //  При select должен возвращаться Seq
  def exec[T](action: DBIO[T]): T =
    Await.result(db.run(action), 2.seconds)

  override def getListByBoard(listName: String, boardName: String): ListDB = {
    val listSeq = exec(
      (for {
        l <- lists.filter(_.name === listName)
        b <- boards.filter(_.name === boardName) if l.boardId === b.id
      } yield l).result
    )
    if (listSeq.nonEmpty) {
      listSeq.head
    } else {
      throw ListNotFoundException("Error in getListByBoard: no such list in db", null)
    }
  }

  override def addTask(task: TaskDB): TaskDB = {
    val taskSeq = exec(
      (tasks ++= Seq(task)) andThen
        tasks.filter(t => t.name === task.name && t.listId === task.listId).sortBy(_.id.desc).result
    )
    if (taskSeq.nonEmpty) {
      taskSeq.head
    } else {
      throw TaskNotFoundException("Error in addTask: Something wrong with adding", null)
    }
  }

  override def deleteTask(taskId: Int): Unit = {
    val taskSeq = exec(tasks.filter(_.id === taskId).result
    )
    if (taskSeq.nonEmpty) {
      exec(tasks.filter(_.id === taskId).delete)
    } else {
      throw TaskNotFoundException("Error in deleteTask: No such task in db", null)
    }
  }

  override def getListsByBoard(boardName: String): Seq[ListDB] = {
    val listSeq = exec(
      (for {
        l <- lists
        b <- boards.filter(_.name === boardName) if l.boardId === b.id
      } yield l).result
    )
    if (listSeq.nonEmpty) {
      listSeq
    } else {
      throw ListNotFoundException("Error in deleteTask: No such list in db", null)
    }
  }

  override def getTasksByList(listName: String, boardName: String): Seq[TaskDB] = {
    //    val taskSeq = exec(
    //      sql"""
    //        select t.*
    //        from TASKS t
    //          inner join LISTS l on t.lisId = l.id
    //          inner join BOARDS b  on l.boardId = b.id
    //        where l.name = $listName and b.name = $boardName""".as[Seq[TaskDB]]
    val taskSeq = exec(
      (for {
        t <- tasks
        l <- lists if t.listId === l.id
        b <- boards if l.boardId === b.id
      } yield t).result
    )
    if (taskSeq.nonEmpty) {
      taskSeq
    } else {
      throw TaskNotFoundException("Error in deleteTask: No such task in db", null)
    }
  }

  override def getTaskById(taskId: Int): TaskDB = {
    val taskSeq = exec(tasks.filter(_.id === taskId).result)
    if (taskSeq.nonEmpty) {
      taskSeq.head
    } else {
      throw TaskNotFoundException("Error in deleteTask: No such list in db", null)
    }
  }

  override def moveTask(taskId: Int, listName: String): Unit = {
    //    Достаем listId
    val listIdSeq = exec(
      sql"""
        select l.id
        from TASKS t
          inner join LISTS l on t.lisId = l.id
          inner join BOARDS b on l.boardId = b.id
        where t.id = $taskId and l.name = $listName""".as[Int]
    )
    if (listIdSeq.nonEmpty) {
      exec(tasks.filter(_.id === taskId).map(_.listId).update(listIdSeq(0)))
    } else {
      throw ListNotFoundException("Error in deleteTask: No such list in db", null)
    }

  }

  def createEmptyTables() : Unit = {
    val schema = boards.schema ++ lists.schema ++ tasks.schema
    //    create tables
    exec(schema.create)
  }

  def addInitialQueries() : Unit = {
    val boardName = "InitialBoard"
    val initBoard = List(BoardDB(None, boardName))
    val boardId = exec(
      //      insert initBoard into boards
      (boards ++= initBoard)
        //      select * from boards where ...
        andThen boards.filter(_.name === boardName).map(_.id).result
    ).head
    val initLists = List(
      ListDB(None, "BackLog", boardId),
      ListDB(None, "In progress", boardId),
      ListDB(None, "Testing", boardId),
      ListDB(None, "Ready", boardId),
      ListDB(None, "Releaase 1.0", boardId)
    )
    exec(lists ++= initLists)
  }

  def dropAllTables() : Unit = {
    val schema = boards.schema ++ lists.schema ++ tasks.schema
    // delete tables
    exec(schema.drop)
  }
}