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
    * Удаляет задачу по id.
    * @param taskId id задачи
    */
  def deleteTask(taskId: Int): Unit

  /**
    * Получает все списки по названию доски
    * @param boardName Имя доски
    */
  def getListsByBoard(boardName: String): Seq[ListDB]

  /**
    * Получает все задачи по имени списка и доски
    * @param listName Имя списка
    * @param boardName Имя доски
    */
  def getTasksByList(listName: String, boardName: String): Seq[TaskDB]

  /**
    * Достаем информацию о задаче по id.
    * @param taskId id задачи
    */
  def getTaskById (taskId: Int): TaskDB

  /**
    * Перемещает задачу по id в другой список.
    * @param taskId id задачи
    * @param listName Имя списка, куда перемещать задачу
    */
  def moveTask (taskId: Int, listName: String): Unit

}
