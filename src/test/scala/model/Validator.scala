package waas.model

import waas._
import org.specs2.mutable._
import org.specs2.ScalaCheck
import scalaz._
import syntax.validation._
import org.scalacheck._
class ValidatorSpec extends Specification with ScalaCheck with ValidationSpec {
  import modelSyntax._
  import parser._
  import WorkflowParser._
  def parse(s: String): Workflow = WorkflowParser(s).toOption.map{ _.head }.get

  "start steps validator" should {
    "fail on 2 start step" in {
      parse("""workflow sample {
          start step start goes to end;
          start step end;
        };""").validate must_== "2 start steps found".failNel
      }
      "fail on no start step" in {
        parse("""workflow sample {
          step start goes to end;
          step end goes to start;
        };""").validate must_== "no start steps found".failNel
      }
    }
  "steps validator" should {
    "fail if step is not defined and used" in {
      parse("""workflow sample {
        start step start goes to end1, end;
        step end;
      };""").validate must_== "step 'end1' is used but not defined".failNel
    }
    "stack errors" in {
      parse("""workflow sample {
        start step start goes to end1, end;
        step end goes to end2, end3;
      };""").validate must_== NonEmptyList(
        "step 'end1' is used but not defined",
        "step 'end2' is used but not defined",
        "step 'end3' is used but not defined"
      ).fail
    }
  }
  "step accessibility validator" should {
    "fail is step is defined but not accessible" in {
      parse("""workflow sample {
        start step start goes to end;
        step end;
        step end2;
      };""").validate must_== "step 'end2' is defined but not used".failNel
    }
  }
  "syntax extension" should {
    import generators._
    "pass valid workflow" in {
      parse("""workflow sample {
        start step start goes to end;
        step end;
      };""").validate must beSuccess
    }
    "stack errros from different validators" in {
      parse("""workflow sample {
        start step s1 goes to e1;
        start step s2 goes to e3;
      };""").validate must_== NonEmptyList(
        "2 start steps found",
        "step 'e1' is used but not defined",
        "step 'e3' is used but not defined"
      ).fail
    }
    "be called as method" in prop {
      (wf: Workflow) => WorkflowValidator.validate(wf) must_== wf.validate
    }
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

  implicit def stepArb = Arbitrary { genStep }
  implicit def workflowArb: Arbitrary[Workflow] = Arbitrary { genWorkflow }
}
