package parser

import scala.util.parsing.combinator._
import model._
import scalaz._
import syntax.validation._

// TODO We could use JavaIdenParser to reuse ident parser
object WorkflowParser extends RegexParsers {
  def id      : Parser[String] = """[a-zA-Z]+""".r
  def workflow: Parser[Workflow] = ("workflow" ~> id <~ "{") ~ step.* <~ ("}" ~ ";") ^^ {
    case name ~ steps => Workflow(name, steps)
  }
  def workflows: Parser[List[Workflow]] = workflow.*
  def step     : Parser[Step] = ("start".? <~ "step") ~ id ~ goesTo.? <~ (";") ^^ {
    case startOpt ~ name ~ goesOpt => {
      val isStart = startOpt map { _ => true } getOrElse false
      val goesList = goesOpt getOrElse Nil
      Step(name, goesList, isStart)
    }
  }
  def goesTo   : Parser[List[String]] = ("goes" ~ "to") ~> (id <~ ",").* ~ id ^^ {
    // TODO Could this be done simpler?
    case list ~ x => list :+ x
  }
  def apply(inputString: String): Validation[String, List[Workflow]] = parseAll(workflows, inputString).toValid
  implicit class parsed[A](s: ParseResult[A]) {
    def toValid: Validation[String, A] = s match {
      case Success(x, _)   => x.success
      case NoSuccess(x, _) => x.failure
    }
  }
}

