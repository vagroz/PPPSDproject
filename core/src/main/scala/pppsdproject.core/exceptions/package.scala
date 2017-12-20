package pppsdproject.core

package object exceptions {
  case class ListNotFoundException(message: String, cause: Throwable) extends Exception(message, cause)

  case class BoardNotFoundException(message: String, cause: Throwable) extends Exception(message, cause)

  case class TaskNotFoundException(message: String, cause: Throwable) extends Exception(message, cause)

  case class InternalError(message: String, cause: Throwable) extends Exception(message, cause)
}
