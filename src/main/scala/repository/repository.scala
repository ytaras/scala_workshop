package waas
package repository
import com.mongodb.casbah.Imports._
import model._
import scalaz._
import syntax.validation._

case object mongoRepository {
  def save(wf: Workflow)(implicit db: MongoDB) = {
    (db("workflows") += wf.asDbObject).success
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
