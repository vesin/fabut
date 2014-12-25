package eu.execom.fabut.util

import eu.execom.fabut.AbstractFabutObjectAssertTest
import eu.execom.fabut.AssertableType._
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.model.TrivialClasses._
import eu.execom.fabut.model.{ObjectInsideSimpleProperty, ObjectWithComplexProperty, ObjectWithSimpleProperties, TierOneType}
import eu.execom.fabut.property.Property
import eu.execom.fabut.util.ReflectionUtil._
import org.junit.Assert._
import org.junit.Test

import scala.reflect.runtime.universe.typeOf

class ReflectionUtilTest extends AbstractFabutObjectAssertTest {

  val TEST = "test"

  @Test
  def testGetObjectProperties() = {
    //	setup
    val complexObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val actualObject = ObjectWithComplexProperty(5, true, complexObject, List(1, 2, 3))

    //    method
    val actualObjectProperties = getObjectProperties(actualObject, Some(typeOf[ObjectWithComplexProperty]))

    //	assert
    assertEquals(actualObjectProperties.size, 4)
    assertEquals(actualObjectProperties("id").asInstanceOf[Property].value, 5)
    assertEquals(actualObjectProperties("state").asInstanceOf[Property].value, true)
    assertEquals(actualObjectProperties("complexObject").asInstanceOf[Property].value, complexObject)
    assertEquals(actualObjectProperties("list").asInstanceOf[Property].value, List(1, 2, 3))
  }

  @Test
  def testGetFieldValueFromGetter() = {
    //	setup
    val actualObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))

    //	method
    val actualFieldUsername = getFieldValueFromGetter("_username", actualObject, getClassType(actualObject, COMPLEX_TYPE).get).get
    val actualFieldAge = getFieldValueFromGetter("_age", actualObject, getClassType(actualObject, COMPLEX_TYPE).get).get
    val actualFieldObjectInside = getFieldValueFromGetter("o", actualObject, getClassType(actualObject, COMPLEX_TYPE).get).get

    //	assert
    assertEquals(actualFieldUsername, "pera")
    assertEquals(actualFieldAge, 40)
    assertEquals(actualFieldObjectInside, ObjectInsideSimpleProperty("200"))
  }

  @Test
  def testGetFieldValueFromGetterWithFail() = {
    //	setup
    val actualObject = ObjectInsideSimpleProperty("100")

    //	method
    val actualUnknownField = getFieldValueFromGetter("_sid", actualObject, getClassType(actualObject, COMPLEX_TYPE).get)

    println(actualUnknownField)

  }

  @Test
  def testSetField() = {
    // setup
    val actualObject = ObjectInsideSimpleProperty("100")

    //	method
    setField("id", "500", actualObject, typeOf[ObjectInsideSimpleProperty])

    //assert
    assertEquals(actualObject.id, "500")
  }

  @Test
  def testCreateEmptyCopyFromComplexObject = {
    //	setup
    val complexObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val originalObject = ObjectWithComplexProperty(10000, true, complexObject, List(5, 6, 7))

    //	method
    val copiedObject = createEmptyCopy(originalObject, typeOf[ObjectWithComplexProperty]).get.asInstanceOf[ObjectWithComplexProperty]

    //	assert
    assertEquals(0, copiedObject.id)
    assertEquals(false, copiedObject.state)
    assertEquals(null, copiedObject.complexObject)
    assertEquals(null, copiedObject.list)
  }

  @Test
  def testCopyPropertyIsNullProperty {
    //    setup
    val nullProperty = null

    //    method
    val assertResult = copyProperty(nullProperty, new NodesList)

    //    assert
    assertEquals(null, assertResult)
  }

  @Test
  def testCopyPropertyListPropertyWithPrimitiveTypes {
    //    setup
    val listProperty = List(TEST + 1, TEST + 2)

    //    method
    val assertResult = copyProperty(listProperty, new NodesList).asInstanceOf[List[_]]

    //    assert
    assertEquals(listProperty.size, assertResult.size)
    assertEquals(listProperty.head, assertResult.head)
    assertEquals(listProperty.last, assertResult.last)
  }

  @Test
  def testCopyPropertyMapPropertyWithPrimitiveTypes {
    //    setup
    val mapProperty = Map(TEST -> (TEST + 1), TEST -> (TEST + 2))

    //    method
    val assertResult = copyProperty(mapProperty, new NodesList).asInstanceOf[Map[_, _]]

    //    assert
    assertEquals(mapProperty.size, assertResult.size)
    assertEquals(mapProperty.head, assertResult.head)
    assertEquals(mapProperty.last, assertResult.last)
  }

  @Test
  def testCopyValue {
    //    setup
    val tierOneTypeEmptyCopy = new TierOneType()

    //    method
    val assertResult = copyValueTo("_property", TEST, tierOneTypeEmptyCopy)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testCopyValueFieldNameDoesntExist {
    //    setup
    val tierOneTypeEmptyCopy = new TierOneType()

    //    method
    val assertResult = copyValueTo(TEST, TEST, tierOneTypeEmptyCopy)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testCreateCopyList {
    //    setup
    val list = List(1, 2, 3)

    //    method
    val copiedList = createCopy(list)

    //    assert
    assertEquals(list, copiedList)
  }

  @Test
  def testCreateCopyObjectWithComplexProperty {
    //	setup
    val complexObject = ObjectWithSimpleProperties(TEST + TEST, 1, ObjectInsideSimpleProperty(TEST))
    val originalObject = ObjectWithComplexProperty(2, true, complexObject, List(TEST + 1, TEST + 2, TEST + 3))

    //	method
    val copiedObject = createCopy(originalObject).asInstanceOf[ObjectWithComplexProperty]

    //    assert
    assertEquals(2, copiedObject.id)
    assertEquals(true, copiedObject.state)
    assertEquals(complexObject, copiedObject.complexObject)
    assertEquals(complexObject.username, TEST + TEST)
    assertEquals(complexObject.age, 1)
    assertEquals(complexObject.o, ObjectInsideSimpleProperty(TEST))
    assertEquals(complexObject.o.id, TEST)
    assertEquals(List(TEST + 1, TEST + 2, TEST + 3), copiedObject.list)
  }

  @Test
  def testCreateCopyCyclic {
    //    setup
    val originalObjectA = new A(null, "mika")
    val originalObjectB = new B(originalObjectA, "pera")
    originalObjectA.b = originalObjectB

    //    method
    val copiedObject = createCopy(originalObjectA).asInstanceOf[A]

    //    assert
    assertEquals(originalObjectA.b.asInstanceOf[B].s, copiedObject.b.asInstanceOf[B].s)
    assertEquals(originalObjectA.b.asInstanceOf[B].c.asInstanceOf[A].b.asInstanceOf[B].s, copiedObject.b.asInstanceOf[B].c.asInstanceOf[A].b.asInstanceOf[B].s)
    assertEquals(originalObjectA.s, copiedObject.s)
  }

  //  @Test
  //  def test {
  //    val entity = new EntityTierOneType(TEST, 1)
  //    val e = new ChildClass(TEST, TEST + TEST)
  //    pullMembers(e)
  //    val ee = new ParentClass(TEST)
  //    pullMembers(ee)
  //    pullMembers(entity)
  //  }
  //
  //  @Test
  //  def testGetObjectPropertiesAll {
  //  }
  //
  //  @Test
  //  def testCopyPropertyListPropertyComplexTypesTODO {
  //
  //  }
  //
  //  @Test
  //  def testCopyPropertyMapPropertyComplexTypesTODO = {
  //
  //  }
}