package waas.repository

import waas._
import org.specs2.mock._
import org.specs2.mutable._
import org.specs2.specification.Scope
import com.mongodb.casbah.Imports._
import model._

class MongoRepositorySpec extends Specification with Mockito with ValidationSpec {
  trait dbContext extends Mockito with Scope {
    lazy val workflowColl = mock[MongoCollection]
    implicit lazy val db = {
      val ret = mock[MongoDB].smart
      ret("workflows") returns workflowColl
      ret
    }
  }

  "save" should {
    "save empty workflow" in new dbContext {
      mongoRepository.save(Workflow("empty")) must beSuccess
      there was (one(workflowColl) += MongoDBObject("_id" -> "empty", "name" -> "empty", "steps" -> Nil))
      mongoRepository.save(Workflow("other")) must beSuccess
      there was (one(workflowColl) += MongoDBObject("_id" -> "other", "name" -> "other", "steps" -> Nil))
    }
    "save workflow with one step" in new dbContext {
      val wf = Workflow(
        "wf",
        List( Step("step", true) ))
      mongoRepository.save(wf) must beSuccess

      val expectedObject = MongoDBObject(
        "name" -> "wf",
        "_id" -> "wf",
        "steps" -> List(
          Map("name" -> "step", "start" -> true, "goes" -> Nil)
        )
      )
      there was (one(workflowColl) += expectedObject)
    }

    "save workflow with few steps" in new dbContext {
      val wf = Workflow(
        "wf",
        List( Step("start", List("end"), true), Step("end") ))

      mongoRepository.save(wf) must beSuccess

      val expectedObject = MongoDBObject(
        "name" -> "wf",
        "_id" -> "wf",
        "steps" -> List(
          Map("name" -> "start", "start" -> true, "goes" -> List("end")),
          Map("name" -> "end",   "start" -> false, "goes" -> Nil)
        )
      )
      there was (one(workflowColl) += expectedObject)
    }
  }
}
