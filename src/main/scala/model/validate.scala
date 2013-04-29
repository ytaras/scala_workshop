package model

import scalaz._
import syntax.validation._
object WorkflowValidator {
  def validate(wf: Workflow): ValidationNel[String, Workflow] = {
    import syntax.std.list._
    val errors = validators.flatMap{ _.apply(wf) }.toNel
    errors map { _.fail } getOrElse wf.success
  }
    //startSteps(wf) flatMap stepsExist
  private
  val validators: List[Workflow => List[String]] = List(startSteps _, stepsExist _)
  def startSteps(wf: Workflow) = wf.steps.filter { _.start }.size match {
    case 1 => Nil
    case 0 => List("no start steps found")
    case x => List("%d start steps found".format(x))
  }

  def stepsExist(wf: Workflow) = {
    import util.set._
    val definedSteps = wf.steps.map { _.name }.toSet
    val usedSteps = wf.steps.flatMap { _.goesTo }.toSet
    val diff: Set[String] = usedSteps &~ definedSteps
    diff.map { "step '%s' is used but not defined" format _ }.toList
  }
}

object modelSyntax {
  implicit class workflowSyntax(wf: Workflow) {
    def validate = WorkflowValidator.validate(wf)
  }
}
