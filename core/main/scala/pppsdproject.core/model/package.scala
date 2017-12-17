package pppsdproject.core

package object model {
  case class TaskDB (id: Int, name: String, list_id: Int, description: Some[String])

  case class ListDB (id: Int, name: String, project_id: Int)

  case class ProjectDb (id: Int, name: String, description: Some[String])
}
