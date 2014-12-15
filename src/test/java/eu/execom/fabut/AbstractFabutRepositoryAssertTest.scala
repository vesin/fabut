package eu.execom.fabut

import eu.execom.fabut.enums.AssertType
import eu.execom.fabut.model.TrivialClasses._
import eu.execom.fabut.model.test.{Address, Faculty, Student, Teacher}
import eu.execom.fabut.model.{CopyCaseClass, EmptyClass, EntityTierOneType, EntityTierThreeType, EntityTierTwoType, IgnoredType, ListType, NoDefaultConstructorEntity, ObjectInsideSimpleProperty, ObjectWithComplexProperty, ObjectWithMap, ObjectWithSimpleList, ObjectWithSimpleMap, ObjectWithSimpleProperties, Person, TierFiveType, TierFourType, TierOneType, TierOneTypeDuplicate, TierSixType, TierThreeType, TierTwoType, TierTwoTypeWithIgnoredType}
import org.junit.{After, Assert, Before}

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe.{Type, typeOf}

class AbstractFabutRepositoryAssertTest extends Assert with IFabutRepositoryTest {

  //mock lists
  var entityTierOneTypes: ListBuffer[EntityTierOneType] = ListBuffer()
  var entityTierTwoTypes: ListBuffer[EntityTierTwoType] = ListBuffer()
  var noDefaultConstructorEntities: ListBuffer[NoDefaultConstructorEntity] = ListBuffer()

  private var _fabutRepositoryAssert: FabutRepositoryAssert = null

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

    entityTypes.toList
  }

  override def findAll(clazz: Type) =
    if (typeOf[EntityTierOneType] == clazz) {
      entityTierOneTypes.toList
    } else if (typeOf[EntityTierTwoType] == clazz) {
      entityTierTwoTypes.toList
    } else {
      noDefaultConstructorEntities.toList
    }


  override def findById(entityClass: Type, id: Any) =
    if (typeOf[EntityTierOneType] == entityClass) {
      entityTierOneTypes.find(entity => entity.id == id)
    } else if (typeOf[EntityTierTwoType] == entityClass) {
      entityTierTwoTypes.find(entity => entity.id == id)
    } else {
      noDefaultConstructorEntities.find(entity => entity.id == id)
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

    complexTypes.toList
  }

  override def ignoredTypes: List[Type] = {
    val ignoredTypes: ListBuffer[Type] = ListBuffer()
    ignoredTypes += typeOf[IgnoredType]

    ignoredTypes.toList
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