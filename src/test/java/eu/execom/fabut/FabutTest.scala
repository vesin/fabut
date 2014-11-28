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

class FabutTest extends AbstractFabutRepositoryAssertTest {

  val TEST = "test"
  val TEST_ID = 105

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
  @Test(expected = classOf[IllegalArgumentException])
  def testBeforeTest {
    //    method
    Fabut.beforeTest(new Object)
  }

  @Test
  def testAfterTestSuccess {
    //    setup
    Fabut.beforeTest(this)
    Fabut.takeSnapshot(new FabutReport)
    //    method
    Fabut.afterTest
  }

  @Test
  def testAfterTestFail {
    //    setup
    Fabut.beforeTest(this)
    Fabut.takeSnapshot(new FabutReport)
    val entityTierOneType = new EntityTierOneType(TEST, TEST_ID)
    //    method
    Fabut.afterTest
  }

  //    setup
  //    method
  //    assert

  //  @Test
  //  def testAssertObjectSimpleCaseClass = {
  //
  //    //	setup
  //    val objectInstance = new Person("1208992", "Branko", "Gvoka", 22)
  //
  //    //	assert
  //    assertObject(objectInstance, value("id", "1208992"), value("firstName", "Branko"), value("lastName", "Gvoka"), value("age", 22))
  //  }
  //
  //  @Test
  //  def testAssertObjectCaseClass = {
  //
  //    //	setup
  //    val objectInstance = new ObjectInsideSimpleProperty("myId")
  //
  //    //	assert
  //    assertObject(objectInstance, value("id", "myId"))
  //  }
  //
  //  @Test
  //  def testAssertObjectsWithExpectedNull = {
  //
  //    //	aetup
  //    val actualObject = new ObjectInsideSimpleProperty("zika")
  //
  //    //	assert
  //    assertObjects(null, actualObject)
  //
  //  }
  //
  //  @Test
  //  def testAssertObjectWithActualNull = {
  //
  //    //	setup
  //    val expectedObject = new ObjectInsideSimpleProperty("zika")
  //
  //    //	assert
  //    assertObjects(expectedObject, null)
  //  }
  //
  //  @Test
  //  def testAssertObjectsWithBothNull = {
  //
  //    //	assert
  //    assertObjects(null, null)
  //  }

}