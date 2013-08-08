package models.auth

import reactivemongo.bson._
import securesocial.core._
import play.api.libs.json._
import securesocial.core.UserId
import securesocial.core.OAuth2Info
import securesocial.core.OAuth1Info
import securesocial.core.PasswordInfo
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.Cursor
import scala.concurrent.{Future, ExecutionContext}
import securesocial.core.providers.Token

import ExecutionContext.Implicits.global
import play.api.Play.current
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.core.commands.LastError
import play.api.Logger
import org.joda.time.DateTime

/**
 * User entity
 *
 * @param _id db id
 * @param id secureSocial identity identifier
 * @param firstName
 * @param lastName
 * @param fullName
 * @param email
 * @param avatarUrl
 * @param authMethod
 * @param oAuth1Info
 * @param oAuth2Info
 * @param passwordInfo
 * @param role
 * @param isBlocked
 */
case class User(
                 _id: Option[BSONObjectID],
                 id: UserId,
                 firstName: String,
                 lastName: String,
                 fullName: String,
                 email: Option[String],
                 avatarUrl: Option[String],
                 authMethod: AuthenticationMethod,
                 oAuth1Info: Option[OAuth1Info],
                 oAuth2Info: Option[OAuth2Info],
                 passwordInfo: Option[PasswordInfo],

                 // TODO introduce Role trait and concrete role objects
                 role: String = "identity",
                 isBlocked: Boolean = false,
                 createdAt: Option[DateTime] = None,
                 lastActivity: Option[DateTime] = None
                 ) extends Identity

object User {

  implicit val jsonFormat = new Format[User] {

    val bsonObjectIdFormat = BSONObjectIDFormat
    val userIdFormat = Json.format[UserId]
    val authMethodFormat = Json.format[AuthenticationMethod]
    val oAuth1InfoFormat = Json.format[OAuth1Info]
    val oAuth2InfoFormat = Json.format[OAuth2Info]
    val passwordInfoFormat = Json.format[PasswordInfo]

    def reads(json: JsValue): JsResult[User] =
      JsSuccess(
        User(
          bsonObjectIdFormat.reads(json \ "_id").asOpt,
          userIdFormat.reads(json \ "id").get,
          (json \ "firstName").as[String],
          (json \ "lastName").as[String],
          (json \ "fullName").as[String],
          (json \ "email").asOpt[String],
          (json \ "avatarUrl").asOpt[String],
          authMethodFormat.reads(json \ "authMethod").get,
          oAuth1InfoFormat.reads(json \ "oAuth1Info").asOpt,
          oAuth2InfoFormat.reads(json \ "oAuth2Info").asOpt,
          passwordInfoFormat.reads(json \ "passwordInfo").asOpt,
          (json \ "role").asOpt[String].getOrElse("identity"),
          (json \ "isBlocked").asOpt[Boolean].getOrElse(false),
          (json \ "createdAt").asOpt[DateTime],
          (json \ "lastActivity").asOpt[DateTime]
        )
      )

    def writes(u: User): JsValue =
      Json.obj(
        "_id" -> u._id.map(bsonObjectIdFormat.writes),
        "id" -> userIdFormat.writes(u.id),
        "firstName" -> u.firstName,
        "lastName" -> u.lastName,
        "fullName" -> u.fullName,
        "email" -> u.email,
        "avatarUrl" -> u.avatarUrl,
        "authMethod" -> authMethodFormat.writes(u.authMethod),
        "oAuth1Info" -> u.oAuth1Info.map(oAuth1InfoFormat.writes),
        "oAuth2Info" -> u.oAuth2Info.map(oAuth2InfoFormat.writes),
        "passwordInfo" -> u.passwordInfo.map(passwordInfoFormat.writes),
        "role" -> u.role,
        "isBlocked" -> u.isBlocked,
        "createdAt" -> u.createdAt,
        "lastActivity" -> u.lastActivity
      )
  }


  private def usersCollection: JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection]("users")

  /**
   * Find a identity by UserId
   *
   * @param id secureSocial identity identifier (be aware that this id IS NOT the mongodb object id!)
   * @return matching User object or None if not found
   */
  def find(id: UserId): Future[Option[User]] = {
    val query = Json.obj(
      "id" -> Json.obj(
        "id" -> id.id,
        "providerId" -> id.providerId
      )
    )
    val cursor: Cursor[User] = usersCollection.find(query).cursor[User]
    cursor.toList().map(_.headOption)
  }

  /**
   * Find a identity by email and provider
   *
   * @param email identity email
   * @param providerId provider id
   * @return matching User or None if not found
   */
  def findByEmailAndProvider(email: String, providerId: String): Future[Option[User]] = {
    val query = Json.obj("email" -> email, "id" -> Json.obj("providerId" -> providerId))
    val cursor = usersCollection.find(query).cursor[User]
    cursor.toList().map(_.headOption)
  }

  /**
   * Save given User
   *
   * @param user user object to be persisted
   * @return future containing last error
   */
  def insert(user: User): Future[LastError] = {
    if (Logger.isDebugEnabled) {
      Logger.debug("inserting %s".format(user))
    }
    val currentTime = new DateTime()
    val userWithDefaults = user.copy(
      _id = Some(BSONObjectID.generate),
      createdAt = Some(currentTime),
      lastActivity = Some(currentTime))
    usersCollection.save(userWithDefaults)
  }

  /**
   * Update given user
   *
   * @param user user object to be updated
   * @return future containing last error
   */
  def update(user: User): Future[LastError] = {
    if (Logger.isDebugEnabled) {
      Logger.debug("updating %s".format(user))
    }
    val selector = Json.obj("id" -> Json.format[UserId].writes(user.id))
    val userWithUpdatedLastActivity = user.copy(
      lastActivity = Some(new DateTime))
    val update = Json.obj("$set" -> (
      (jsonFormat.writes(userWithUpdatedLastActivity): @unchecked) match {
        case obj: JsObject => obj - "_id"
      }
    ))
    usersCollection.update(selector, update)
  }
}

object TokenDao {
  implicit val jsonFormat = Json.format[Token]

  def tokensCollection: JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection]("tokens")


  def save(token: Token) {
    tokensCollection.insert(token)
  }

  def find(uuid: String): Future[Option[Token]] = {
    val criteria = Json.obj("uuid" -> uuid)
    val cursor = tokensCollection.find(criteria).cursor[Token]
    cursor.toList().map(e => e.headOption)
  }

  def delete(uuid: String) {
    val criteria = Json.obj("uuid" -> uuid)
    tokensCollection.remove(criteria)
  }

  def deleteExpired() {
    val criteria = Json.obj("isExpired" -> true)
    tokensCollection.remove(criteria)
  }
}
