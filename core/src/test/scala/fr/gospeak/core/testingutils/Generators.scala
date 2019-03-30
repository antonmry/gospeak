package fr.gospeak.core.testingutils

import java.time.{Instant, LocalDateTime, ZoneOffset}

import fr.gospeak.core.domain._
import fr.gospeak.core.domain.utils.Info
import fr.gospeak.libs.scalautils.domain._
import org.scalacheck.ScalacheckShapeless._
import org.scalacheck.{Arbitrary, Gen}

object Generators {
  private val _ = coproductCogen // to keep the `org.scalacheck.ScalacheckShapeless._` import
  private val stringGen = implicitly[Arbitrary[String]].arbitrary
  private val nonEmptyStringGen = Gen.nonEmptyListOf(Gen.alphaChar).map(_.mkString)
  private val slugGen = Gen.nonEmptyListOf(Gen.alphaNumChar).map(_.mkString.take(SlugBuilder.maxLength).toLowerCase)

  implicit val aInstant: Arbitrary[Instant] = Arbitrary(Gen.calendar.map(_.toInstant))
  implicit val aLocalDateTime: Arbitrary[LocalDateTime] = Arbitrary(Gen.calendar.map(c => LocalDateTime.ofInstant(c.toInstant, ZoneOffset.UTC)))
  implicit val aMarkdown: Arbitrary[Markdown] = Arbitrary(stringGen.map(str => Markdown(str)))
  implicit val aSecret: Arbitrary[Secret] = Arbitrary(stringGen.map(str => Secret(str)))
  implicit val aSlides: Arbitrary[Slides] = Arbitrary(slugGen.map(slug => Slides.from(s"http://docs.google.com/presentation/d/$slug").right.get))
  implicit val aVideo: Arbitrary[Video] = Arbitrary(slugGen.map(slug => Video.from(s"http://youtu.be/$slug").right.get))
  implicit val aEmail: Arbitrary[Email] = Arbitrary(slugGen.map(slug => Email.from(slug.take(90) + "@mail.com").right.get)) // TODO improve
  implicit val aUrl: Arbitrary[Url] = Arbitrary(stringGen.map(_ => Url.from("https://www.youtube.com/watch").right.get)) // TODO improve

  implicit val aUserId: Arbitrary[User.Id] = Arbitrary(Gen.uuid.map(id => User.Id.from(id.toString).right.get))
  implicit val aUserSlug: Arbitrary[User.Slug] = Arbitrary(slugGen.map(slug => User.Slug.from(slug).right.get))
  implicit val aTalkId: Arbitrary[Talk.Id] = Arbitrary(Gen.uuid.map(id => Talk.Id.from(id.toString).right.get))
  implicit val aTalkSlug: Arbitrary[Talk.Slug] = Arbitrary(slugGen.map(slug => Talk.Slug.from(slug).right.get))
  implicit val aTalkTitle: Arbitrary[Talk.Title] = Arbitrary(stringGen.map(str => Talk.Title(str)))
  implicit val aTalkStatus: Arbitrary[Talk.Status] = Arbitrary(Gen.oneOf(Talk.Status.all))
  implicit val aGroupId: Arbitrary[Group.Id] = Arbitrary(Gen.uuid.map(id => Group.Id.from(id.toString).right.get))
  implicit val aGroupSlug: Arbitrary[Group.Slug] = Arbitrary(slugGen.map(slug => Group.Slug.from(slug).right.get))
  implicit val aGroupName: Arbitrary[Group.Name] = Arbitrary(stringGen.map(str => Group.Name(str)))
  implicit val aCfpId: Arbitrary[Cfp.Id] = Arbitrary(Gen.uuid.map(id => Cfp.Id.from(id.toString).right.get))
  implicit val aCfpSlug: Arbitrary[Cfp.Slug] = Arbitrary(slugGen.map(slug => Cfp.Slug.from(slug).right.get))
  implicit val aCfpName: Arbitrary[Cfp.Name] = Arbitrary(stringGen.map(str => Cfp.Name(str)))
  implicit val aEventId: Arbitrary[Event.Id] = Arbitrary(Gen.uuid.map(id => Event.Id.from(id.toString).right.get))
  implicit val aEventSlug: Arbitrary[Event.Slug] = Arbitrary(slugGen.map(slug => Event.Slug.from(slug).right.get))
  implicit val aEventName: Arbitrary[Event.Name] = Arbitrary(stringGen.map(str => Event.Name(str)))
  implicit val aProposalId: Arbitrary[Proposal.Id] = Arbitrary(Gen.uuid.map(id => Proposal.Id.from(id.toString).right.get))
  implicit val aProposalStatus: Arbitrary[Proposal.Status] = Arbitrary(Gen.oneOf(Proposal.Status.all))

  // do not write explicit type, it will throw a NullPointerException
  implicit val aInfo = implicitly[Arbitrary[Info]]
  implicit val aGMapPlace = implicitly[Arbitrary[GMapPlace]]
  implicit val aUser = implicitly[Arbitrary[User]]
  implicit val aTalk = implicitly[Arbitrary[Talk]]
  implicit val aGroup = implicitly[Arbitrary[Group]]
  implicit val aCfp = implicitly[Arbitrary[Cfp]]
  implicit val aEvent = implicitly[Arbitrary[Event]]
  implicit val aProposal = implicitly[Arbitrary[Proposal]]
}
