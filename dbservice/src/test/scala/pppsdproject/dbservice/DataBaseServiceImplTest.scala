package pppsdproject.dbservice

import java.io.File

import org.scalatest.{FlatSpec, Matchers}
import pppsdproject.core.exceptions._
import pppsdproject.core.model._

class DataBaseServiceImplTest extends FlatSpec
    with Matchers{

  val dbs = new DataBaseServiceImpl
  val dbFile = new File("/pppsdb")
  if (!dbFile.exists) {
    dbs.createEmptyTables()
  } else {
    dbs.clearTables()
  }
  dbs.addInitialQueries()

  "DataBaseServiceImpl" should "add tasks to existing list" in {
    dbs.addTask(TaskDB(None, "New", 1, None)) shouldBe TaskDB(Some(1), "New", 1, None)
  }

  it should "throw exception on non-existing list" in {
    an [ListNotFoundException] should be thrownBy dbs.addTask(TaskDB(None, "New 2", 6, None))
  }

  it should "move task" in {
    dbs.moveTask(1, "Testing") shouldBe ()
  }

  it should "throw exception on move task" in {
    an [TaskNotFoundException] should be thrownBy dbs.moveTask(2, "Testing")
    an [ListNotFoundException] should be thrownBy dbs.moveTask(1, "Not exist")
  }

  it should "return all lists from board" in {
    dbs.getListsByBoard("InitialBoard") shouldBe Seq[ListDB](
      ListDB(Some(1), "BackLog", 1),
      ListDB(Some(2), "In progress", 1),
      ListDB(Some(3), "Testing", 1),
      ListDB(Some(4), "Ready", 1),
      ListDB(Some(5), "Release 1.0", 1)
    )
  }

  it should "throw an exception on getting from non-existing board" in {
    an [BoardNotFoundException] should be thrownBy dbs.getListByBoard("Testing", "Not exist")
    an [BoardNotFoundException] should be thrownBy dbs.getListsByBoard("Not exist")
    an [BoardNotFoundException] should be thrownBy dbs.getTasksByList("Testing", "Not exist")
  }

  it should "return list 'Testing' by name" in {
    dbs.getListByBoard("Testing", "InitialBoard") shouldBe ListDB(Some(3), "Testing", 1)
  }

  it should "return tasks from list" in {
    dbs.getTasksByList("Testing", "InitialBoard") shouldBe Seq[TaskDB](TaskDB(Some(1), "New", 3, None))
  }

  it should "throw an exception if list is empty" in {
    an [TaskNotFoundException] should be thrownBy dbs.getTasksByList("BackLog", "InitialBoard")
  }

  it should "throw an exception if list doesn't exist in current board" in {
    an [ListNotFoundException] should be thrownBy dbs.getTasksByList("None", "InitialBoard")
  }

}
