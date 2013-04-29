package waas
package repository
import com.mongodb.casbah.Imports._
import model._
import scalaz._
import syntax.validation._

case object mongoRepository {
  def save(wf: Workflow)(implicit db: MongoDB) = {
    val builder = MongoDBObject.newBuilder
    builder += "name" -> wf.name
    builder += "_id" -> wf.name
    val steps = wf.steps map {
      x => Map(
        "name" -> x.name,
        "start" -> x.start,
        "goes" -> x.goesTo
    )}
    builder += "steps" -> steps
    (db("workflows") += builder.result).success
  }
}
