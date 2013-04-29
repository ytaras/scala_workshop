package waas
package repository
import com.mongodb.casbah.Imports._
import model._
import scalaz._
import syntax.validation._
import syntax.std.option._

case object mongoRepository {
  def save(wf: Workflow)(implicit db: MongoDB) = {
    val loaded: Option[DBObject] = db("workflows").findOneByID(wf.name)
    loaded map {
      _ => ObjectExists("workflow", wf.name)
    } toFailure { db("workflows") += wf.asDbObject }
  }

  def overwrite(wf:Workflow)(implicit db: MongoDB) = {
    db("workflows").findOneByID(wf.name) map {
      _ => db("workflows") += wf.asDbObject
    } toSuccess { ObjectNotExists("workflow", wf.name) }
  }

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
}

sealed trait RepositoryErrors
case class ObjectExists(name: String, id: String)
case class ObjectNotExists(name: String, id: String)
