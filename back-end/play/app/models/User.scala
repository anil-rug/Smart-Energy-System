package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.api.MongoDriver

case class User(_id: String, prosumerId: String, userName: String, password: String) extends MongoDriver {
  import reactivemongo.bson.Macros
  implicit val format: OFormat[User] = Json.format[User]
  implicit val energyConsFormat = Macros.handler[EnergyConsumption]
  implicit val writer = Macros.writer[User]
  implicit val reader = Macros.reader[User]
  implicit val jsonRead = Json.format[User]
}
