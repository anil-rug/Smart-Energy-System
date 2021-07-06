package utility

import models.JsonFormats._
import models.User
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UserTableUtils {

  def getUser(userName: String, usersCollection: Future[BSONCollection]): Future[Option[User]] = {
    usersCollection.flatMap(x =>
      x.find(BSONDocument("_id" -> genUniqueProsumerID(userName))).one[User]
    )
  }

  def getUserByUserName(userName: String, usersCollection: Future[BSONCollection]): Future[Option[User]] = {
    usersCollection.flatMap(table => {
      table.find(BSONDocument("userName" -> userName)).one[User]
    })
  }

  def addNewUser(userName: String, password: String, userCollection: Future[BSONCollection]): Future[WriteResult] = {
    val prosumerId = genUniqueProsumerID(userName)
    val user = User(prosumerId, prosumerId, userName, password)
    userCollection.flatMap(_.insert(ordered = false).one(user))
  }

  def removeUser(userName: String, userCollection: Future[BSONCollection]) = {
    val prosumerId = genUniqueProsumerID(userName)
    userCollection.flatMap(_.findAndRemove[BSONDocument](BSONDocument("_id" -> prosumerId)).map(_.result[User]))
  }

  def updateUser(userName: String, password: String, newUserName: String = "", newPassword: String = "",
                 userCollection: Future[BSONCollection]) = {
    val prosumerId = genUniqueProsumerID(userName)
    val user = User(prosumerId, prosumerId, if (newUserName.nonEmpty) newUserName else userName, if (newPassword.nonEmpty) newPassword else password)
    userCollection.flatMap(_.findAndUpdate(BSONDocument("_id" -> prosumerId), user).map(_.result[User]))
  }

  val genUniqueProsumerID: String => String = (userName: String) => {
    Math.abs(userName.hashCode).toString
  }


}
