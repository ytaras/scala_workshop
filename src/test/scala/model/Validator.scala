package model

import org.specs2.mutable._
import org.specs2.ScalaCheck
import scalaz._
import syntax.validation._
import org.scalacheck._
class ValidatorSpec extends Specification with ScalaCheck {
  import modelSyntax._
  // TODO Use parser for fixtures!
  "Workflow validator" should {
    "pass valid workflow" in {
      val wf = Workflow("sample", List(
        Step("start", List("end"),true), Step("end")
      ))
      wf.validate must_== wf.successNel
    }
    "fail on no start step" in {
      val wf = Workflow("sample", List(
        Step("start", List("end"), true), Step("end", true)
      ))
      wf.validate must_== "2 start steps found".failNel
    }
    "fail on no start step" in {
      val wf = Workflow("sample", List(
        Step("start", List("end")), Step("end")
      ))
      wf.validate must_== "no start steps found".failNel
    }
  }
  "syntax extension" should {
    import generators._
    "be called as method" in checkProp(Prop.forAll(genWorkflow)(
      (wf: Workflow) => WorkflowValidator.validate(wf) must_== wf.validate
    ))
  }
}

object generators {
  import Gen._
  import Arbitrary.arbitrary

  val genStep = for {
    name <- arbitrary[String]
    other <- arbitrary[List[String]]
    start <- arbitrary[Boolean]
  } yield Step(name, other, start)
  val genWorkflow = for {
    name <- arbitrary[String]
    steps <- listOf(genStep)
  } yield Workflow(name, steps)
}
