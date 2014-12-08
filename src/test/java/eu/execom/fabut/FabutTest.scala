package eu.execom.fabut
import scala.reflect.runtime.universe.{ Type, typeOf }
import org.junit.Before
import eu.execom.fabut.util.ReflectionUtil._
import eu.execom.fabut.enums.AssertableType._
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import org.junit.Assert._
import org.junit.Test
import eu.execom.fabut.model.Person
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.model.ObjectWithComplexProperty
import eu.execom.fabut.model.CopyCaseClass
import eu.execom.fabut.model.ObjectWithSimpleProperties
import eu.execom.fabut.model.ObjectWithMap
import eu.execom.fabut.model.ObjectWithSimpleList
import eu.execom.fabut.model.ObjectWithSimpleMap
import eu.execom.fabut.Fabut._
import eu.execom.fabut.model.EntityTierOneType
import scala.collection.mutable.WrappedArray
import eu.execom.fabut.property.IgnoredProperty
import org.junit.Ignore
import eu.execom.fabut.property.NullProperty
import eu.execom.fabut.property.NotNullProperty
import eu.execom.fabut.property.NullProperty
import eu.execom.fabut.property.NotNullProperty
import eu.execom.fabut.property.IgnoredProperty
import eu.execom.fabut.property.Property
import junit.framework.AssertionFailedError
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.NoDefaultConstructorEntity
import scala.collection.mutable.ListBuffer
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.TierOneTypeDuplicate
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.TierOneType

class FabutTest extends AbstractFabutRepositoryAssertTest {

  val TEST = "test"
  val TEST_ID = 105
  val PROPERTY = "_property"

  @Test
  def testIgnoredVarArgs = {
    //    setup
    val properties = Seq(EntityTierOneType.PROPERTY, EntityTierOneType.ID)

    //    method
    val multiProperties = Fabut.ignored(properties)

    //    assert
    assertEquals(properties.size, multiProperties.size)

    (properties, multiProperties).zipped.foreach {
      (property, multiProperty) =>
        {
          assertTrue(multiProperty.isInstanceOf[IgnoredProperty])
          assertEquals(property, multiProperty.asInstanceOf[IgnoredProperty].getPath)
        }
    }
  }

  @Test
  def testNullVarArgs = {
    //    setup
    val properties = Seq(EntityTierOneType.PROPERTY, EntityTierOneType.ID)

    //    method
    val multiProperties = Fabut.isNull(properties)

    //    assert
    assertEquals(properties.size, multiProperties.size)

    (properties, multiProperties).zipped.foreach {
      (property, multiProperty) =>
        {
          assertTrue(multiProperty.isInstanceOf[NullProperty])
          assertEquals(property, multiProperty.asInstanceOf[NullProperty].getPath)
        }
    }
  }

  @Test
  def testNotNullVarArgs = {
    //    setup
    val properties = Seq(EntityTierOneType.PROPERTY, EntityTierOneType.ID)

    //    method
    val multiProperties = Fabut.notNull(properties)

    //    assert
    assertEquals(properties.size, multiProperties.size)

    (properties, multiProperties).zipped.foreach {
      (property, multiProperty) =>
        {
          assertTrue(multiProperty.isInstanceOf[NotNullProperty])
          assertEquals(property, multiProperty.asInstanceOf[NotNullProperty].getPath)
        }
    }
  }

  @Test
  def testNull = {
    //    setup
    val property = EntityTierOneType.PROPERTY

    //    method
    val nullProperty = Fabut.isNull(property)

    //    assert
    assertTrue(nullProperty.isInstanceOf[NullProperty])
    assertEquals(property, nullProperty.path)
  }

  @Test
  def testNotNull = {
    //    setup
    val property = EntityTierOneType.PROPERTY

    //    method
    val notNullProperty = Fabut.notNull(property)

    //    assert
    assertTrue(notNullProperty.isInstanceOf[NotNullProperty])
    assertEquals(property, notNullProperty.path)
  }

  @Test
  def testIgnored = {
    //    setup
    val property = EntityTierOneType.PROPERTY

    //    method
    val ignoredProperty = Fabut.ignored(property)

    //    assert
    assertTrue(ignoredProperty.isInstanceOf[IgnoredProperty])
    assertEquals(property, ignoredProperty.path)
  }

  @Test
  def testValue = {
    //    setup
    val property = EntityTierOneType.PROPERTY

    //    method
    val valueProperty: Property = Fabut.value(EntityTierOneType.PROPERTY, TEST)

    //    assert
    assertTrue(valueProperty.isInstanceOf[Property])
    assertEquals(valueProperty.path, EntityTierOneType.PROPERTY)
    assertEquals(valueProperty.value, TEST)
  }

  /** SNAPSHOT and stuff assert*/
  @Test(expected = classOf[IllegalStateException])
  def testBeforeTest {
    //    method
    Fabut.beforeTest(new Object)
  }

  @Test
  def testAfterTestSuccess {
    //    setup
    Fabut.beforeTest(this)
    Fabut.takeSnapshot()

    //    method
    Fabut.afterTest
  }

