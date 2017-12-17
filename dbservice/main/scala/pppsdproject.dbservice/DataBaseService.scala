package pppsdproject.dbservice

import pppsdproject.core.model._


trait DataBaseService {

  /**
    * Возвращает ListDB по его имени и названию доски.
    * В случае конфликта выбрасывает исключение.
    * @param listName Имя списка
    * @param boardName Имя доски
    */
  def getListByBoard(listName: String, boardName: String): ListDB

  /**
    * Добавляет задачу в базу данных. Возвращает ее же с заполненным id.
    * @param task TaskDb, id не учитывается
    */
  def addTask(task: TaskDB): TaskDB

  /**
    * Удаляяет задачу по id.
    * @param taskId id задачи
    */
  def deleteTask(taskId: Int): Unit


  def getListsByBoard(boardName: String): Seq[ListDB]

  def getTasksByList(listName: String, boardName: String): Seq[TaskDB]

  //нужно дополнительно:

  def getTaskById (taskId: Int): TaskDB

  def moveTask (taskId: Int, listName: String): Unit

}
