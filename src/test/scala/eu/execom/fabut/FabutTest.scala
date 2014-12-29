package eu.execom.fabut

import eu.execom.fabut.Fabut._
import eu.execom.fabut.model.test.{Address, Faculty, Student, Teacher}
import eu.execom.fabut.model.{EntityTierOneType, NoDefaultConstructorEntity, TierOneType}
import eu.execom.fabut.property.{IgnoredProperty, NotNullProperty, NullProperty, Property}
import junit.framework.AssertionFailedError
import org.junit.Assert._
import org.junit.Test

import scala.collection.mutable.ListBuffer

class FabutTest extends AbstractFabutRepositoryAssertTest {

  val TEST = "test"
  val TEST_ID = 105
  val PROPERTY = "property"

  @Test
  def testIgnoredVarArgs() = {
    //    setup
    val properties = Seq(EntityTierOneType.PROPERTY, EntityTierOneType.ID)

    //    method
    val multiProperties = Fabut.ignored(properties)

    //    assert
    assertEquals(properties.size, multiProperties.size)

    (properties, multiProperties).zipped.foreach {
      (property, multiProperty) => {
        assertTrue(multiProperty.isInstanceOf[IgnoredProperty])
        assertEquals(property, multiProperty.path)
      }
    }
  }

  @Test
  def testNullVarArgs() = {
    //    setup
    val properties = Seq(EntityTierOneType.PROPERTY, EntityTierOneType.ID)

    //    method
    val multiProperties = Fabut.isNull(properties)

    //    assert
    assertEquals(properties.size, multiProperties.size)

    (properties, multiProperties).zipped.foreach {
      (property, multiProperty) => {
        assertTrue(multiProperty.isInstanceOf[NullProperty])
        assertEquals(property, multiProperty.path)
      }
    }
  }

  @Test
  def testNotNullVarArgs() = {
    //    setup
    val properties = Seq(EntityTierOneType.PROPERTY, EntityTierOneType.ID)

    //    method
    val multiProperties = Fabut.notNull(properties)

    //    assert
    assertEquals(properties.size, multiProperties.size)

    (properties, multiProperties).zipped.foreach {
      (property, multiProperty) => {
        assertTrue(multiProperty.isInstanceOf[NotNullProperty])
        assertEquals(property, multiProperty.path)
      }
    }
  }

  @Test
  def testNull() = {
    //    setup
    val property = EntityTierOneType.PROPERTY

    //    method
    val nullProperty = Fabut.isNull(property)

    //    assert
    assertTrue(nullProperty.isInstanceOf[NullProperty])
    assertEquals(property, nullProperty.path)
  }

  @Test
  def testNotNull() = {
    //    setup
    val property = EntityTierOneType.PROPERTY

    //    method
    val notNullProperty = Fabut.notNull(property)

    //    assert
    assertTrue(notNullProperty.isInstanceOf[NotNullProperty])
    assertEquals(property, notNullProperty.path)
  }

  @Test
  def testIgnored() = {
    //    setup
    val property = EntityTierOneType.PROPERTY

    //    method
    val ignoredProperty = Fabut.ignored(property)

    //    assert
    assertTrue(ignoredProperty.isInstanceOf[IgnoredProperty])
    assertEquals(property, ignoredProperty.path)
  }

  @Test
  def testValue() = {
    //    method
    val valueProperty: Property = Fabut.value(EntityTierOneType.PROPERTY, TEST)

    //    assert
    assertTrue(valueProperty.isInstanceOf[Property])
    assertEquals(valueProperty.path, EntityTierOneType.PROPERTY)
    assertEquals(valueProperty.value, TEST)
  }

  /** SNAPSHOT and stuff assert */
  @Test(expected = classOf[IllegalStateException])
  def testBeforeTest() = {
    //    method
    Fabut.beforeTest(new Object)
  }