  @Test(expected = classOf[AssertionFailedError])
  def testAfterTestFail {
    //    setup
    Fabut.beforeTest(this)
    Fabut.takeSnapshot()
    entityTierOneTypes += new EntityTierOneType(TEST, TEST_ID)

    //    method
    Fabut.afterTest
  }

  @Test(expected = classOf[AssertionFailedError])
  def testTakeSnapshotFail {
    //    setup
    Fabut.beforeTest(this)
    noDefaultConstructorEntities += new NoDefaultConstructorEntity(TEST_ID, TEST)

    //    setup
    Fabut.takeSnapshot()
  }

  @Test
  def testTakeSnapshotSuccess {
    //    setup
    Fabut.beforeTest(this)
    entityTierOneTypes += new EntityTierOneType(TEST, TEST_ID)
    Fabut.takeSnapshot()

    //    method
    Fabut.afterTest
  }

  @Test
  def testAssertObjectWithComplexType {
    //    setup
    Fabut.beforeTest(this)
    val tierOneType = new TierOneType(TEST)
    takeSnapshot()

    //    method
    Fabut.assertObject(tierOneType, value(PROPERTY, TEST))
    Fabut.afterTest
  }

  @Test
  def testAssertObjectWithEntityTypeUsualCase {
    //    setup
    Fabut.beforeTest(this)
    val entityTierOneType = new EntityTierOneType(TEST, TEST_ID)

    //    method
    Fabut.takeSnapshot()
    entityTierOneTypes += entityTierOneType
    Fabut.assertObject(entityTierOneType, value(PROPERTY, TEST), notNull("_id"))

    Fabut.afterTest

  }

  @Test
  def testAssertObjectWithEntityType {
    //    setup
    val entity = new EntityTierOneType(TEST, 1)
    Fabut.beforeTest(this)

    //    method
    Fabut.takeSnapshot()
    entityTierOneTypes ++= List(entity)

    Fabut.assertObject(entity, value(EntityTierOneType.ID, 1), value(EntityTierOneType.PROPERTY, TEST))

    Fabut.afterTest
  }

  @Test(expected = classOf[AssertionError])
  def testAssertObjectWithEntityTypeFail {
    //    setup
    val entity = new EntityTierOneType(TEST, 1)
    Fabut.beforeTest(this)

    //    method
    Fabut.takeSnapshot()
    entityTierOneTypes ++= List(entity)

    Fabut.assertObject(entity, value(EntityTierOneType.ID, 1), value(EntityTierOneType.PROPERTY, "fail"))

    Fabut.afterTest
  }

  @Test
  def testAssertObjectsComplexSuccess {
    //    setup
    Fabut.beforeTest(this)
    val expected = new TierOneType(TEST)
    val actual = new TierOneType(TEST)

    //    method
    Fabut.assertObjects(expected, actual)
    Fabut.afterTest
  }

  @Test(expected = classOf[AssertionError])
  def testAssertObjectsComplexFail {
    //    setup
    Fabut.beforeTest(this)
    val expected = new TierOneType(TEST)
    val actual = new TierOneType(TEST + TEST)

    //    method
    Fabut.assertObjects(expected, actual)
    Fabut.afterTest
  }

  @Test
  def testAssertObjectsComplexWithPropertyDifference {
    //    setup
    Fabut.beforeTest(this)
    val expected = new TierOneType(TEST)
    val actual = new TierOneType(TEST + TEST)

    //    method
    Fabut.assertObjects(expected, actual, value(PROPERTY, TEST + TEST))
    Fabut.afterTest
  }

  @Test(expected = classOf[AssertionFailedError])
  def testMarkAssertedFail {
    //    setup
    Fabut.beforeTest(this)
    val entityTierOneType = new EntityTierOneType(TEST, null)
    Fabut.takeSnapshot()
    entityTierOneTypes += entityTierOneType

    //    method
    Fabut.markAsserted(entityTierOneType)

    Fabut.afterTest

  }

  @Test(expected = classOf[IllegalStateException])
  def testMarkAssertedNotEntity {
    // setup
    Fabut.beforeTest(this)
    val entity = new TierOneType
    Fabut.takeSnapshot()

    // method
    Fabut.markAsserted(entity);

    Fabut.afterTest
  }

  @Test
  def testAssertObjectsEntitySuccess {
    //    setup
    Fabut.beforeTest(this)
    val expected = new EntityTierOneType(TEST, 1)
    val actual = new EntityTierOneType(TEST, 1)

    //    method
    Fabut.takeSnapshot()
    entityTierOneTypes += actual
    Fabut.assertObjects(expected, actual)

    Fabut.afterTest
  }

  @Test(expected = classOf[AssertionError])
  def testAssertObjectsEntityFail {
    //    setup
    Fabut.beforeTest(this)
    val expected = new EntityTierOneType(TEST, 1)
    val actual = new EntityTierOneType(TEST + TEST, 1)

    //    method
    Fabut.takeSnapshot()
    entityTierOneTypes += actual
    Fabut.assertObjects(expected, actual)

    Fabut.afterTest
  }

