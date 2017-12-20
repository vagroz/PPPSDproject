package pppsdproject.dbservice

import pppsdproject.core.exceptions._
import pppsdproject.core.model._
import pppsdproject.dbservice.tables._

import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration._

class DataBaseServiceImpl extends DataBaseService {
//  def init(configuration: SomeConfigurationThingie) {
//    val driver = cfg.getOrElse("db.driver", "org.sqlite.JDBC")
//    val url    = cfg.getOrElse("db.url", "jdbc:sqlite:my.db")
//    val user   = cfg.getOrElse("db.user", "")
//    val pw     = cfg.getOrElse("db.password, "")
//    val db     = Database.forURL(url, driver=driver, user=user, password=pw)

  val db = Database.forURL("jdbc:sqlite:D:/sqlite/pppsdb.db", driver="org.sqlite.JDBC", user="", password="")

  //  Как вариант - возвращать везде Future и пусть web сторона сама его обрабатывает. Но не сегодня
  //  При select должен возвращаться Seq
  def exec[T](action: DBIO[T]): T =
    Await.result(db.run(action), 5.seconds)

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
    val newId = exec(tasks returning tasks.map(_.id) ++= Seq(task)).head
    exec(tasks.filter(_.id === newId).result).head
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
      throw ListNotFoundException("Error in getListsByBoard: No such list in db", null)
    }
  }

  override def getTasksByList(listName: String, boardName: String): Seq[TaskDB] = {
    val taskSeq = exec(
      (for {
        t <- tasks
        l <- lists.filter(_.name === listName) if t.listId === l.id
        b <- boards.filter(_.name === boardName) if l.boardId === b.id
      } yield t).result
    )
    if (taskSeq.nonEmpty) {
      taskSeq
    } else {
      throw TaskNotFoundException("Error in getTasksByList: No such task in db", null)
    }
  }

  override def getTaskById(taskId: Int): TaskDB = {
    val taskSeq = exec(tasks.filter(_.id === taskId).result)
    if (taskSeq.nonEmpty) {
      taskSeq.head
    } else {
      throw TaskNotFoundException("Error in getTaskbyId: No such list in db", null)
    }
  }

  override def moveTask(taskId: Int, listName: String): Unit = {
    val taskIdSeq = exec(tasks.filter(_.id === taskId).result)
    if (taskIdSeq.nonEmpty) {
      //Достаем boardId
      val boardIdSeq = exec(
        (for {
          l <- lists.filter(_.id === taskIdSeq.head.listId)
          b <- boards if l.boardId === b.id
        } yield b.id).result
      )
      //Достаем listId
      val listIdSeq = exec(
        (for {
          l <- lists.filter(_.name === listName)
          b <- boards.filter(_.id === boardIdSeq.head) if l.boardId === b.id
        } yield l.id).result
      )
      if (listIdSeq.nonEmpty) {
        exec(tasks.filter(_.id === taskId).map(_.listId).update(listIdSeq.head))
      } else {
        throw ListNotFoundException("Error in moveTask: No such list in db", null)
      }
    } else {
      throw TaskNotFoundException("Error in moveTask: No such task in db", null)
    }

  }

  def createEmptyTables() : Unit = {
    val schema = boards.schema ++ lists.schema ++ tasks.schema
    //    create tables
    exec(DBIO.seq(schema.create))
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
      ListDB(None, "Release 1.0", boardId)
    )
    exec(lists ++= initLists)
  }

  def dropAllTables() : Unit = {
    val schema = boards.schema ++ lists.schema ++ tasks.schema
    // delete tables
    exec(schema.drop)
  }
}