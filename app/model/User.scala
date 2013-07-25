package model

case class User(name: String, email: String, githubUsername: String, 
    avatarUrl: String, isAdmin: Boolean = false, isBlocked: Boolean = false)
