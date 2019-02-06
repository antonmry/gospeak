package fr.gospeak.web.user.groups.proposals.speakers

import cats.data.OptionT
import fr.gospeak.core.domain.{Group, Proposal, Talk, User}
import fr.gospeak.core.services.GospeakDb
import fr.gospeak.web.auth.AuthService
import fr.gospeak.web.domain.{Breadcrumb, HeaderInfo}
import fr.gospeak.web.user.groups.proposals.ProposalCtrl
import fr.gospeak.web.user.groups.proposals.routes.{ProposalCtrl => ProposalRoutes}
import fr.gospeak.web.user.groups.proposals.speakers.SpeakerCtrl._
import fr.gospeak.web.utils.UICtrl
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}

class SpeakerCtrl(cc: ControllerComponents, db: GospeakDb, auth: AuthService) extends UICtrl(cc) {
  def detail(group: Group.Slug, proposal: Proposal.Id, speaker: User.Slug): Action[AnyContent] = Action.async { implicit req: Request[AnyContent] =>
    implicit val user: User = auth.authed()
    (for {
      groupElt <- OptionT(db.getGroup(user.id, group))
      proposalElt <- OptionT(db.getProposal(proposal))
      speakerElt <- OptionT(db.getUser(speaker))
      h = header(group)
      b = breadcrumb(user.name, group -> groupElt.name, proposal -> proposalElt.title, speaker -> speakerElt.name)
    } yield Ok(html.detail(speakerElt)(h, b))).value.map(_.getOrElse(proposalNotFound(group, proposal))).unsafeToFuture()
  }
}

object SpeakerCtrl {
  def listHeader(group: Group.Slug): HeaderInfo =
    ProposalCtrl.header(group)

  def listBreadcrumb(user: User.Name, group: (Group.Slug, Group.Name), proposal: (Proposal.Id, Talk.Title)): Breadcrumb =
    ProposalCtrl.breadcrumb(user, group, proposal).add("Speakers" -> ProposalRoutes.detail(group._1, proposal._1))

  def header(group: Group.Slug): HeaderInfo =
    listHeader(group)

  def breadcrumb(user: User.Name, group: (Group.Slug, Group.Name), proposal: (Proposal.Id, Talk.Title), speaker: (User.Slug, User.Name)): Breadcrumb =
    listBreadcrumb(user, group, proposal).add(speaker._2.value -> routes.SpeakerCtrl.detail(group._1, proposal._1, speaker._1))

}