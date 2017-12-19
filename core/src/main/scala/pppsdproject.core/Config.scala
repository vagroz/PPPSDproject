package pppsdproject.core
import com.typesafe.config.ConfigFactory

object Config {
  val conf = ConfigFactory.load()

  case class WebserviceConfig(endpoint: String, port: Int)

  def loadWebserviceConfig() = {
    WebserviceConfig(
      conf.getString("webservice.endpoint"),
      conf.getInt("webservice.port")
    )
  }
}
