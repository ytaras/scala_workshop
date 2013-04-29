package waas.parser

import waas._
import org.specs2.mutable._
import model._
import scalaz._
import syntax.validation._
import WorkflowParser.{step => wStep, Success => ParseSuccess}
import WorkflowParser._

class ParserSpec extends Specification {
  def parse(s: String) = WorkflowParser(s)
  "File parser" should {
    "parse empty string" in {
      parse("") must_== List().success
    }
    "parse single empty workflow" in {
      parse("workflow name { };") must_== List(Workflow("name")).success
    }
    "parse few empty workflows" in {
      parse("""workflow name {
        };
        workflow secondName { };""") must_== List(Workflow("name"), Workflow("secondName")).success
    }
    "parse single not empty workflow" in {
      parse("""workflow name {
          start step step;
        };""") must_== List(Workflow("name", List(Step("step", true)))).success
    }
  }
  "Step parser" should {
    "parse no-go step" in {
      parseAll(wStep, "step name;").toValid must_== Step("name").success
    }
    "parse goes to step" in {
      parseAll(wStep, "step name goes to second, name;").toValid must_== Step("name", List("second", "name")).success
    }
    "parse start no-go step" in {
      parseAll(wStep, "start step name;").toValid must_== Step("name", true).success
    }

    "parse start goes step" in {
      parseAll(wStep, "start step name goes to second, third;").toValid must_== Step("name", List("second", "third"), true).success
    }
  }
  "Goes to parser" should {
    "parse goes to single" in {
      parseAll(goesTo, "goes to name").toValid must_== List("name").success
    }
    "parse goes to few" in {
      parseAll(goesTo, "goes to name, otherName").toValid must_== List("name", "otherName").success
    }
  }
}
