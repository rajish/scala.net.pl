package service.auth

import securesocial.core._

import play.api.{Logger, Application}

import securesocial.core.providers.Token
import securesocial.core.UserId
import scala.concurrent.{Future, Await}
import models.auth.{TokenDao, User}
import reactivemongo.bson.BSONObjectID
import akka.util.Timeout

class UserService(application: Application) extends UserServicePlugin(application) {

  import scala.concurrent.ExecutionContext.Implicits.global

  import scala.concurrent.duration._

  val defaultTimeout = Timeout(10 seconds).duration

  private def waitForRes[A](awaiting: => Future[A], timeout: Duration = defaultTimeout): A = {
    Await.result(awaiting, timeout)
  }

  def find(secureSocialId: UserId): Option[Identity] = {
    val futResult = User.find(secureSocialId)
    waitForRes(futResult)
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    val futResult = User.findByEmailAndProvider(email, providerId)
    waitForRes(futResult)
  }

  /**
   * Saves the user. This method gets called when a user logs in.
   * @param identity
   * @return identity
   */
  def save(identity: Identity): Identity = {
    val existingUser = User.find(identity.id)
    val savedUser = existingUser flatMap ({
      case Some(user) => {
        User.update(user)
      }
      case None => {
        val newlyCreatedUser = User(
          Some(BSONObjectID.generate),
          identity.id,
          identity.firstName,
          identity.lastName,
          identity.fullName,
          identity.email,
          identity.avatarUrl,
          identity.authMethod,
          identity.oAuth1Info,
          identity.oAuth2Info,
          identity.passwordInfo)
        User.insert(newlyCreatedUser)
      }
    }) map (_ => {
      if (Logger.isDebugEnabled) {
        Logger.debug("Successfully saved user: %s".format(identity.id))
      }
      waitForRes(User.find(identity.id))
    })
    waitForRes(savedUser).get
  }

  def save(token: Token) {
    TokenDao.save(token)
  }

  def findToken(tokenUuid: String): Option[Token] = {
    val futResult = TokenDao.find(tokenUuid)
    waitForRes(futResult)
  }

  def deleteToken(uuid: String) {
    TokenDao.delete(uuid)
  }

  def deleteExpiredTokens() {
    TokenDao.deleteExpired()
  }

}
