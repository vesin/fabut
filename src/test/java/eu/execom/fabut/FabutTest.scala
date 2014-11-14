package eu.execom.fabut
import scala.reflect.runtime.universe.{ Type, typeOf }
import org.junit.Before
import eu.execom.fabut.util.ReflectionUtil._
import eu.execom.fabut.enums.AssertableType._
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import org.junit.Test
import eu.execom.fabut.model.Person
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.model.ObjectWithComplexProperty
import eu.execom.fabut.model.CopyCaseClass
import eu.execom.fabut.model.ObjectWithSimpleProperties
import eu.execom.fabut.model.ObjectWithMap
import eu.execom.fabut.model.ObjectWithSimpleList
import eu.execom.fabut.model.ObjectWithSimpleMap
import eu.execom.fabut.FabutObjectAssert._
import eu.execom.fabut.Fabut._

class FabutTest {

  @Test
  def testAssertObjectSimpleCaseClass = {

    //	setup
    val objectInstance = new Person("1208992", "Branko", "Gvoka", 22)

    //	assert
    assertObject(objectInstance, value("id", "1208992"), value("firstName", "Branko"), value("lastName", "Gvoka"), value("age", 22))
  }

  @Test
  def testAssertObjectCaseClass = {

    //	setup
    val objectInstance = new ObjectInsideSimpleProperty("myId")

    //	assert
    assertObject(objectInstance, value("id", "myId"))
  }

  @Test
  def testAssertObjectsWithExpectedNull = {

    //	aetup
    val actualObject = new ObjectInsideSimpleProperty("zika")

    //	assert
    assertObjects(null, actualObject)

  }

  @Test
  def testAssertObjectWithActualNull = {

    //	setup
    val expectedObject = new ObjectInsideSimpleProperty("zika")

    //	assert
    assertObjects(expectedObject, null)
  }

  @Test
  def testAssertObjectsWithBothNull = {

    //	assert
    assertObjects(null, null)
  }

}