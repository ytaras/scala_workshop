package model

case class Workflow(name: String, steps: List[Step])
object Workflow {
  def apply(name: String): Workflow = Workflow(name, Nil)
}
case class Step(name: String, goesTo: List[String], start: Boolean)
object Step {
  def apply(name: String): Step = Step(name, Nil, false)
  def apply(name: String, start: Boolean): Step = Step(name, Nil, start)
  def apply(name: String, goes: List[String]): Step = Step(name, goes, false)
}
