package models

import play.api.libs.json.{Json, OFormat}

case class EnergyConsumption(_id: String, prosumerId: String, refrigeration: String = "", plugLoads: String, evCharge: String, others: String = "0") {
  /* Implicit writer[T] to handle the data type conversion during query operations */
  import reactivemongo.bson.Macros
  implicit val format: OFormat[EnergyConsumption] = Json.format[EnergyConsumption]
  implicit val userFormat = Macros.handler[EnergyConsumption]
}




