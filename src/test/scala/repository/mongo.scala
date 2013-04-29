package waas.repository

import waas._
import org.specs2.mock._
import org.specs2.mutable._
import org.specs2.ScalaCheck
import org.specs2.specification.Scope
import com.mongodb.casbah.Imports._
import model._
import mongoRepository._
import generators._
import org.scalacheck.Prop
import scalaz._
import syntax.std.option._

class MongoRepositorySpec extends Specification
  with ValidationSpec with ScalaCheck {
  trait dbContext extends Mockito {
    lazy val workflowColl = {
      val ret = mock[MongoCollection].smart
      ret.findOneByID(any[String]) returns {
        if(valueExist) Some(MongoDBObject()) else None
      }
      ret
    }
    val valueExist = false
    implicit lazy val db = {
      val ret = mock[MongoDB].smart
      ret("workflows") returns workflowColl
      ret
    }
  }

  "implicit conversion" should {
    "convert from workflow" in {
      val name = Prop.forAll { (wf: Workflow) => wf.asDbObject("name") === wf.name }
      val id = Prop.forAll { (wf: Workflow) => wf.asDbObject("_id") === wf.name }
      val steps = Prop.forAll { (wf: Workflow) =>
        wf.asDbObject("steps") ===
          MongoDBList(wf.steps map { _.asDbObject }:_*)
      }
      name && steps && id
    }
    "convert to workflow" in {
      MongoDBObject("name" -> "name").asWorkflow must_== Workflow("name").some
      MongoDBObject("noname" -> "name").asWorkflow must_== None
    }
    "converts workflow back and forth" in prop { wf: Workflow =>
      wf.asDbObject.asWorkflow must_== wf.some
    }
    "convert from step" in {
      val name = Prop.forAll { (st: Step) => st.asDbObject("name") === st.name }
      val start = Prop.forAll {
        (st: Step) => st.asDbObject("start") === st.start
      }
      val goes = Prop.forAll {
        (st: Step) => st.asDbObject("goes") === MongoDBList(st.goesTo:_*)
      }
      name && start && goes
    }
    "convert to step" in {
      MongoDBObject("name" -> "name").asStep must_== Step("name").some
      MongoDBObject("noname" -> "name").asStep must_== None
      MongoDBObject("name" -> "name", "start" -> true).asStep must_== Step("name", true).some
      MongoDBObject("name" -> "name", "goes" -> Nil).asStep must_== Step("name").some
      MongoDBObject("name" -> "name", "goes" -> List("a")).asStep must_== Step("name", List("a")).some
    }
  }
  "overwrite" should {
    "save converted workflow" in prop { wf: Workflow =>
      val context = new dbContext { override val valueExist = true}
      import context._
      mongoRepository.overwrite(wf) must beSuccess
      there was (one(workflowColl) += wf.asDbObject)
    }
    "dont save if value exist" in prop { wf: Workflow =>
      val context = new dbContext { }
      import context._
      mongoRepository.overwrite(wf) must
        beFailure(ObjectNotExists("workflow", wf.name))

      there was (no(workflowColl) += wf.asDbObject)
    }
  }
  "save" should {
    "save converted workflow" in prop { wf: Workflow =>
      val context = new dbContext {}
      import context._
      mongoRepository.save(wf) must beSuccess
      there was (one(workflowColl) += wf.asDbObject)
    }
    "dont save if value exist" in prop { wf: Workflow =>
      val context = new dbContext { override val valueExist = true }
      import context._
      mongoRepository.save(wf) must
        beFailure(ObjectExists("workflow", wf.name))

      there was (no(workflowColl) += wf.asDbObject)
    }
  }
  "load" should {
    "load and convert" in prop { wf: Workflow =>
      val context = new dbContext {}
      import context._
      workflowColl.findOneByID(wf.name) returns wf.asDbObject.some

      mongoRepository.load(wf.name) must_== wf.some
    }
  }
}
