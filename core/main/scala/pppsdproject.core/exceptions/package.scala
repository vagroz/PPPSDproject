package pppsdproject.core

package object exceptions {
  case class ListNotFoundException(message: String, cause: Throwable) extends Exception

}