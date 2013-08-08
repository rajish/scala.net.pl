package controllers

import play.api.mvc._
import securesocial.core.{Identity, SecureSocial}
import play.api.Logger
import reactivemongo.api.MongoConnection
import play.modules.reactivemongo.MongoController
import models.auth.User

object Application extends Controller with SecureSocial {

  def index = Action {
    implicit request =>
      Logger.debug(securesocial.controllers.routes.LoginPage.logout.url)
      Logger.debug("Current identity = %s".format(SecureSocial.currentUser))
      val userBox = SecureSocial.currentUser match {
        case Some(user: User) => views.html.user.userBox(user)
        case _ => views.html.user.loginBox()
      }
      Ok(views.html.index("Your new application is ready.", userBox))
  }

  def secureTest = SecuredAction {
    implicit request =>
      Ok(views.html.securedPageExample())
  }

}
