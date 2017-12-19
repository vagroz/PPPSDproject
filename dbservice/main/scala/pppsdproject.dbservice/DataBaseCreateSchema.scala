import pppsdproject.dbservice.tables._
import pppsdproject.core.model._

import scala.concurrent.duration._
import scala.concurrent.Await

import slick.jdbc.PostgresProfile.api._

class DataBaseCreateSchema {
  lazy val boards = TableQuery[BoardTable]
  lazy val lists = TableQuery[ListTable]
  lazy val tasks = TableQuery[TaskTable]

  val db = Database.forConfig("databaseTest")

  def exec[T](action: DBIO[T]): T =
    Await.result(db.run(action), 2.seconds)

  def createEmptyTables = {
    val schema = boards.schema ++ lists.schema ++ tasks.schema
//    create tables
    exec(schema.create)
  }

//  Заглушка: сразу создаем борду и списки в ней
  def addInitialQueries = {
    val boardName = "InitialBoard"
    val initBoard = List(BoardDB(None, boardName))
    val boardId = exec(
//      insert initBoard into boards
      (boards ++= initBoard)
//      select * from boards where ...
      andThen boards.filter(_.name === boardName).map(_.id).result
    )(0)
    val initLists = List(
      ListDB(None, "BackLog", boardId),
      ListDB(None, "In progress", boardId),
      ListDB(None, "Testing", boardId),
      ListDB(None, "Ready", boardId),
      ListDB(None, "Releaase 1.0", boardId)
    )
    exec(lists ++= initLists)
  }

  def dropAllTables = {
    val schema = boards.schema ++ lists.schema ++ tasks.schema
    //    create tables
    exec(schema.drop)
  }
}
