package waas
package util

import scalaz._
import std.list.listSyntax._

object set {
  implicit class setSyntax[A](s: Set[A]) {
    def toNel: Option[NonEmptyList[A]] = s.toList.toNel
  }
}
