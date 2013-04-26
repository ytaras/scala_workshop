package model

import scalaz._
import syntax.validation._
object WorkflowValidator {
  def validate(wf: Workflow) = startSteps(wf)
  private
  def startSteps(wf: Workflow) = wf.steps.filter { _.start }.size match {
    case 1 => wf.successNel
    case 0 => "no start steps found".failNel
    case x => "%d start steps found".format(x).failNel
  }
}

object modelSyntax {
  implicit class workflow(wf: Workflow) {
    def validate = WorkflowValidator.validate(wf)
  }
}
