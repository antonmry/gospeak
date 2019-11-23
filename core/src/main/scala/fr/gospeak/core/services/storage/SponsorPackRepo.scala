package fr.gospeak.core.services.storage

import cats.effect.IO
import fr.gospeak.core.domain.utils.OrgaCtx
import fr.gospeak.core.domain.{Group, SponsorPack}
import fr.gospeak.libs.scalautils.domain.Done

trait SponsorPackRepo extends OrgaSponsorPackRepo with PublicSponsorPackRepo with SuggestSponsorPackRepo

trait OrgaSponsorPackRepo {
  def create(data: SponsorPack.Data)(implicit ctx: OrgaCtx): IO[SponsorPack]

  def edit(pack: SponsorPack.Slug, data: SponsorPack.Data)(implicit ctx: OrgaCtx): IO[Done]

  def disable(pack: SponsorPack.Slug)(implicit ctx: OrgaCtx): IO[Done]

  def enable(pack: SponsorPack.Slug)(implicit ctx: OrgaCtx): IO[Done]

  def find(pack: SponsorPack.Slug)(implicit ctx: OrgaCtx): IO[Option[SponsorPack]]

  def listAll(group: Group.Id): IO[Seq[SponsorPack]]

  def listAll(implicit ctx: OrgaCtx): IO[Seq[SponsorPack]]

  def listActives(group: Group.Id): IO[Seq[SponsorPack]]
}

trait PublicSponsorPackRepo {
  def listActives(group: Group.Id): IO[Seq[SponsorPack]]
}

trait SuggestSponsorPackRepo {
  def listAll(group: Group.Id): IO[Seq[SponsorPack]]
}
