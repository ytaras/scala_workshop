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

class MongoRepositorySpec extends Specification
  with ValidationSpec with ScalaCheck {
  trait dbContext extends Mockito {
    lazy val workflowColl = mock[MongoCollection]
    implicit lazy val db = {
      val ret = mock[MongoDB].smart
      ret("workflows") returns workflowColl
      ret
    }
  }

  "implicit conversion" should {
    "convert workflow" in {
      val name = Prop.forAll { (wf: Workflow) => wf.asDbObject("name") === wf.name }
      val id = Prop.forAll { (wf: Workflow) => wf.asDbObject("_id") === wf.name }
      val steps = Prop.forAll { (wf: Workflow) =>
        wf.asDbObject("steps") ===
          MongoDBList(wf.steps map { _.asDbObject }:_*)
      }
      name && steps && id
    }
    "convert step" in {
      val name = Prop.forAll { (st: Step) => st.asDbObject("name") === st.name }
      val start = Prop.forAll {
        (st: Step) => st.asDbObject("start") === st.start
      }
      val goes = Prop.forAll {
        (st: Step) => st.asDbObject("goes") === MongoDBList(st.goesTo:_*)
      }
      name && start && goes
    }
  }
  "save" should {
    "save converted workflow" in prop { wf: Workflow => 
      val context = new dbContext {}
      import context._
      mongoRepository.save(wf) must beSuccess
      there was (one(workflowColl) += wf.asDbObject)
    }
  }
}
