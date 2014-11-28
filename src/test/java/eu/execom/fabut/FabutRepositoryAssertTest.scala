package eu.execom.fabut

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.typeOf

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

import eu.execom.fabut.enums.AssertableType.ENTITY_TYPE
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.model.EntityTierOneType
import eu.execom.fabut.model.EntityTierThreeType
import eu.execom.fabut.model.EntityTierTwoType
import eu.execom.fabut.pair.AssertPair

class FabutRepositoryAssertTest extends AbstractFabutRepositoryAssertTest {

  val TEST = "test"
  val TEST_ID = 105
  val PROPERTY = "_property"
  val DOT = "."
  val ENTITY_TWO_PROPERTY = "_subProperty._property"

  @Test
  def testAssertDbStateTrue {
    //    setup
    val beforeList1 = List(new EntityTierOneType(TEST, 1), new EntityTierOneType(TEST, 2))
    entityTierOneTypes = beforeList1

    val beforeList2 = List(
      new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, new EntityTierOneType(TEST, 1), 4))
    entityTierTwoTypes = beforeList2

    fabutRepositoryAssert.takeSnapshot(new FabutReport)

    val afterList1 = List(new EntityTierOneType(TEST, 1), new EntityTierOneType(TEST, 2))
    entityTierOneTypes = afterList1
    val afterList2 = List(new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, new EntityTierOneType(TEST, 1), 4))
    entityTierTwoTypes = afterList2

    //    method
    val assertResult = fabutRepositoryAssert.assertDbSnapshot(new FabutReport)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertDbStateFalse {
    //    setup
    val beforeList1 = List(new EntityTierOneType(TEST, 1), new EntityTierOneType(TEST, 2))
    entityTierOneTypes = beforeList1

    val beforeList2 = List(
      new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, new EntityTierOneType(TEST, 7), 4))
    entityTierTwoTypes = beforeList2

    fabutRepositoryAssert.takeSnapshot(new FabutReport)

    val afterList1 = List(new EntityTierOneType(TEST, 1), new EntityTierOneType(TEST, 2))
    entityTierOneTypes = afterList1
    val afterList2 = List(new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, new EntityTierOneType(TEST + TEST, 7), 4))
    entityTierTwoTypes = afterList2

    //    method
    val assertResult = fabutRepositoryAssert.assertDbSnapshot(new FabutReport)

    //    assert
    assertFalse(assertResult)

  }

  @Test
  def testAfterAssertEntityWhenEntityNotProperty {
    //    setup
    val list1 = List(new EntityTierOneType(TEST, 1), new EntityTierOneType(TEST, 2))
    entityTierOneTypes = list1

    val actual = new EntityTierOneType(TEST, 1)

    val list2 = List(new EntityTierOneType(TEST, 2))
    entityTierOneTypes = list2
    //    method
    //    fabutRepositoryAssert.afterAssertEntity(new FabutReport, actual, true)
    //    val assertResult = fabutRepositoryAssert.after

  }
  @Test
  def testAssertPairCaseEntityType = {
    //    setup
    val assertPair = AssertPair(TEST, new EntityTierOneType(TEST, TEST_ID), new EntityTierOneType(TEST, TEST_ID), ENTITY_TYPE, false)
    //    method
    val assertResult = fabutRepositoryAssert.assertPair(assertPair, Map(), new NodesList)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPairCaseEntityTypeNotEqual = {

    val actual = new EntityTierOneType(TEST, TEST_ID)
    val expected = new EntityTierOneType(TEST, TEST_ID)
    //    setup
    val assertPair = AssertPair(TEST, actual, expected, ENTITY_TYPE, false)
    //    method
    val assertResult = fabutRepositoryAssert.assertPair(assertPair, Map(), new NodesList)(new FabutReport)
    //    assert
    assertFalse(assertResult)
  }

}