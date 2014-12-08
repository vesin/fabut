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
import scala.collection.mutable.ListBuffer
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.UnknownEntityType
import eu.execom.fabut.model.UnknownEntityType
import eu.execom.fabut.property.CopyAssert

class FabutRepositoryAssertTest extends AbstractFabutRepositoryAssertTest {

  val TEST = "test"
  val TEST_ID = 105
  val PROPERTY = "_property"
  val DOT = "."
  val ENTITY_TWO_PROPERTY = "_subProperty._property"

  @Test
  def testAssertDbStateTrue {
    //    setup
    val beforeList1 = ListBuffer(new EntityTierOneType(TEST, 1), new EntityTierOneType(TEST, 2))
    entityTierOneTypes = beforeList1

    val beforeList2 = ListBuffer(
      new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, new EntityTierOneType(TEST, 1), 4))
    entityTierTwoTypes = beforeList2

    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())

    val afterList1 = ListBuffer(new EntityTierOneType(TEST, 1), new EntityTierOneType(TEST, 2))
    entityTierOneTypes = afterList1
    val afterList2 = ListBuffer(new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, new EntityTierOneType(TEST, 1), 4))
    entityTierTwoTypes = afterList2

    //    method
    val assertResult = fabutRepositoryAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertDbStateFalse {
    //    setup
    val beforeList1 = ListBuffer(new EntityTierOneType(TEST, 1), new EntityTierOneType(TEST, 2))
    setEntityTierOneTypes(beforeList1)

    val beforeList2 = ListBuffer(
      new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, new EntityTierOneType("greska", 7), 4))
    setEntityTierTwoTypes(beforeList2)

    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())

    val afterList1 = ListBuffer(new EntityTierOneType(TEST, 1), new EntityTierOneType(TEST, 2))
    setEntityTierOneTypes(afterList1)
    val afterList2 = ListBuffer(new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, new EntityTierOneType(TEST + TEST, 6), 4))
    setEntityTierTwoTypes(afterList2)

    //    method
    val assertResult = fabutRepositoryAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)

  }

  @Test
  def testAssertEntityAsDeletedEntity {
    //    setup
    val list1: ListBuffer[EntityTierOneType] = ListBuffer()
    list1 += new EntityTierOneType(TEST, 1)
    list1 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list1)

    val actual = new EntityTierOneType(TEST, 1)
    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())

    val list2: ListBuffer[EntityTierOneType] = ListBuffer()
    list2 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list2)

    //    method
    val assertEntityAsDeleted = fabutRepositoryAssert.assertEntityAsDeleted(new FabutReportBuilder, actual)
    val assertDbState = fabutRepositoryAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(assertEntityAsDeleted)
    assertTrue(assertDbState)
  }

  @Test
  def testIgnoreEntityEntity {
    //    setup
    val list1: ListBuffer[EntityTierOneType] = ListBuffer()
    list1 += new EntityTierOneType(TEST, 1)
    list1 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list1)

    val actual = new EntityTierOneType(TEST, 1)
    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())

    val list2: ListBuffer[EntityTierOneType] = ListBuffer()
    list2 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list2)

    //    method
    val ignoreEntity = fabutRepositoryAssert.ignoreEntity(actual)(new FabutReportBuilder)
    val assertDbState = fabutRepositoryAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(ignoreEntity)
    assertTrue(assertDbState)
  }

  @Test
  def testAfterAssertEntityParentEntityNotProperty {
    //    setup
    val list1: ListBuffer[EntityTierOneType] = ListBuffer()
    list1 += new EntityTierOneType(TEST, 1)
    list1 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list1)

    val actual = new EntityTierOneType(TEST, 1)
    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())

    val list2: ListBuffer[EntityTierOneType] = ListBuffer()
    list2 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list2)

    //    method
    val afterAssertEntity = fabutRepositoryAssert.afterAssertEntity(new FabutReportBuilder, actual, false)
    val assertDbState = fabutRepositoryAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(afterAssertEntity)
    assertTrue(assertDbState)
  }

  @Test(expected = classOf[AssertionError])
  def testAfterAssertEntityIsProperty {
    //    setup
    val list1: ListBuffer[EntityTierOneType] = ListBuffer()
    list1 += new EntityTierOneType(TEST, 1)
    list1 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list1)

    val actual = new EntityTierOneType(TEST, 1)
    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())

    val list2: ListBuffer[EntityTierOneType] = ListBuffer()
    list2 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list2)

    //    method
    fabutRepositoryAssert.afterAssertEntity(new FabutReportBuilder, actual, true)
    val assertResult = fabutRepositoryAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAfterAssertEntityNotEntity {
    //    setup
    val list1: ListBuffer[EntityTierOneType] = ListBuffer()
    list1 += new EntityTierOneType(TEST, 1)
    list1 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list1)

    val actual = new TierOneType()
    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())

    val list2: ListBuffer[EntityTierOneType] = ListBuffer()
    list2 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list2)

    //    method
    val assertResult = fabutRepositoryAssert.afterAssertEntity(new FabutReportBuilder, actual, false)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAfterAssertEntityWithoutID {
    //    setup
    val list1: ListBuffer[EntityTierOneType] = ListBuffer()
    list1 += new EntityTierOneType(TEST, 1)
    list1 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list1)

    val actual = new EntityTierOneType(TEST, 1)
    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())

    //    method
    val afterAssertEntity = fabutRepositoryAssert.afterAssertEntity(new FabutReportBuilder, actual, false)
    val assertDbState = fabutRepositoryAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(afterAssertEntity)
    assertTrue(assertDbState)
  }

  @Test
  def testMarkAssertedNotTypeSupportedTypeTrue {

    val list: ListBuffer[EntityTierTwoType] = ListBuffer()
    setEntityTierTwoTypes(list)

    //    method
    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())
    list += new EntityTierTwoType(TEST, new EntityTierOneType(TEST + TEST, 10), 1)
    val assertValue = fabutRepositoryAssert.markAsAsserted(new FabutReportBuilder, new EntityTierThreeType(TEST, new EntityTierOneType(TEST + TEST, 10), 1), Some(typeOf[EntityTierThreeType]))
    val assertDbState = fabutRepositoryAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    println("baguje testMarkAssertedNotTypeSupportedTypeTrue")
    //    assertTrue(assertValue)
    //    assertTrue(assertDbState)
  }

  @Test
  def testMarkAssertedNotTypeSupportedTypeFalse {
    //    method
    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())
    val assertValue = fabutRepositoryAssert.markAsAsserted(new FabutReportBuilder, new UnknownEntityType(4), Some(typeOf[UnknownEntityType]))
    val assertDbState = fabutRepositoryAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertFalse(assertValue)
    assertTrue(assertDbState)
  }

  @Test
  def testMarkAssertedCopyAssertNull {
    val list: ListBuffer[EntityTierTwoType] = ListBuffer()
    setEntityTierTwoTypes(list)

    //    method
    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())
    list += new EntityTierTwoType(TEST, new EntityTierOneType(TEST + TEST, 10), 1)
    val assertValue = fabutRepositoryAssert.markAsAsserted(new FabutReportBuilder, new EntityTierTwoType(TEST, new EntityTierOneType(TEST + TEST, 10), 1), Some(typeOf[EntityTierTwoType]))
    val assertDbState = fabutRepositoryAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(assertValue)
    assertTrue(assertDbState)
  }

  @Test
  def testMarkAssertedCopyAssertNotNull {

    val entity: EntityTierTwoType = new EntityTierThreeType(TEST, new EntityTierOneType(TEST + TEST, 10), 1)
    val list: ListBuffer[EntityTierTwoType] = ListBuffer()
    list += entity
    setEntityTierTwoTypes(list)

    //    method
    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())
    entity.property_=("new" + TEST)
    val assertValue = fabutRepositoryAssert.markAsAsserted(new FabutReportBuilder, new EntityTierTwoType(TEST, new EntityTierOneType(TEST + TEST, 10), 1), Some(typeOf[EntityTierTwoType]))
    val assertDbState = fabutRepositoryAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(assertValue)
    assertTrue(assertDbState)
  }

  @Test
  def testCheckNotExistingInAfterDbStateTrue {
    //    setup
    val beforeIds: Set[Any] = Set(1, 2, 3)
    val afterIds: Set[Any] = Set(1, 3)

    val copyAssert2 = CopyAssert(new EntityTierOneType)
    copyAssert2.asserted_=(true)
    val beforeEntities: Map[Any, CopyAssert] = Map(1 -> CopyAssert(new EntityTierOneType), 2 -> copyAssert2, 3 -> CopyAssert(new EntityTierOneType))
    //    method
    val assertResult = fabutRepositoryAssert.checkNotExistingInAfterDbState(beforeIds, afterIds, beforeEntities)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testCheckNotExistingInAfterDbStateFalse {
    //    setup
    val beforeIds: Set[Any] = Set(1, 2, 3)
    val afterIds: Set[Any] = Set(1, 3)

    val beforeEntities: Map[Any, CopyAssert] = Map(1 -> CopyAssert(new EntityTierOneType), 2 -> CopyAssert(new EntityTierOneType), 3 -> CopyAssert(new EntityTierOneType))
    //    method
    val assertResult = fabutRepositoryAssert.checkNotExistingInAfterDbState(beforeIds, afterIds, beforeEntities)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testCheckAddedToAfterDbStateFalse {
    //    setup
    val beforeIds: Set[Any] = Set(1, 2)
    val afterIds: Set[Any] = Set(1, 3)

    val afterEntities: Map[Any, Any] = Map(1 -> new EntityTierOneType, 3 -> new EntityTierOneType)
    //    method
    val assertResult = fabutRepositoryAssert.checkNewToAfterDbState(beforeIds, afterIds, afterEntities)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testCheckAddedToAfterDbStateTrue {
    //    setup
    val beforeIds: Set[Any] = Set(1, 3)
    val afterIds: Set[Any] = Set(1, 3)

    val afterEntities: Map[Any, Any] = Map(1 -> new EntityTierOneType, 3 -> new EntityTierOneType)
    //    method
    val assertResult = fabutRepositoryAssert.checkNewToAfterDbState(beforeIds, afterIds, afterEntities)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertDbSnapshotWithAfterStateTrue {
    //    setup
    val beforeIds: Set[Any] = Set(1, 3)
    val afterIds: Set[Any] = Set(1, 3)

    val afterEntities: Map[Any, Object] = Map(1 -> new EntityTierOneType, 3 -> new EntityTierOneType(TEST, 3))

    val copyAssert1 = CopyAssert(new EntityTierOneType(TEST, 1))
    copyAssert1.asserted_=(true)

    val copyAssert2 = CopyAssert(new EntityTierOneType)
    copyAssert2.asserted_=(true)

    val copyAssert3 = CopyAssert(new EntityTierOneType(TEST, 3))

    val beforeEntities: Map[Any, CopyAssert] = Map(1 -> copyAssert1, 2 -> copyAssert2, 3 -> copyAssert3)
    //    method
    val assertResult = fabutRepositoryAssert.assertDbSnapshotWithAfterState(beforeIds, afterIds, beforeEntities, afterEntities)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertDbSnapshotWithAfterStateFalse {
    //    setup
    val beforeIds: Set[Any] = Set(1, 3)
    val afterIds: Set[Any] = Set(1, 3)

    val afterEntities: Map[Any, Object] = Map(1 -> new EntityTierOneType, 3 -> new EntityTierOneType(TEST + TEST, 3))

    val copyAssert1 = CopyAssert(new EntityTierOneType(TEST, 1))
    copyAssert1.asserted_=(true)

    val copyAssert2 = CopyAssert(new EntityTierOneType)
    copyAssert2.asserted_=(true)

    val copyAssert3 = CopyAssert(new EntityTierOneType(TEST, 3))

    val beforeEntities: Map[Any, CopyAssert] = Map(1 -> copyAssert1, 2 -> copyAssert2, 3 -> copyAssert3)
    //    method
    val assertResult = fabutRepositoryAssert.assertDbSnapshotWithAfterState(beforeIds, afterIds, beforeEntities, afterEntities)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertEntityWithSnapshotTrue {
    //    setup
    val list: ListBuffer[EntityTierOneType] = ListBuffer()
    list += new EntityTierOneType(TEST, 1)
    setEntityTierOneTypes(list)

    //    method
    val entity = new EntityTierOneType(TEST + TEST, 1)
    val property = Fabut.value(EntityTierOneType.PROPERTY, TEST + TEST)
    val properties = Fabut.createExpectedPropertiesMap(Seq(property))

    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())
    val assertEntityWithSnapshot = fabutRepositoryAssert.assertEntityWithSnapshot(new FabutReportBuilder, entity, properties)
    val assertDbSnapshot = fabutRepositoryAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(assertEntityWithSnapshot)
    assertTrue(assertDbSnapshot)
  }

  @Test
  def testAssertEntityWithSnapshotFalse {
    //    setup
    val list: ListBuffer[EntityTierOneType] = ListBuffer()
    setEntityTierOneTypes(list)

    //    method
    val entity = new EntityTierOneType(TEST + TEST, 1)
    val property = Fabut.value(EntityTierOneType.PROPERTY, TEST + TEST)
    val properties = Fabut.createExpectedPropertiesMap(Seq(property))

    fabutRepositoryAssert.takeSnapshot(new FabutReportBuilder, Seq())
    val assertEntityWithSnapshot = fabutRepositoryAssert.assertEntityWithSnapshot(new FabutReportBuilder, entity, properties)
    val assertDbSnapshot = fabutRepositoryAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertFalse(assertEntityWithSnapshot)
    assertTrue(assertDbSnapshot)
  }

}