  @Test
  def testAfterTestSuccess() = {
    //    setup
    Fabut.beforeTest(this)
    Fabut.takeSnapshot()

    //    method
    Fabut.afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testAfterTestFail() = {
    //    setup
    Fabut.beforeTest(this)
    Fabut.takeSnapshot()
    _entityTierOneTypes += new EntityTierOneType(TEST, TEST_ID)

    //    method
    Fabut.afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testTakeSnapshotFail() = {
    //    setup
    Fabut.beforeTest(this)
    _noDefaultConstructorEntities += new NoDefaultConstructorEntity(TEST_ID, TEST)

    //    setup
    Fabut.takeSnapshot()
  }

  @Test
  def testTakeSnapshotSuccess() = {
    //    setup
    Fabut.beforeTest(this)
    _entityTierOneTypes += new EntityTierOneType(TEST, TEST_ID)
    Fabut.takeSnapshot()

    //    method
    Fabut.afterTest()
  }

  @Test
  def testAssertObjectWithComplexType() = {
    //    setup
    Fabut.beforeTest(this)
    val tierOneType = new TierOneType(TEST)
    takeSnapshot()

    //    method
    Fabut.assertObject(tierOneType, value(PROPERTY, TEST))
    Fabut.afterTest()
  }

  @Test
  def testAssertObjectWithEntityTypeUsualCase(): Unit = {
    //    setup
    Fabut.beforeTest(this)
    val entityTierOneType = new EntityTierOneType(TEST, TEST_ID)

    //    method
    Fabut.takeSnapshot()
    _entityTierOneTypes += entityTierOneType
    Fabut.assertObject(entityTierOneType, value(PROPERTY, TEST), notNull("id"))

    Fabut.afterTest()

  }

  @Test
  def testAssertObjectWithEntityType(): Unit = {
    //    setup
    val entity = new EntityTierOneType(TEST, 1)
    Fabut.beforeTest(this)

    //    method
    Fabut.takeSnapshot()
    _entityTierOneTypes ++= List(entity)

    Fabut.assertObject(entity, value(EntityTierOneType.ID, 1), value(EntityTierOneType.PROPERTY, TEST))

    Fabut.afterTest()
  }

  @Test(expected = classOf[AssertionError])
  def testAssertObjectWithEntityTypeFail(): Unit = {
    //    setup
    val entity = new EntityTierOneType(TEST, 1)
    Fabut.beforeTest(this)

    //    method
    Fabut.takeSnapshot()
    _entityTierOneTypes ++= List(entity)

    Fabut.assertObject(entity, value(EntityTierOneType.ID, 1), value(EntityTierOneType.PROPERTY, "fail"))

    Fabut.afterTest()
  }

  @Test
  def testAssertObjectsComplexSuccess(): Unit = {
    //    setup
    Fabut.beforeTest(this)
    val expected = new TierOneType(TEST)
    val actual = new TierOneType(TEST)

    //    method
    Fabut.assertObjects(expected, actual)
    Fabut.afterTest()
  }

  @Test(expected = classOf[AssertionError])
  def testAssertObjectsComplexFail(): Unit = {
    //    setup
    Fabut.beforeTest(this)
    val expected = new TierOneType(TEST)
    val actual = new TierOneType(TEST + TEST)

    //    method
    Fabut.assertObjects(expected, actual)
    Fabut.afterTest()
  }

  @Test
  def testAssertObjectsComplexWithPropertyDifference(): Unit = {
    //    setup
    Fabut.beforeTest(this)
    val expected = new TierOneType(TEST)
    val actual = new TierOneType(TEST + TEST)

    //    method
    Fabut.assertObjects(expected, actual, value(PROPERTY, TEST + TEST))
    Fabut.afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testMarkAssertedFail(): Unit = {
    //    setup
    Fabut.beforeTest(this)
    val entityTierOneType = new EntityTierOneType(TEST, null)
    Fabut.takeSnapshot()
    _entityTierOneTypes += entityTierOneType

    //    method
    Fabut.markAsserted(entityTierOneType)

    Fabut.afterTest()

  }

  @Test(expected = classOf[IllegalStateException])
  def testMarkAssertedNotEntity(): Unit = {
    // setup
    Fabut.beforeTest(this)
    val entity = new TierOneType
    Fabut.takeSnapshot()

    // method
    Fabut.markAsserted(entity)

    Fabut.afterTest()
  }

  @Test
  def testAssertObjectsEntitySuccess(): Unit = {
    //    setup
    Fabut.beforeTest(this)
    val expected = new EntityTierOneType(TEST, 1)
    val actual = new EntityTierOneType(TEST, 1)

    //    method
    Fabut.takeSnapshot()
    _entityTierOneTypes += actual
    Fabut.assertObjects(expected, actual)

    Fabut.afterTest()
  }

  @Test(expected = classOf[AssertionError])
  def testAssertObjectsEntityFail(): Unit = {
    //    setup
    Fabut.beforeTest(this)
    val expected = new EntityTierOneType(TEST, 1)
    val actual = new EntityTierOneType(TEST + TEST, 1)

    //    method
    Fabut.takeSnapshot()
    _entityTierOneTypes += actual
    Fabut.assertObjects(expected, actual)

    Fabut.afterTest()
  }

  @Test
  def testAssertEntityWithSnapshotSuccess(): Unit = {
    //    setup
    Fabut.beforeTest(this)
    val entityTierOneType = new EntityTierOneType(TEST, 10)
    _entityTierOneTypes += entityTierOneType
    Fabut.takeSnapshot()

    //    method
    _entityTierOneTypes.head.property_=(TEST + TEST)

    Fabut.assertEntityWithSnapshot(entityTierOneType, Fabut.value(PROPERTY, TEST + TEST))

    Fabut.afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testAssertEntityWithSnapshotFail(): Unit = {
    //    setup
    Fabut.beforeTest(this)
    val entityTierOneType = new EntityTierOneType(TEST, 10)
    _entityTierOneTypes += entityTierOneType
    Fabut.takeSnapshot()

    //    method
    _entityTierOneTypes.head.property_=(TEST + TEST)
    Fabut.assertEntityWithSnapshot(entityTierOneType, Fabut.value(PROPERTY, TEST + TEST + TEST))

    Fabut.afterTest()
  }

  @Test(expected = classOf[IllegalStateException])
  def testAssertEntityWithSnapshotNotEntity(): Unit = {
    //    setup
    Fabut.beforeTest(this)
    Fabut.takeSnapshot()

    //    method
    Fabut.assertEntityWithSnapshot(new TierOneType)
  }

  @Test(expected = classOf[NullPointerException])
  def testAssertEntityWithSnapshotNullEntity(): Unit = {
    //    setup
    Fabut.beforeTest(this)
    val entityTierOneType = new EntityTierOneType(TEST, 10)
    _entityTierOneTypes += entityTierOneType
    Fabut.takeSnapshot()

    //    method
    _entityTierOneTypes.head.property_=(TEST + TEST)

    Fabut.assertEntityWithSnapshot(null, Fabut.value(PROPERTY, TEST + TEST))

    Fabut.afterTest()
  }

  @Test
  def testMarkAssertedSuccess(): Unit = {
    // setup
    Fabut.beforeTest(this)
    val entity = new EntityTierOneType(TEST, 1)
    Fabut.takeSnapshot()
    _entityTierOneTypes ++= List(entity)

    // method
    Fabut.markAsserted(entity)

    Fabut.afterTest()
  }

  @Test
  def testAssertEntityAsDeletedSuccess(): Unit = {
    // setup
    Fabut.beforeTest(this)
    val entity = new EntityTierOneType(TEST, 1)
    _entityTierOneTypes ++= List(entity)

    Fabut.takeSnapshot()

    // method
    _entityTierOneTypes = ListBuffer()
    Fabut.assertEntityAsDeleted(entity)

    Fabut.afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testAssertEntityAsDeletedFail(): Unit = {
    // setup
    Fabut.beforeTest(this)
    val entity = new EntityTierOneType(TEST, 1)
    _entityTierOneTypes ++= List(entity)
    Fabut.takeSnapshot()

    // method
    Fabut.assertEntityAsDeleted(entity)

    Fabut.afterTest()
  }

  @Test(expected = classOf[IllegalStateException])
  def testAssertEntityAsDeletedNotEntity(): Unit = {
    // setup
    Fabut.beforeTest(this)
    val entity = new TierOneType(TEST)
    Fabut.takeSnapshot()

    // method
    Fabut.assertEntityAsDeleted(entity)

    Fabut.afterTest()
  }

  @Test
  def testIgnoreEntitySuccess(): Unit = {
    // setup
    Fabut.beforeTest(this)
    val entity = new EntityTierOneType(TEST, 1)
    Fabut.takeSnapshot()

    // method
    Fabut.ignoreEntity(entity)

    Fabut.afterTest()
  }

  @Test(expected = classOf[IllegalStateException])
  def testIgnoreEntityNotEntity(): Unit = {
    Fabut.beforeTest(this)
    val entity = new TierOneType(TEST)
    Fabut.takeSnapshot()

    // method
    Fabut.ignoreEntity(entity)

    Fabut.afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testIgnoreEntityFail(): Unit = {
    // setup
    Fabut.beforeTest(this)

    val entity = new EntityTierOneType(TEST, null)
    Fabut.takeSnapshot()
    _entityTierOneTypes ++= List(entity)

    // method
    Fabut.ignoreEntity(entity)

    Fabut.afterTest()
  }

  //integracioni
  @Test
  def testAssertObject(): Unit = {
    //	  method
    Fabut.beforeTest(this)

    val student = new Student()
    student.name = "Branko"
    student.lastName = "Gvoka"
    student.address = new Address()
    student.address.city = "Ruma"
    student.address.street = "15. Maja"
    student.address.streetNumber = "97"

    val teacher = new Teacher()
    teacher.name = "Ilija"
    teacher.student = student
    teacher.address = new Address()
    teacher.address.city = "Novi Sad"
    teacher.address.street = "Puskinova"
    teacher.address.streetNumber = "22"

    val faculty = new Faculty("FTN", teacher)
    student.faculty = faculty

    Fabut.takeSnapshot()

    //    assert
    Fabut.assertObject("", student, Fabut.value("name", "Branko"),
      Fabut.value("lastName", "Gvoka"),
      Fabut.value("address.city", "Ruma"), Fabut.value("address.street", "15. Maja"),
      Fabut.value("address.streetNumber", "97"), Fabut.value("faculty.name", "FTN"),
      Fabut.value("faculty.teacher.name", "Ilija"), Fabut.value("faculty.teacher.address.city", "Novi Sad"),
      Fabut.value("faculty.teacher.address.street", "Puskinova"),
      Fabut.value("faculty.teacher.student", student),
      Fabut.value("faculty.teacher.address.streetNumber", "22"))

    Fabut.afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testAssertObjectMapsFail(): Unit = {
    //    setup
    Fabut.beforeTest(this)
    val expected = Map("first" -> new TierOneType(TEST), "second" -> new TierOneType(TEST), "third2" -> new TierOneType(TEST))
    val actual = Map("first" -> new TierOneType(TEST), "second" -> new TierOneType(TEST), "third1" -> new TierOneType(TEST))

    Fabut.takeSnapshot()

    //    method
    Fabut.assertObjects(expected, actual)

    Fabut.afterTest()

  }

}