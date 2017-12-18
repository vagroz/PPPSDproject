package pppsdproject.dbservice

import pppsdproject.core.model._
import pppsdproject.dbservice.tables._

import scala.concurrent.Await


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

  //нужно дополнительно:

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

class DataBaseServiceImpl extends DataBaseService {
//  надо еще обернуть все функции в try catch
  lazy val boards = TableQuery[BoardTable]
  lazy val lists = TableQuery[ListTable]
  lazy val tasks = TableQuery[TaskTable]

  val db = Database.forConfig("databaseAdmin")

//  Как вариант - возвращать везде Future и пусть web сторона сама его обрабатывает. Но не сегодня
//  При select должен возвращаться Seq
  def exec[T](action: DBIO[T]): T =
    Await.result(db.run(action), 2.seconds)

  override def getListByBoard(listName: String, boardName: String): ListDB = {
    val listSeq = exec(
      for {
        (l, b) <- lists.filter(_.name === listName) join boards.filter(_.name === boardName)
          on (_.boardId = _.id)
      } yield (l)
    )
    if (listSeq.size > 0) {
      return listSeq(0)
    } else {
      throw new NotFoundInDatabase("Error in getListByBoard: no such list in db")
    }
  }

  override def addTask(task: TaskDB): TaskDB = {
    val taskSeq = exec(
      tasks ++= task
      andThen tasks.filter(t => t.name === task.name && t.listId === task.listId).sortBy(_.id.desc)
    )
    if (taskSeq.size > 0) {
      return taskSeq(0)
    } else {
      throw new NotFoundInDatabase("Error in addTask: Something wrong with adding")
    }
  }

  override def deleteTask(taskId: Int): Unit = {
    let taskSeq = exec(tasks.filter(_.id === taskId).result
    )
    if (taskSeq.size > 0) {
      exec(tasks.filter(_.id === taskId).delete)
    } else {
      throw new NotFoundInDatabase("Error in deleteTask: No such task in db")
    }
  }

  override def getListsByBoard(boardName: String): Seq[ListDB] = {
    return exec(
      for {
        (l, b) <- lists.filter(_.name === listName) join boards.filter(_.name === boardName)
        on (_.boardId = _.id)
      } yield (l)
    )
  }

  override def getTasksByList(listName: String, boardName: String): Seq[TaskDB] = {
    return exec(
      sql"""
        select t.*
        from TASKS t
          inner join LISTS l on t.lisId = l.id
          inner join BOARDS b  on l.boardId = b.id
        where l.name = $listName and b.name = $boardName""".as[TaskDB]
    )
  }

  override def getTaskById (taskId: Int): TaskDB = {
    return exec(tasks.filter(_.id === taskId))
  }

  override def moveTask (taskId: Int, listName: String): Unit = {
//    Достаем listId
    val listIdSeq = exec(
      sql"""
        select l.id
        from TASKS t
          inner join LISTS l on t.lisId = l.id
          inner join BOARDS b on l.boardId = b.id
        where t.id = $taskId and l.name = $listName""".as[Int]
    )
    if (listIdSeq.size > 0) {
      exec(tasks.filter(_.id === taskId).map(_.listId).update(listIdSeq(0)))
    } else {
      throw new NotFoundInDatabase("Error in deleteTask: No such task in db")
    }
  }
}
