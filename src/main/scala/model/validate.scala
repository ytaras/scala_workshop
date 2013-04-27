package model

import scalaz._
import syntax.validation._
object WorkflowValidator {
  def validate(wf: Workflow) = startSteps(wf) flatMap stepsExist
  private
  def startSteps(wf: Workflow) = wf.steps.filter { _.start }.size match {
    case 1 => wf.successNel
    case 0 => "no start steps found".failNel
    case x => "%d start steps found".format(x).failNel
  }

  def stepsExist(wf: Workflow): ValidationNel[String, Workflow] = {
    import util.set._
    val definedSteps = wf.steps.map { _.name }.toSet
    val usedSteps = wf.steps.flatMap { _.goesTo }.toSet
    val diff: Set[String] = usedSteps &~ definedSteps
    diff.map { "step '%s' is used but not defined" format _ }
      .toNel.map { _.fail } getOrElse wf.success
  }
}

object modelSyntax {
  implicit class workflowSyntax(wf: Workflow) {
    def validate = WorkflowValidator.validate(wf)
  }
}
