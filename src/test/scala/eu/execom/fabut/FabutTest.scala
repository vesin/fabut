package eu.execom.fabut

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
    val multiProperties = ignored(properties)

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
    val multiProperties = isNull(properties)

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
    val multiProperties = notNull(properties)

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
    val nullProperty = isNull(property)

    //    assert
    assertTrue(nullProperty.isInstanceOf[NullProperty])
    assertEquals(property, nullProperty.path)
  }

  @Test
  def testNotNull() = {
    //    setup
    val property = EntityTierOneType.PROPERTY

    //    method
    val notNullProperty = notNull(property)

    //    assert
    assertTrue(notNullProperty.isInstanceOf[NotNullProperty])
    assertEquals(property, notNullProperty.path)
  }

  @Test
  def testIgnored() = {
    //    setup
    val property = EntityTierOneType.PROPERTY

    //    method
    val ignoredProperty = ignored(property)

    //    assert
    assertTrue(ignoredProperty.isInstanceOf[IgnoredProperty])
    assertEquals(property, ignoredProperty.path)
  }

  @Test
  def testValue() = {
    //    method
    val valueProperty: Property = value(EntityTierOneType.PROPERTY, TEST)

    //    assert
    assertTrue(valueProperty.isInstanceOf[Property])
    assertEquals(valueProperty.path, EntityTierOneType.PROPERTY)
    assertEquals(valueProperty.value, TEST)
  }

  @Test
  def testAfterTestSuccess() = {
    //    setup
    beforeTest()
    takeSnapshot()

    //    method
    afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testAfterTestFail() = {
    //    setup
    beforeTest()
    takeSnapshot()
    _entityTierOneTypes += new EntityTierOneType(TEST, TEST_ID)

    //    method
    afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testTakeSnapshotFail() = {
    //    setup
    beforeTest()
    _noDefaultConstructorEntities += new NoDefaultConstructorEntity(TEST_ID, TEST)

    //    setup
    takeSnapshot()
  }

  @Test
  def testTakeSnapshotSuccess() = {
    //    setup
    beforeTest()
    _entityTierOneTypes += new EntityTierOneType(TEST, TEST_ID)
    takeSnapshot()

    //    method
    afterTest()
  }

  @Test
  def testAssertObjectWithComplexType() = {
    //    setup
    beforeTest()
    val tierOneType = new TierOneType(TEST)
    takeSnapshot()

    //    method
    assertObject(tierOneType, value(PROPERTY, TEST))
    afterTest()
  }

  @Test
  def testAssertObjectWithEntityTypeUsualCase(): Unit = {
    //    setup
    beforeTest()
    val entityTierOneType = new EntityTierOneType(TEST, TEST_ID)

    //    method
    takeSnapshot()
    _entityTierOneTypes += entityTierOneType
    assertObject(entityTierOneType, value(PROPERTY, TEST), notNull("id"))

    afterTest()

  }

  @Test
  def testAssertObjectWithEntityType(): Unit = {
    //    setup
    val entity = new EntityTierOneType(TEST, 1)
    beforeTest()

    //    method
    takeSnapshot()
    _entityTierOneTypes ++= List(entity)

    assertObject(entity, value(EntityTierOneType.ID, 1), value(EntityTierOneType.PROPERTY, TEST))

    afterTest()
  }

  @Test(expected = classOf[AssertionError])
  def testAssertObjectWithEntityTypeFail(): Unit = {
    //    setup
    val entity = new EntityTierOneType(TEST, 1)
    beforeTest()

    //    method
    takeSnapshot()
    _entityTierOneTypes ++= List(entity)

    assertObject(entity, value(EntityTierOneType.ID, 1), value(EntityTierOneType.PROPERTY, "fail"))

    afterTest()
  }

  @Test
  def testAssertObjectsComplexSuccess(): Unit = {
    //    setup
    beforeTest()
    val expected = new TierOneType(TEST)
    val actual = new TierOneType(TEST)

    //    method
    assertObjects(expected, actual)
    afterTest()
  }

  @Test(expected = classOf[AssertionError])
  def testAssertObjectsComplexFail(): Unit = {
    //    setup
    beforeTest()
    val expected = new TierOneType(TEST)
    val actual = new TierOneType(TEST + TEST)

    //    method
    assertObjects(expected, actual)
    afterTest()
  }

  @Test
  def testAssertObjectsComplexWithPropertyDifference(): Unit = {
    //    setup
    beforeTest()
    val expected = new TierOneType(TEST)
    val actual = new TierOneType(TEST + TEST)

    //    method
    assertObjects(expected, actual, value(PROPERTY, TEST + TEST))
    afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testMarkAssertedFail(): Unit = {
    //    setup
    beforeTest()
    val entityTierOneType = new EntityTierOneType(TEST, null)
    takeSnapshot()
    _entityTierOneTypes += entityTierOneType

    //    method
    markAsserted(entityTierOneType)

    afterTest()

  }

  @Test(expected = classOf[IllegalStateException])
  def testMarkAssertedNotEntity(): Unit = {
    // setup
    beforeTest()
    val entity = new TierOneType
    takeSnapshot()

    // method
    markAsserted(entity)

    afterTest()
  }

  @Test
  def testAssertObjectsEntitySuccess(): Unit = {
    //    setup
    beforeTest()
    val expected = new EntityTierOneType(TEST, 1)
    val actual = new EntityTierOneType(TEST, 1)

    //    method
    takeSnapshot()
    _entityTierOneTypes += actual
    assertObjects(expected, actual)

    afterTest()
  }

  @Test(expected = classOf[AssertionError])
  def testAssertObjectsEntityFail(): Unit = {
    //    setup
    beforeTest()
    val expected = new EntityTierOneType(TEST, 1)
    val actual = new EntityTierOneType(TEST + TEST, 1)

    //    method
    takeSnapshot()
    _entityTierOneTypes += actual
    assertObjects(expected, actual)

    afterTest()
  }

  @Test
  def testAssertEntityWithSnapshotSuccess(): Unit = {
    //    setup
    beforeTest()
    val entityTierOneType = new EntityTierOneType(TEST, 10)
    _entityTierOneTypes += entityTierOneType
    takeSnapshot()

    //    method
    _entityTierOneTypes.head.property_=(TEST + TEST)

    assertEntityWithSnapshot(entityTierOneType, value(PROPERTY, TEST + TEST))

    afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testAssertEntityWithSnapshotFail(): Unit = {
    //    setup
    beforeTest()
    val entityTierOneType = new EntityTierOneType(TEST, 10)
    _entityTierOneTypes += entityTierOneType
    takeSnapshot()

    //    method
    _entityTierOneTypes.head.property_=(TEST + TEST)
    assertEntityWithSnapshot(entityTierOneType, value(PROPERTY, TEST + TEST + TEST))

    afterTest()
  }

  @Test(expected = classOf[IllegalStateException])
  def testAssertEntityWithSnapshotNotEntity(): Unit = {
    //    setup
    beforeTest()
    takeSnapshot()

    //    method
    assertEntityWithSnapshot(new TierOneType)
  }

  @Test(expected = classOf[NullPointerException])
  def testAssertEntityWithSnapshotNullEntity(): Unit = {
    //    setup
    beforeTest()
    val entityTierOneType = new EntityTierOneType(TEST, 10)
    _entityTierOneTypes += entityTierOneType
    takeSnapshot()

    //    method
    _entityTierOneTypes.head.property_=(TEST + TEST)

    assertEntityWithSnapshot(null, value(PROPERTY, TEST + TEST))

    afterTest()
  }

  @Test
  def testMarkAssertedSuccess(): Unit = {
    // setup
    beforeTest()
    val entity = new EntityTierOneType(TEST, 1)
    takeSnapshot()
    _entityTierOneTypes ++= List(entity)

    // method
    markAsserted(entity)

    afterTest()
  }

  @Test
  def testAssertEntityAsDeletedSuccess(): Unit = {
    // setup
    beforeTest()
    val entity = new EntityTierOneType(TEST, 1)
    _entityTierOneTypes ++= List(entity)

    takeSnapshot()

    // method
    _entityTierOneTypes = ListBuffer()
    assertEntityAsDeleted(entity)

    afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testAssertEntityAsDeletedFail(): Unit = {
    // setup
    beforeTest()
    val entity = new EntityTierOneType(TEST, 1)
    _entityTierOneTypes ++= List(entity)
    takeSnapshot()

    // method
    assertEntityAsDeleted(entity)

    afterTest()
  }

  @Test(expected = classOf[IllegalStateException])
  def testAssertEntityAsDeletedNotEntity(): Unit = {
    // setup
    beforeTest()
    val entity = new TierOneType(TEST)
    takeSnapshot()

    // method
    assertEntityAsDeleted(entity)

    afterTest()
  }

  @Test
  def testIgnoreEntitySuccess(): Unit = {
    // setup
    beforeTest()
    val entity = new EntityTierOneType(TEST, 1)
    takeSnapshot()

    // method
    ignoreEntity(entity)

    afterTest()
  }

  @Test(expected = classOf[IllegalStateException])
  def testIgnoreEntityNotEntity(): Unit = {
    beforeTest()
    val entity = new TierOneType(TEST)
    takeSnapshot()

    // method
    ignoreEntity(entity)

    afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testIgnoreEntityFail(): Unit = {
    // setup
    beforeTest()

    val entity = new EntityTierOneType(TEST, null)
    takeSnapshot()
    _entityTierOneTypes ++= List(entity)

    // method
    ignoreEntity(entity)

    afterTest()
  }

  //integracioni
  @Test
  def testAssertObject(): Unit = {
    //	  method
    beforeTest()

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

    takeSnapshot()

    //    assert
    assertObject("", student, value("name", "Branko"),
      value("lastName", "Gvoka"),
      value("address.city", "Ruma"), value("address.street", "15. Maja"),
      value("address.streetNumber", "97"), value("faculty.name", "FTN"),
      value("faculty.teacher.name", "Ilija"), value("faculty.teacher.address.city", "Novi Sad"),
      value("faculty.teacher.address.street", "Puskinova"),
      value("faculty.teacher.student", student),
      value("faculty.teacher.address.streetNumber", "22"))

    afterTest()
  }

  @Test(expected = classOf[AssertionFailedError])
  def testAssertObjectMapsFail(): Unit = {
    //    setup
    beforeTest()
    val expected = Map("first" -> new TierOneType(TEST), "second" -> new TierOneType(TEST), "third2" -> new TierOneType(TEST))
    val actual = Map("first" -> new TierOneType(TEST), "second" -> new TierOneType(TEST), "third1" -> new TierOneType(TEST))

    takeSnapshot()

    //    method
    assertObjects(expected, actual)

    afterTest()
  }
}
