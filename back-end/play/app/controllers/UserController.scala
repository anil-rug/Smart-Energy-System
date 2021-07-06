package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import javax.inject.Inject
import models.User
import play.api.mvc.{AbstractController, ControllerComponents}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.collections.bson.BSONCollection
import utility.UserTableUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class UserController @Inject()(val reactiveMongoApi: ReactiveMongoApi,
                               cc: ControllerComponents)
                              (implicit system: ActorSystem, materializer: Materializer) extends AbstractController(cc)
  with MongoController
  with ReactiveMongoComponents {

  implicit def m: Materializer = materializer

  def usersCollection: Future[BSONCollection] = database map (_.collection("Users"))

  import reactivemongo.bson.Macros

  implicit val userFormat = Macros.handler[User]


  def authenticateLogin(userName: String, password: String) = Action.async {
    UserTableUtils.getUser(userName, usersCollection).map(user => {
      user.map(u => {
        if (userName == u.userName && password == u.password) Ok("200") else NotFound("401")
      }).getOrElse(NotFound)
    })
  }

  def createNewUser(userName: String, password: String) = Action.async(
    UserTableUtils.getUserByUserName(userName, usersCollection).flatMap(user => {
      if (user.isEmpty)
        UserTableUtils.addNewUser(userName, password, usersCollection).map(_ => Created(s"UserName: $userName successfully Created"))
      else Future.successful(BadRequest(s"UserName $userName already in use"))
    })
  )

  def deleteUser(userName: String) = Action.async {
    UserTableUtils.removeUser(userName, usersCollection).map {
      case Some(user) => Ok(s"User: ${user.userName} Deleted")
      case _ => NotFound(s"Failed to Delete the User: ${userName}")
    }
  }

  // TODO: Fix updateUser ID calculation logic
  def updateUser(userName: String, password: String, newUserName: String = "", newPassword: String = "") = Action.async {
    UserTableUtils.updateUser(userName, password, newUserName, newPassword, usersCollection).map{
      case Some(user) => Ok(s"user: ${user.userName} Updated!")
      case _ => NotFound(s"Failed to Update the user: ${userName}")
    }
  }
}

