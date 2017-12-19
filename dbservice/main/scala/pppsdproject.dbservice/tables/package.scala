package pppsdproject.dbservice

import pppsdproject.core.model._
import slick.jdbc.PostgresProfile.api._

package object tables {
  case class BoardTable(tag: Tag) extends Table[BoardDB](tag, "BOARD") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (id, name).mapTo[BoardDB]
  }

  lazy val boards = TableQuery[BoardTable]

  case class ListTable(tag: Tag) extends Table[ListDB](tag, "LIST") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def boardId = column[Int]("boardId")

    def * = (id, name, boardId).mapTo[ListDB]

    def board = foreignKey("board_fK", boardId, boards)(_.id, onDelete=ForeignKeyAction.NoAction, onUpdate=ForeignKeyAction.Cascade)
  }

  lazy val lists = TableQuery[ListTable]

  case class TaskTable(tag: Tag) extends Table[TaskDB](tag, "TASK") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def listId = column[Int]("listId")
    def description = column[Option[String]]("description")

    def * = (id, name, listId, description).mapTo[TaskDB]

    def list = foreignKey("list_fK", listId, lists)(_.id, onDelete=ForeignKeyAction.Restrict, onUpdate=ForeignKeyAction.Cascade)
  }

  lazy val tasks = TableQuery[TaskTable]
}
