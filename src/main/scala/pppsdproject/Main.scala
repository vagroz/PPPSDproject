package pppsdproject

import pppsdproject.webserver._
import pppsdproject.dbservice._

object Main extends App {
  if (args.length == 0) {
    val dbService = new DataBaseServiceImpl
    dbService.createDb()
    val webService = new WebServiceImpl(dbService)
    WebServer.runWithService(webService)
  }else if (args.toSet.contains("-t")){
    println("Test mode: ON")
    val webService = new WebServiceImpl(new FakeDb)
    WebServer.runWithService(webService)
  }
}