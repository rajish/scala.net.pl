package controllers

import securesocial.controllers.{DefaultTemplatesPlugin, TemplatesPlugin}
import securesocial.core.{SecuredRequest, Identity}
import play.api.mvc.{Request, RequestHeader}
import play.api.templates.{Html, Txt}
import play.api.data.Form
import securesocial.controllers.PasswordChange.ChangeInfo
import securesocial.controllers.Registration.RegistrationInfo

class CustomTemplatesPlugin(application: play.api.Application) extends DefaultTemplatesPlugin(application) {

  override def getLoginPage[A](implicit request: Request[A], form: Form[(String, String)], msg: Option[String]): Html =
    views.html.auth.loginPage(form, msg)

  override def getSignUpPage[A](implicit request: Request[A], form: Form[RegistrationInfo], token: String): Html =
    views.html.auth.registration.signUpPage(form, token)

  override def getStartSignUpPage[A](implicit request: Request[A], form: Form[String]): Html =
    views.html.auth.registration.startSignUpPage(form)

  override def getResetPasswordPage[A](implicit request: Request[A], form: Form[(String, String)], token: String): Html =
    views.html.auth.registration.resetPasswordPage(form, token)

  override def getStartResetPasswordPage[A](implicit request: Request[A], form: Form[String]): Html =
    views.html.auth.registration.startSignUpPage(form)

//  def getPasswordChangePage[A](implicit request: SecuredRequest[A], form: Form[ChangeInfo]): Html = ???
//
//  def getNotAuthorizedPage[A](implicit request: Request[A]): Html = ???
//
//  def getSignUpEmail(token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = ???
//
//  def getAlreadyRegisteredEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = ???
//
//  def getWelcomeEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = ???
//
//  def getUnknownEmailNotice()(implicit request: RequestHeader): (Option[Txt], Option[Html]) = ???
//
//  def getSendPasswordResetEmail(user: Identity, token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = ???
//
//  def getPasswordChangedNoticeEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = ???
}
