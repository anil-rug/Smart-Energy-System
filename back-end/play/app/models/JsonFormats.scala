package models

import reactivemongo.bson.Macros

object JsonFormats {

  import play.api.libs.json.Json

  implicit val feedFormat = Json.format[EnergyConsumption]
  implicit val jsonRead = Json.format[User]
  implicit val userFormat = Macros.handler[User]
}