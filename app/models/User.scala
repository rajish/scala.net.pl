package models

import org.joda.time.DateTime
import reactivemongo.bson._

case class User(
  id: Option[BSONObjectID],
  username: String,
  password: String,
  email: String,
  githubUsername: String,
  avatarUrl: String,
  isAdmin: Boolean = false,
  isBlocked: Boolean = false,
  createdAt: Option[DateTime],
  updated: Option[DateTime]
)
