package fr.gospeak.web.user

import fr.gospeak.core.domain.User
import fr.gospeak.web.user.UserCtrl._
import fr.gospeak.web.views.domain._
import fr.gospeak.web.{HomeCtrl, Values}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

class UserCtrl(cc: ControllerComponents) extends AbstractController(cc) {
  private val user = Values.user // logged user

  def index(): Action[AnyContent] = Action.async { implicit req: Request[AnyContent] =>
    for {
      groups <- Values.getGroups(user.id)
      talks <- Values.getTalks(user.id)
    } yield Ok(views.html.index(groups, talks)(indexHeader, breadcrumb(user.name)))
  }

  def profile(): Action[AnyContent] = Action { implicit req: Request[AnyContent] =>
    Ok(views.html.profile()(header.activeFor(routes.UserCtrl.profile()), breadcrumb(user.name).add("Profile" -> routes.UserCtrl.profile())))
  }
}

object UserCtrl {
  val userNav: Seq[NavLink] = Seq(
    NavLink("Groups", groups.routes.GroupCtrl.list()),
    NavLink("Talks", talks.routes.TalkCtrl.list()),
    NavLink("Profile", routes.UserCtrl.profile()))

  val indexHeader: HeaderInfo = HeaderInfo(
    brand = NavLink("Gospeak", fr.gospeak.web.routes.HomeCtrl.index()),
    links = NavDropdown("Public", HomeCtrl.publicNav) +: userNav,
    rightLinks = Seq(NavLink("logout", fr.gospeak.web.auth.routes.AuthCtrl.logout())))

  val header: HeaderInfo =
    indexHeader.copy(brand = NavLink("Gospeak", routes.UserCtrl.index()))

  def breadcrumb(user: User.Name) = Breadcrumb(Seq(
    BreadcrumbLink("Public", fr.gospeak.web.routes.HomeCtrl.index()),
    BreadcrumbLink(user.value, routes.UserCtrl.index())))
}
