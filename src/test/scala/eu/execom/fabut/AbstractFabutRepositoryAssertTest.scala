package eu.execom.fabut

import eu.execom.fabut.model.TrivialClasses._
import eu.execom.fabut.model._
import eu.execom.fabut.model.test._
import org.junit.{After, Assert, Before}

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe.{Type, typeOf}

class AbstractFabutRepositoryAssertTest extends Assert with IFabutRepositoryTest {

  val _noDefaultConstructorEntities: ListBuffer[NoDefaultConstructorEntity] = ListBuffer()
  var _entityTierOneTypes: ListBuffer[EntityTierOneType] = ListBuffer()
  var _entityTierTwoTypes: ListBuffer[EntityTierTwoType] = ListBuffer()

  private var _fabutRepositoryAssert: FabutRepositoryAssert = null

  def fabutRepositoryAssert(): FabutRepositoryAssert = _fabutRepositoryAssert

  @Before
  override def beforeTest(): Unit = _fabutRepositoryAssert = new FabutRepositoryAssert(this, AssertType.REPOSITORY_ASSERT)

  @After
  override def afterTest(): Unit = ()

  override def entityTypes(): List[Type] = {

    val entityTypes: ListBuffer[Type] = ListBuffer()
    entityTypes += typeOf[EntityTierOneType]
    entityTypes += typeOf[EntityTierTwoType]
    entityTypes += typeOf[EntityTierThreeType]
    entityTypes += typeOf[NoDefaultConstructorEntity]

    entityTypes.toList
  }

  override def findAll(clazz: Type) =
    if (typeOf[EntityTierOneType] == clazz) {
      _entityTierOneTypes.toList
    } else if (typeOf[EntityTierTwoType] == clazz) {
      _entityTierTwoTypes.toList
    } else {
      _noDefaultConstructorEntities.toList
    }


  override def findById(entityClass: Type, id: Any) =
    if (typeOf[EntityTierOneType] == entityClass) {
      _entityTierOneTypes.find(entity => entity.id == id).orNull
    } else if (typeOf[EntityTierTwoType] == entityClass) {
      _entityTierTwoTypes.find(entity => entity.id == id).orNull
    } else {
      _noDefaultConstructorEntities.find(entity => entity.id == id).orNull
    }


  override def complexTypes(): List[Type] = {

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
    complexTypes += typeOf[FakeEntity]


    complexTypes.toList
  }

  override def ignoredTypes(): List[Type] = {
    val ignoredTypes: ListBuffer[Type] = ListBuffer()
    ignoredTypes += typeOf[IgnoredType]

    ignoredTypes.toList
  }

  override def customAssertEquals(expectedObject: Any, actualObject: Any): Unit = Assert.assertEquals(expectedObject, actualObject)

  def setEntityTierOneTypes(entityTierOneTypes1: ListBuffer[EntityTierOneType]): Unit = _entityTierOneTypes = entityTierOneTypes1

  def setEntityTierTwoTypes(entityTierTwoTypes1: ListBuffer[EntityTierTwoType]): Unit = _entityTierTwoTypes = entityTierTwoTypes1


}