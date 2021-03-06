package gospeak.libs.scala

import java.time.{Duration, Instant, LocalDateTime, ZoneId}

import scala.concurrent.duration.{FiniteDuration, MILLISECONDS, NANOSECONDS}
import scala.util.Try

object TimeUtils {
  def toInstant(date: LocalDateTime, zone: ZoneId): Instant =
    date.toInstant(zone.getRules.getOffset(date))

  def toFiniteDuration(duration: String): Try[FiniteDuration] =
    Try(Duration.parse(duration)).map(v => FiniteDuration(v.toNanos, NANOSECONDS).toCoarsest)

  def toFiniteDuration(start: Instant, end: Instant): FiniteDuration =
    FiniteDuration(end.toEpochMilli - start.toEpochMilli, MILLISECONDS).toCoarsest
}
