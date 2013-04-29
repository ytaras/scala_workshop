package waas
package repository
import com.mongodb.casbah.Imports._
import model._
import scalaz._
import syntax.validation._
import syntax.std.option._

case object mongoRepository {
  def save(wf: Workflow)(implicit db: MongoDB) = {
    val loaded: Option[DBObject] = workflows.findOneByID(wf.name)
    loaded map {
      _ => ObjectExists("workflow", wf.name)
    } toFailure { workflows += wf.asDbObject }
  }

  def overwrite(wf:Workflow)(implicit db: MongoDB) = {
    workflows.findOneByID(wf.name) map {
      _ => workflows += wf.asDbObject
    } toSuccess { ObjectNotExists("workflow", wf.name) }
  }

  def load(name: String)(implicit db: MongoDB) = {
    workflows.findOneByID(name).flatMap { _.asWorkflow }
  }
  private def workflows(implicit db: MongoDB) = db("workflows")

  implicit class wfConverter(wf: Workflow) {
    def convertedSteps = wf.steps.map { _.asDbObject }
    def asDbObject = MongoDBObject(
      "name" -> wf.name,
      "_id" -> wf.name,
      "steps" -> MongoDBList(convertedSteps:_*)
    )
  }
  implicit class stepConverter(st: Step) {
    def asDbObject = MongoDBObject(
      "name" -> st.name, "start" -> st.start,
      "goes" -> MongoDBList(st.goesTo:_*)
    )
  }
  implicit class dbObjectConverter(obj: DBObject) {
    def asWorkflow: Option[Workflow] = for {
      name  <- obj.getAs[String]("name")
      steps <- obj.getAs[MongoDBList]("steps") orElse MongoDBList().some
      convertedSteps = steps.indices.map{ steps.as[DBObject](_) }.map{ _.asStep }.flatten
    } yield Workflow(name, convertedSteps.toList )
    def asStep = for {
      name  <- obj.getAs[String]("name")
      start <- obj.getAs[Boolean]("start") orElse false.some
      goes  <- obj.getAs[MongoDBList]("goes") orElse Nil.some
      convertedGoes = goes.map{(_:Any).toString }.toList
    } yield Step(name, convertedGoes, start)
  }
}

sealed trait RepositoryErrors
case class ObjectExists(name: String, id: String)
case class ObjectNotExists(name: String, id: String)
