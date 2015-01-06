package eu.execom.fabut

import eu.execom.fabut.model.TrivialClasses._
import eu.execom.fabut.model._
import org.junit.{After, Assert, Before}

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe.{Type, typeOf}

class AbstractFabutObjectAssertTest extends Assert with Fabut {

  private var _fabutObjectAssert: FabutObjectAssert = null

  def fabutObjectAssert(): FabutObjectAssert = _fabutObjectAssert

  @Before
  override def before(): Unit = {
    _fabutObjectAssert = new FabutObjectAssert(this)
  }

  @After
  override def after(): Unit = {}

  override def complexTypes(): List[Type] = {
    val complexTypes: ListBuffer[Type] = ListBuffer()

    complexTypes += typeOf[ObjectWithSimpleProperties]
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
    complexTypes += typeOf[EntityTierOneType]
    complexTypes += typeOf[ListType]
    complexTypes += typeOf[A]
    complexTypes += typeOf[B]
    complexTypes += typeOf[C]
    complexTypes += typeOf[D]
    complexTypes += typeOf[E]
    complexTypes += typeOf[BadCopyClass]

    complexTypes.toList
  }

  override def ignoredTypes(): List[Type] = {
    val ignoredTypes: ListBuffer[Type] = ListBuffer()

    ignoredTypes += typeOf[IgnoredType]
    ignoredTypes.toList
  }

  override def customAssertEquals(expectedObject: Any, actualObject: Any): Unit = Assert.assertEquals(expectedObject, actualObject)
}