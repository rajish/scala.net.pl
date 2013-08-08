package test

import org.specs2.mutable._
import org.specs2.specification.{ Scope, Around}
import org.specs2.execute._

import play.api._, mvc._
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification {

  "Application" should {

    "render the index page" in  new FakeApp{
      running(FakeApplication()) {
        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
      }
    }

  }
}

trait FakeApp extends WithApplication {
  lazy val home = route(FakeRequest(GET, "/")).get

  def setup: Unit = {
    Logger.debug("----- new test -------")
    // can be some database setup here, etc.
  }

  override def around[T : AsResult](t: => T) = super.around {
    setup
    t
  }
}
