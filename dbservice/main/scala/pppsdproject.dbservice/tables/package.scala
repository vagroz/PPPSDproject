package pppsdproject.dbservice

import pppsdproject.core.model._
import slick.jdbc.PostgresProfile.api._

package object tables {
  case class BoardTable(tag: Tag) extends TableBoardDB](tag, "BOARD") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (id, name, listId, description).mapTo[TaskDB]
  }

  lazy val boards = TableQuery[BoardTable]

  case class ListTable(tag: Tag) extends Table[ListDB](tag, "LIST") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def boardId = column[Int]("boardId")

    def * = (id, name, listId, description).mapTo[TaskDB]

    def board = foreignKey("board_fK", boardId, boards)(._id, onDelete=ForeignKeyAction.NoAction, onUpdate=ForeignKeyAction.Cascade)
  }

  lazy val lists = TableQuery[ListTable]

  case class TaskTable(tag: Tag) extends Table[TaskDB](tag, "TASK") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def listId = column[Int]("listId")
    def description = column[Option[String]]("description")

    def * = (id, name, listId, description).mapTo[TaskDB]

    def list = foreignKey("list_fK", listId, lists)(._id, onDelete=ForeignKeyAction.Restrict, onUpdate=ForeignKeyAction.Cascade)
  }

  lazy val tasks = TableQuery[TaskTable]
}
