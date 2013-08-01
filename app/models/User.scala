package model

import play.api.Play.current
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._

case class User(
  id: ObjectId = new ObjectId,
  username: String,
  password: String,
  email: String,
  githubUsername: String,
  avatarUrl: String,
  isAdmin: Boolean = false,
  isBlocked: Boolean = false,
  createdAt: Option[Date] = None,
  updated: Option[Date] = None
)

object User extends ModelCompanion[User, ObjectId] {
  val dao = new SalatDAO[User, ObjectId](collection = mongoCollection("users")) {}

  def findOneByUsername(username: String): Option[User] = dao.findOne(MongoDBObject("username" -> username))
}
