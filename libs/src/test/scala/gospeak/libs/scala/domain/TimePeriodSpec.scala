package gospeak.libs.scala.domain

import gospeak.libs.scala.testingutils.Generators._
import gospeak.libs.testingutils.BaseSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.concurrent.duration.FiniteDuration

class TimePeriodSpec extends BaseSpec with ScalaCheckPropertyChecks {
  describe("TimePeriod") {
    it("build and transform to FiniteDuration") {
      forAll { v: FiniteDuration =>
        val p = TimePeriod.from(v)
        val r = p.toDuration.get
        r shouldBe v
      }
    }
    it("should parse and serialize to String") {
      forAll { v: TimePeriod =>
        val s = v.value
        val p = TimePeriod.from(s).get
        p shouldBe v
      }
    }
  }
}
