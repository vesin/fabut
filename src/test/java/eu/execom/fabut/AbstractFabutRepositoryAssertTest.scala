package eu.execom.fabut

import org.junit.Assert
import scala.reflect.runtime.universe.{ Type, typeOf }
import eu.execom.fabut.model.EntityTierOneType
import eu.execom.fabut.model.TierTwoType
import eu.execom.fabut.model.ObjectWithComplexProperty
import eu.execom.fabut.model.TierSixType
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.CopyCaseClass
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.model.IgnoredType
import eu.execom.fabut.model.ObjectWithSimpleProperties
import eu.execom.fabut.model.TierTwoTypeWithIgnoredType
import eu.execom.fabut.model.ObjectWithMap
import eu.execom.fabut.model.EntityTierThreeType
import eu.execom.fabut.model.Person
import eu.execom.fabut.model.TierThreeType
import eu.execom.fabut.model.ObjectWithSimpleList
import eu.execom.fabut.model.EntityTierTwoType
import eu.execom.fabut.model.ObjectWithSimpleMap
import eu.execom.fabut.model.ListType
import eu.execom.fabut.model.TierFiveType
import eu.execom.fabut.model.EmptyClass
import eu.execom.fabut.model.TierFourType
import eu.execom.fabut.model.TrivialClasses._
import eu.execom.fabut.model.EntityTierOneType
import eu.execom.fabut.model.EntityTierTwoType
import org.junit.Before
import org.junit.After
import eu.execom.fabut.enums.AssertType

class AbstractFabutRepositoryAssertTest extends Assert with IFabutRepositoryTest {

  //mock lists
  var entityTierOneTypes: List[EntityTierOneType] = List()
  var entityTierTwoTypes: List[EntityTierTwoType] = List()
  var entityTierThreeTypes: List[EntityTierThreeType] = List()

  var fabutRepositoryAssert: FabutRepositoryAssert = null

  @Before
  override def fabutBeforeTest = {
    fabutRepositoryAssert = new FabutRepositoryAssert(this, AssertType.REPOSITORY_ASSERT)
  }

  @After
  override def fabutAfterTest = {}

  override def getEntityTypes(): List[Type] = {

    var entityTypes: List[Type] = List()
    entityTypes ::= typeOf[EntityTierOneType]
    entityTypes ::= typeOf[EntityTierTwoType]
    entityTypes ::= typeOf[EntityTierThreeType]
    return entityTypes
  }

  override def findAll(clazz: Type) = {

    if (typeOf[EntityTierOneType] == clazz) {
      entityTierOneTypes
    } else if (typeOf[EntityTierTwoType] == clazz) {
      entityTierTwoTypes
    } else {
      entityTierThreeTypes
    }

  }

  override def findById(entityClass: Type, id: Any) = {
    if (typeOf[EntityTierOneType] == entityClass) {
      entityTierOneTypes.find(entity => entity.id == id)
    } else if (typeOf[EntityTierTwoType] == entityClass) {
      entityTierTwoTypes.find(entity => entity.id == id)
    } else {
      entityTierThreeTypes.find(entity => entity.id == id)
    }

    //TODO no default constructor 
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
    complexTypes ::= typeOf[EntityTierOneType]
    complexTypes ::= typeOf[EntityTierThreeType]
    complexTypes ::= typeOf[EntityTierTwoType]
    complexTypes ::= typeOf[EntityTierOneType]
    complexTypes ::= typeOf[TierOneType]
    complexTypes ::= typeOf[TierTwoType]
    complexTypes ::= typeOf[TierThreeType]
    complexTypes ::= typeOf[TierFourType]
    complexTypes ::= typeOf[TierFiveType]
    complexTypes ::= typeOf[TierSixType]
    complexTypes ::= typeOf[TierTwoTypeWithIgnoredType]
    complexTypes ::= typeOf[EmptyClass]
    complexTypes ::= typeOf[ListType]
    complexTypes ::= typeOf[A]
    complexTypes ::= typeOf[B]
    complexTypes ::= typeOf[C]
    complexTypes ::= typeOf[D]
    complexTypes ::= typeOf[E]

    return complexTypes
  }

  override def getIgnoredTypes(): List[Type] = {
    var ignoredTypes: List[Type] = List()
    ignoredTypes ::= typeOf[IgnoredType]

    return ignoredTypes
  }

  override def customAssertEquals(expectedObject: Any, actualObject: Any) {
    Assert.assertEquals(expectedObject, actualObject)
  }

}