package fr.gospeak.web.utils

import fr.gospeak.core.domain.utils.{Email, Password}
import fr.gospeak.core.domain.{Event, Talk}
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.validation._
import play.api.data.{FormError, Mapping}

import scala.util.matching.Regex

object Mappings {
  val requiredConstraint = "constraint.required"
  val requiredError = "error.required"

  private[utils] def required[A](stringify: A => String): Constraint[A] = Constraint[A](requiredConstraint) { o =>
    if (o == null) Invalid(ValidationError(requiredError))
    else {
      val str = stringify(o)
      if (str == null) Invalid(ValidationError(requiredError))
      else if (str.trim.isEmpty) Invalid(ValidationError(requiredError))
      else Valid
    }
  }

  val patternConstraint = "constraint.pattern"
  val patternError = "error.pattern"

  private[utils] def pattern[A](regex: Regex)(stringify: A => String): Constraint[A] = Constraint[A](patternConstraint, regex) { o =>
    if (o == null) Invalid(ValidationError(patternError, regex))
    else regex.unapplySeq(stringify(o)).map(_ => Valid).getOrElse(Invalid(ValidationError(patternError, regex)))
  }

  private[utils] def formatter[A](from: String => A, to: A => String): Formatter[A] = new Formatter[A] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], A] =
      data.get(key).map(from).toRight(Seq(FormError(key, requiredError)))

    override def unbind(key: String, value: A): Map[String, String] =
      Map(key -> to(value))
  }

  private def stringMapping[A](from: String => A, to: A => String, cs: ((A => String) => Constraint[A])*): Mapping[A] =
    of(formatter(from, to)).verifying(cs.map(_ (to)): _*)

  private val slugRegex = "[a-z0-9-]+".r
  val mail: Mapping[Email] = stringMapping(Email, _.value, required)
  val password: Mapping[Password] = stringMapping(Password, _.decode, required)
  val talkSlug: Mapping[Talk.Slug] = stringMapping(Talk.Slug, _.value, required, pattern(slugRegex))
  val talkTitle: Mapping[Talk.Title] = stringMapping(Talk.Title, _.value, required)
  val eventSlug: Mapping[Event.Slug] = stringMapping(Event.Slug, _.value, required, pattern(slugRegex))
  val eventName: Mapping[Event.Name] = stringMapping(Event.Name, _.value, required)
}
