package waas
import org.specs2.matcher._
import scalaz._
import syntax.validation._
trait ValidationSpec { self: Matchers =>
  def beSuccess = beTrue ^^ { (_:Validation[_, _]).isSuccess }
  def beFailure(s: String) = be_==(s.failNel)
}