  @Test
  def testAssertEntityWithSnapshotSuccess {
    //    setup
    Fabut.beforeTest(this)
    val entityTierOneType = new EntityTierOneType(TEST, 10)
    entityTierOneTypes += entityTierOneType
    Fabut.takeSnapshot()

    //    method
    entityTierOneTypes.head._property = (TEST + TEST)

    Fabut.assertEntityWithSnapshot(entityTierOneType, Fabut.value(PROPERTY, TEST + TEST))

    Fabut.afterTest
  }

  @Test(expected = classOf[AssertionFailedError])
  def testAssertEntityWithSnapshotFail {
    //    setup
    Fabut.beforeTest(this)
    val entityTierOneType = new EntityTierOneType(TEST, 10)
    entityTierOneTypes += entityTierOneType
    Fabut.takeSnapshot()

    //    method
    entityTierOneTypes.head._property = (TEST + TEST)
    Fabut.assertEntityWithSnapshot(entityTierOneType, Fabut.value(PROPERTY, TEST + TEST + TEST))

    Fabut.afterTest
  }

  @Test(expected = classOf[IllegalStateException])
  def testAssertEntityWithSnapshotNotEntity {
    //    setup
    Fabut.beforeTest(this)
    Fabut.takeSnapshot()

    //    method
    Fabut.assertEntityWithSnapshot(new TierOneType)
  }

  @Test(expected = classOf[NullPointerException])
  def testAssertEntityWithSnapshotNullEntity {
    //    setup
    Fabut.beforeTest(this)
    val entityTierOneType = new EntityTierOneType(TEST, 10)
    entityTierOneTypes += entityTierOneType
    Fabut.takeSnapshot()

    //    method
    entityTierOneTypes.head._property = (TEST + TEST)

    Fabut.assertEntityWithSnapshot(null, Fabut.value(PROPERTY, TEST + TEST))

    Fabut.afterTest
  }

  @Test
  def testMarkAssertedSuccess {
    // setup
    Fabut.beforeTest(this)
    val entity = new EntityTierOneType(TEST, 1)
    Fabut.takeSnapshot()
    entityTierOneTypes ++= List(entity)

    // method
    Fabut.markAsserted(entity);

    Fabut.afterTest
  }

  @Test
  def testAssertEntityAsDeletedSuccess {
    // setup
    Fabut.beforeTest(this)
    val entity = new EntityTierOneType(TEST, 1)
    entityTierOneTypes ++= List(entity)

    Fabut.takeSnapshot()

    // method
    entityTierOneTypes = ListBuffer()
    Fabut.assertEntityAsDeleted(entity)

    Fabut.afterTest
  }

  @Test(expected = classOf[AssertionFailedError])
  def testAssertEntityAsDeletedFail {
    // setup
    Fabut.beforeTest(this)
    val entity = new EntityTierOneType(TEST, 1)
    entityTierOneTypes ++= List(entity)
    Fabut.takeSnapshot()

    // method
    Fabut.assertEntityAsDeleted(entity)

    Fabut.afterTest
  }

  @Test(expected = classOf[IllegalStateException])
  def testAssertEntityAsDeletedNotEntity {
    // setup
    Fabut.beforeTest(this)
    val entity = new TierOneType(TEST)
    Fabut.takeSnapshot()

    // method
    Fabut.assertEntityAsDeleted(entity)

    Fabut.afterTest
  }

  @Test
  def testIgnoreEntitySuccess {
    // setup
    Fabut.beforeTest(this)
    val entity = new EntityTierOneType(TEST, 1)
    Fabut.takeSnapshot()

    // method
    Fabut.ignoreEntity(entity)

    Fabut.afterTest
  }

  @Test(expected = classOf[IllegalStateException])
  def testIgnoreEntityNotEntity {
    Fabut.beforeTest(this)
    val entity = new TierOneType(TEST)
    Fabut.takeSnapshot()

    // method
    Fabut.ignoreEntity(entity)

    Fabut.afterTest
  }

  @Test(expected = classOf[AssertionFailedError])
  def testIgnoreEntityFail {
    // setup
    Fabut.beforeTest(this)
    val entity = new EntityTierOneType(TEST, null)
    Fabut.takeSnapshot()
    entityTierOneTypes ++= List(entity)

    // method
    Fabut.ignoreEntity(entity)

    Fabut.afterTest
  }

  //integracioni 

  @Test(expected = classOf[AssertionFailedError])
  def testAssertObjectMapsFail {
    //    setup
    Fabut.beforeTest(this)
    val expected = Map("first" -> new TierOneType(TEST), "second" -> new TierOneType(TEST), "third2" -> new TierOneType(TEST))
    val actual = Map("first" -> new TierOneType(TEST), "second" -> new TierOneType(TEST), "third1" -> new TierOneType(TEST))

    Fabut.takeSnapshot()

    //    method
    Fabut.assertObjects(expected, actual)

    Fabut.afterTest

  }

}