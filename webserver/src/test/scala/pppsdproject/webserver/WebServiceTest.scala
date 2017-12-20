package pppsdproject.webserver

import org.scalatest.{FlatSpec, Matchers}
import pppsdproject.core.exceptions._
import pppsdproject.core.model._
import pppsdproject.dbservice.FakeDb

class WebServiceTest
  extends FlatSpec
    with Matchers{

  val wbs = new WebServiceImpl(new FakeDb)

  "Webservice" should "add and delete and move task" in {
    wbs.deleteTask(1000) shouldBe ()
    val request = AddTaskRequest(TaskDescription("Task1", Some("Very important task")), "list1", "board1")
    wbs.addTask(request) shouldBe 1117
    wbs.moveTask(MoveTaskRequest(1117, "list2")) shouldBe ()
  }

  it should "throw exception" in {
    val request = AddTaskRequest(TaskDescription("none", Some("Very important task")), "list1", "board1")
    an [InternalError] should be thrownBy wbs.addTask(request)
  }

  it should "return lists on board" in {
    wbs.getListsOnBoard("board1") should contain("testList")
  }

  it should "throw BoardNotFoundException" in {
    val exception = the [BoardNotFoundException] thrownBy wbs.getListsOnBoard("NoNe")
    exception.getMessage shouldBe "Board with name=NoNe doesn't exist"
  }

  it should "return task id by list and board" in {
    wbs.getTasksOnListInBoard("list1", "board1") should contain only(1117, 1118, 1119)
  }

  it should "return existing task by Id" in {
    wbs.getTaskById(1199).id shouldBe Some(1199)
    a [TaskNotFoundException] should be thrownBy wbs.getTaskById(0)
  }


}
