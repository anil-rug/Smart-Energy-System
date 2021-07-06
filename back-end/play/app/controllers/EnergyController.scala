package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import akka.stream.Materializer
import javax.inject.Inject
import models.EnergyConsumption
import models.JsonFormats._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection
import repositories.EnergyConsumptionStreamReader

import scala.concurrent.Future
// TODO: Cleanup class
class EnergyController @Inject()(val reactiveMongoApi: ReactiveMongoApi, cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc)
  with MongoController with ReactiveMongoComponents {

  def smartEnergyFuture = database.map(_.collection[JSONCollection]("EnergyConsumption"))

  // POST
  def testInsert() = Action.async {
    // implicit val format: OFormat[EnergyConsumption] = Json.format[EnergyConsumption]
    smartEnergyFuture.flatMap(_.insert(ordered = false).one(EnergyConsumption("123AA", "1000", "2000", "", ""))).map { wr: WriteResult =>
      if (wr.ok && wr.n == 1) {
        Ok("success")
      } else {
        Ok("fail")
      }
    }.recover { case t: Throwable =>
      Ok("error")
    }
  }

  def getEnergyConsumptionStream() = Action {
    val data = EnergyConsumptionStreamReader.getStream()
    Ok(if (data.nonEmpty) {
      data.foreach(x => {
        val ecData = Json.parse(x._2).as[EnergyConsumption]
        smartEnergyFuture.flatMap(_.insert(ordered = false).one(ecData))
      })
      data.head._2
    } else "EMPTY")
  }


  def energyConsumptionRT(id: Int) = Action {
    Ok(EnergyConsumptionStreamReader.getConsumptionById(id.toString))
  }

  def energyConsumptionWS(id: Int) = WebSocket.accept[String, String] {
    request => {
      ActorFlow.actorRef(
        out =>
          EnergyConsumptionWS.props(out, id.toString)
      )
    }
  }

  object EnergyConsumptionWS {
    def props(out: ActorRef, id: String) = Props(new EnergyConsumptionWS(out, id))
  }

  class EnergyConsumptionWS(out: ActorRef, id: String) extends Actor {
    override def receive: Receive = {
      case msg: String if msg.contains("close") =>
        self ! PoisonPill
      case msg: String =>
        out ! EnergyConsumptionStreamReader.getConsumptionById(id)
    }

    override def postStop() {
      println("Closing the websocket connection.")
    }
  }


  // GET


  def create = Action.async {
    val rec = EnergyConsumption("123AA", "1000", "2000", "1", "1")

    // insert the rec
    val futureResult = smartEnergyFuture.flatMap(_.insert.one(rec))

    // when the insert is performed, send a OK 200 result
    futureResult.map(_ => Ok)
  }


  def createFromJson = Action.async(parse.json) { request =>
    request.body.validate[EnergyConsumption].map { rec =>
      // `rec` is an instance of the case class `models.EnergyConsumption`
      smartEnergyFuture.flatMap(_.insert.one(rec)).map { lastError =>
        Created
      }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def findById(id: String) = Action {

    val query = BSONDocument("prosumerId" -> id)
    Ok(smartEnergyFuture.flatMap(_.find(query).one[BSONDocument]).toString)
  }

}

