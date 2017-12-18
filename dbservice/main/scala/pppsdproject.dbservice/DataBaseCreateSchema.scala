import pppsdproject.dbservice.tables._

import scala.concurrent.duration._
import scala.concurrent.Await

import slick.jdbc.PostgresProfile.api._

object DataBaseCreateSchema {
  lazy val boards = TableQuery[BoardTable]
  lazy val lists = TableQuery[ListTable]
  lazy val tasks = TableQuery[TaskTable]

  val db = Database.forConfig("databaseAdmin")

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
    val initBoard = Seq(BoardDB(boardName))
    val boardId = exec(
//      insert initBoard into boards
      boards ++= initBoard
//      select * from boards where ...
      andThen boards.filter(_.name === boardName).map(_.id).result
    )(0)
    val initLists = Seq(
      ListDB("BackLog", boardId),
      ListDB("In progress", boardId),
      ListDB("Testing", boardId),
      ListDB("Ready", boardId),
      ListDB("Releaase 1.0", boardId)
    )
    exec(lists ++= initLists)
  }

  def dropAllTables = {
    val schema = boards.schema ++ lists.schema ++ tasks.query
    //    create tables
    exec(schema.drop)
  }
}
