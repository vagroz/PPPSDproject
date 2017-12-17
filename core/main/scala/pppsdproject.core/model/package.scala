package pppsdproject.core

package object model {
  case class TaskDB (id: Option[Int], name: String, listId: Int, description: Option[String])

  case class ListDB (id: Int, name: String, boardId: Int)

  case class BoardDB (id: Int, name: String)
}
