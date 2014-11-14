package eu.execom.fabut

import org.junit.Assert
import scala.reflect.runtime.universe.{ Type, typeOf }
import eu.execom.fabut.model.ObjectWithComplexProperty
import eu.execom.fabut.model.CopyCaseClass
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.model.ObjectWithSimpleProperties
import eu.execom.fabut.model.ObjectWithMap
import eu.execom.fabut.model.ObjectWithSimpleList
import eu.execom.fabut.model.ObjectWithSimpleMap
import eu.execom.fabut.model.TrivialClasses._
import eu.execom.fabut.model.Person
import eu.execom.fabut.model.EntityTierOneType
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.EntityTierThreeType
import eu.execom.fabut.model.EntityTierTwoType
import eu.execom.fabut.model.EmptyClass

class AbstractFabutObjectAssertTest extends Assert with IFabutTest {

  var fabutObjectAssert: FabutObjectAssert = null

  override def fabutBeforeTest() {
    fabutObjectAssert = new FabutObjectAssert(this)
  }

  override def fabutAfterTest() {

  }

  override def getComplexTypes(): List[Type] = {

    var complexTypes: List[Type] = List()
    complexTypes ::= typeOf[ObjectWithSimpleProperties]
    complexTypes ::= typeOf[ObjectWithComplexProperty]
    complexTypes ::= typeOf[ObjectInsideSimpleProperty]
    complexTypes ::= typeOf[ObjectWithMap]
    complexTypes ::= typeOf[ObjectWithSimpleMap]
    complexTypes ::= typeOf[ObjectWithSimpleList]
    complexTypes ::= typeOf[CopyCaseClass]
    complexTypes ::= typeOf[Person]
    complexTypes ::= typeOf[A]
    complexTypes ::= typeOf[B]
    complexTypes ::= typeOf[C]
    complexTypes ::= typeOf[D]
    complexTypes ::= typeOf[E]
    complexTypes ::= typeOf[EntityTierOneType]
    complexTypes ::= typeOf[EntityTierThreeType]
    complexTypes ::= typeOf[EntityTierTwoType]
    complexTypes ::= typeOf[EntityTierOneType]
    complexTypes ::= typeOf[TierOneType]
    complexTypes ::= typeOf[EmptyClass]
    return complexTypes
  }

  override def getEntityTypes(): List[Type] = {

    var entityTypes: List[Type] = List()

    return entityTypes
  }

  override def getIgnoredTypes(): List[Type] = {
    println("TODO - in AbstractFabutObjectAssertTest getIgnoredTypes")
    List()
  }

  override def customAssertEquals(expectedObject: Any, actualObject: Any) {
    Assert.assertEquals(expectedObject, actualObject)
  }

}