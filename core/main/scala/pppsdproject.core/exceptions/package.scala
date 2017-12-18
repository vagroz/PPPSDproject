package pppsdproject.core

package object exceptions {
  case class ListNotFoundException(message: String, cause: Throwable) extends Exception

  case class BoardNotFoundException(message: String, cause: Throwable) extends Exception

  case class TaskNotFountException(message: String, cause: Throwable) extends Exception

  case class InternalError(message: String, cause: Throwable) extends Exception
}
