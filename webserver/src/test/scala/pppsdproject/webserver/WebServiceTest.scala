package pppsdproject.webserver

import org.scalatest.{FlatSpec, Matchers}
import pppsdproject.core.exceptions._
import pppsdproject.core.model._
import Matchers._
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





}
