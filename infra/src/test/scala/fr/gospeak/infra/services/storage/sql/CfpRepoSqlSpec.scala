package fr.gospeak.infra.services.storage.sql

import cats.data.NonEmptyList
import fr.gospeak.core.domain.{Group, Talk}
import fr.gospeak.infra.services.storage.sql.CfpRepoSqlSpec._
import fr.gospeak.infra.services.storage.sql.EventRepoSqlSpec.{table => eventTable}
import fr.gospeak.infra.services.storage.sql.testingutils.RepoSpec

class CfpRepoSqlSpec extends RepoSpec {
  describe("CfpRepoSql") {
    it("should create and retrieve a cfp for a group") {
      val (user, group) = createUserAndGroup().unsafeRunSync()
      val talkId = Talk.Id.generate()
      cfpRepo.find(cfpData1.slug).unsafeRunSync() shouldBe None
      cfpRepo.list(group.id, params).unsafeRunSync().items shouldBe Seq()
      cfpRepo.availableFor(talkId, params).unsafeRunSync().items shouldBe Seq()
      val cfp = cfpRepo.create(group.id, cfpData1, user.id, now).unsafeRunSync()
      cfpRepo.find(cfp.id).unsafeRunSync().get shouldBe cfp
      cfpRepo.find(cfpData1.slug).unsafeRunSync().get shouldBe cfp
      cfpRepo.list(group.id, params).unsafeRunSync().items shouldBe Seq(cfp)
      cfpRepo.availableFor(talkId, params).unsafeRunSync().items shouldBe Seq(cfp)
    }
    it("should fail to create a cfp when the group does not exists") {
      val user = userRepo.create(userData1, now).unsafeRunSync()
      an[Exception] should be thrownBy cfpRepo.create(Group.Id.generate(), cfpData1, user.id, now).unsafeRunSync()
    }
    it("should fail to create two cfp for a group") {
      val (user, group) = createUserAndGroup().unsafeRunSync()
      cfpRepo.create(group.id, cfpData1, user.id, now).unsafeRunSync()
      an[Exception] should be thrownBy cfpRepo.create(group.id, cfpData1, user.id, now).unsafeRunSync()
    }
    it("should fail on duplicate slug") {
      val (user, group1) = createUserAndGroup().unsafeRunSync()
      val group2 = groupRepo.create(groupData2, user.id, now).unsafeRunSync()
      cfpRepo.create(group1.id, cfpData1, user.id, now).unsafeRunSync()
      an[Exception] should be thrownBy cfpRepo.create(group2.id, cfpData1, user.id, now).unsafeRunSync()
    }
    describe("Queries") {
      it("should build insert") {
        val q = CfpRepoSql.insert(cfp)
        check(q, s"INSERT INTO ${table.stripSuffix(" c")} (${mapFields(fields, _.stripPrefix("c."))}) VALUES (${mapFields(fields, _ => "?")})")
      }
      it("should build update") {
        val q = CfpRepoSql.update(group.id, cfp.slug)(cfpData1, user.id, now)
        check(q, s"UPDATE $table SET c.slug=?, c.name=?, c.begin=?, c.close=?, c.description=?, c.tags=?, c.updated=?, c.updated_by=? WHERE c.group_id=? AND c.slug=?")
      }
      it("should build selectOne for cfp id") {
        val q = CfpRepoSql.selectOne(cfp.id)
        check(q, s"SELECT $fields FROM $table WHERE c.id=?")
      }
      it("should build selectOne for cfp slug") {
        val q = CfpRepoSql.selectOne(cfp.slug)
        check(q, s"SELECT $fields FROM $table WHERE c.slug=?")
      }
      it("should build selectOne for group id and cfp slug") {
        val q = CfpRepoSql.selectOne(group.id, cfp.slug)
        check(q, s"SELECT $fields FROM $table WHERE c.group_id=? AND c.slug=?")
      }
      it("should build selectOne for event id") {
        val q = CfpRepoSql.selectOne(event.id)
        check(q, s"SELECT $fields FROM $table INNER JOIN $eventTable e ON c.id=e.cfp_id WHERE e.id=?")
      }
      it("should build selectOne for cfp slug id and date") {
        val q = CfpRepoSql.selectOne(cfp.slug, now)
        check(q, s"SELECT $fields FROM $table WHERE (c.begin IS NULL OR c.begin < ?) AND (c.close IS NULL OR c.close > ?) AND c.slug=?")
      }
      it("should build selectPage for a group") {
        val q = CfpRepoSql.selectPage(group.id, params)
        check(q, s"SELECT $fields FROM $table WHERE c.group_id=? ORDER BY c.close IS NULL, c.close DESC, c.name IS NULL, c.name OFFSET 0 LIMIT 20")
      }
      it("should build selectPage for a talk") {
        val q = CfpRepoSql.selectPage(talk.id, params)
        check(q, s"SELECT $fields FROM $table WHERE c.id NOT IN (SELECT p.cfp_id FROM proposals p WHERE p.talk_id=?) ORDER BY c.close IS NULL, c.close DESC, c.name IS NULL, c.name OFFSET 0 LIMIT 20")
      }
      it("should build selectPage for a date") {
        val q = CfpRepoSql.selectPage(now, params)
        check(q, s"SELECT $fields FROM $table WHERE (c.begin IS NULL OR c.begin < ?) AND (c.close IS NULL OR c.close > ?) ORDER BY c.close IS NULL, c.close DESC, c.name IS NULL, c.name OFFSET 0 LIMIT 20")
      }
      it("should build selectAll for group id") {
        val q = CfpRepoSql.selectAll(group.id)
        check(q, s"SELECT $fields FROM $table WHERE c.group_id=?")
      }
      it("should build selectAll for cfp ids") {
        val q = CfpRepoSql.selectAll(NonEmptyList.of(cfp.id, cfp.id, cfp.id))
        check(q, s"SELECT $fields FROM $table WHERE c.id IN (?, ?, ?) ")
      }
      it("should build selectAll for group and date") {
        val q = CfpRepoSql.selectAll(group.id, now)
        check(q, s"SELECT $fields FROM $table WHERE (c.begin IS NULL OR c.begin < ?) AND (c.close IS NULL OR c.close > ?) AND c.group_id=?")
      }
      it("should build selectTags") {
        val q = CfpRepoSql.selectTags()
        check(q, s"SELECT c.tags FROM $table")
      }
    }
  }
}

object CfpRepoSqlSpec {
  val table = "cfps c"
  val fields = "c.id, c.group_id, c.slug, c.name, c.begin, c.close, c.description, c.tags, c.created, c.created_by, c.updated, c.updated_by"
}
