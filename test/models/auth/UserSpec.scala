package models.auth

import org.specs2.mutable.Specification
import securesocial.core._
import securesocial.core.UserId
import reactivemongo.bson.BSONObjectID

class UserSpec extends Specification {

  lazy val user = User(
    _id = Some(BSONObjectID.generate),
    id = UserId("someId", "someProvider"),
    firstName = "someFirstName",
    lastName = "someLastName",
    fullName = "someFullName",
    email = Some("email@example.com"),
    avatarUrl = Some("http://some.resource.com/avatar/path"),
    authMethod = AuthenticationMethod.UserPassword,
    oAuth1Info = None,
    oAuth2Info = None,
    passwordInfo = Some(PasswordInfo("someHasher", "someHashedPass", Some("someSalt"))),
    role = "user",
    isBlocked = false
  )

  "User's jsonFormat" should {

    "be transparent" in {
      User.jsonFormat.reads(User.jsonFormat.writes(user)).get must_== user
    }

  }

}
