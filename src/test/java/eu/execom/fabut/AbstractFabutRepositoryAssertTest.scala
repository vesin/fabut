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
import eu.execom.fabut.model.NoDefaultConstructorEntity
import scala.collection.mutable.ListBuffer
import eu.execom.fabut.model.TierOneTypeDuplicate
import eu.execom.fabut.model.test.Student
import eu.execom.fabut.model.test.Faculty
import eu.execom.fabut.model.test.Address
import eu.execom.fabut.model.test.Teacher

class AbstractFabutRepositoryAssertTest extends Assert with IFabutRepositoryTest {

  //mock lists
  var entityTierOneTypes: ListBuffer[EntityTierOneType] = ListBuffer()
  var entityTierTwoTypes: ListBuffer[EntityTierTwoType] = ListBuffer()
  var noDefaultConstructorEntities: ListBuffer[NoDefaultConstructorEntity] = ListBuffer()

  var _fabutRepositoryAssert: FabutRepositoryAssert = null

  @Before
  override def fabutBeforeTest = {
    _fabutRepositoryAssert = new FabutRepositoryAssert(this, AssertType.REPOSITORY_ASSERT)
  }

  @After
  override def fabutAfterTest = {
  }

  def fabutRepositoryAssert = _fabutRepositoryAssert

  override def entityTypes: List[Type] = {

    val entityTypes: ListBuffer[Type] = ListBuffer()
    entityTypes += typeOf[EntityTierOneType]
    entityTypes += typeOf[EntityTierTwoType]
    entityTypes += typeOf[EntityTierThreeType]
    entityTypes += typeOf[NoDefaultConstructorEntity]
    return entityTypes.toList
  }

  override def findAll(clazz: Type) = {

    if (typeOf[EntityTierOneType] == clazz) {
      entityTierOneTypes.toList
    } else if (typeOf[EntityTierTwoType] == clazz) {
      entityTierTwoTypes.toList
    } else {
      noDefaultConstructorEntities.toList
    }

  }

  override def findById(entityClass: Type, id: Any) = {
    if (typeOf[EntityTierOneType] == entityClass) {
      entityTierOneTypes.find(entity => entity.id == id)
    } else if (typeOf[EntityTierTwoType] == entityClass) {
      entityTierTwoTypes.find(entity => entity.id == id)
    } else {
      noDefaultConstructorEntities.find(entity => entity.id == id)
    }
  }

  override def complexTypes: List[Type] = {

    val complexTypes: ListBuffer[Type] = ListBuffer()
    complexTypes += typeOf[ObjectWithSimpleProperties]
    complexTypes += typeOf[EntityTierThreeType]
    complexTypes += typeOf[ObjectWithComplexProperty]
    complexTypes += typeOf[ObjectInsideSimpleProperty]
    complexTypes += typeOf[ObjectWithMap]
    complexTypes += typeOf[ObjectWithSimpleMap]
    complexTypes += typeOf[ObjectWithSimpleList]
    complexTypes += typeOf[CopyCaseClass]
    complexTypes += typeOf[Person]
    complexTypes += typeOf[TierOneType]
    complexTypes += typeOf[TierTwoType]
    complexTypes += typeOf[TierThreeType]
    complexTypes += typeOf[TierFourType]
    complexTypes += typeOf[TierFiveType]
    complexTypes += typeOf[TierSixType]
    complexTypes += typeOf[TierTwoTypeWithIgnoredType]
    complexTypes += typeOf[EmptyClass]
    complexTypes += typeOf[ListType]
    complexTypes += typeOf[TierOneTypeDuplicate]
    complexTypes += typeOf[A]
    complexTypes += typeOf[B]
    complexTypes += typeOf[C]
    complexTypes += typeOf[D]
    complexTypes += typeOf[E]
    complexTypes += typeOf[Student]
    complexTypes += typeOf[Faculty]
    complexTypes += typeOf[Address]
    complexTypes += typeOf[Teacher]

    return complexTypes.toList
  }

  override def ignoredTypes: List[Type] = {
    val ignoredTypes: ListBuffer[Type] = ListBuffer()
    ignoredTypes += typeOf[IgnoredType]
    return ignoredTypes.toList
  }

  override def customAssertEquals(expectedObject: Any, actualObject: Any) {
    Assert.assertEquals(expectedObject, actualObject)
  }

  def setEntityTierOneTypes(entityTierOneTypes1: ListBuffer[EntityTierOneType]) {
    entityTierOneTypes = entityTierOneTypes1
  }

  def setEntityTierTwoTypes(entityTierTwoTypes1: ListBuffer[EntityTierTwoType]) {
    entityTierTwoTypes = entityTierTwoTypes1
  }

}