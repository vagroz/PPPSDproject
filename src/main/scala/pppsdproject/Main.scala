package pppsdproject

import pppsdproject.webserver._
import pppsdproject.dbservice.FakeDb

object Main extends App {
  if (args.length == 0) {
    println("Now database service is not available, use parameter '-t' to run server in test mode")
  }else if (args.toSet.contains("-t")){
    val webservice = new WebServiceImpl(new FakeDb)
    WebServer.runWithService(webservice)
  }
}
