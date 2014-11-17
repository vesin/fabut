package eu.execom.fabut

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.typeOf

import org.junit.Assert.assertEquals
import org.junit.Test

import eu.execom.fabut.Fabut.assertObjects
import eu.execom.fabut.Fabut.createExpectedPropertiesMap
import eu.execom.fabut.enums.AssertType
import eu.execom.fabut.enums.AssertableType.COMPLEX_TYPE
import eu.execom.fabut.model.EntityTierOneType
import eu.execom.fabut.model.EntityTierThreeType
import eu.execom.fabut.model.EntityTierTwoType
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.model.ObjectWithComplexProperty
import eu.execom.fabut.model.ObjectWithSimpleProperties
import eu.execom.fabut.property.Property
import eu.execom.fabut.util.ReflectionUtil._

class ReflectionUtilTest extends AbstractFabutObjectAssertTest {

  /**TODOOOOOOO ask nolah for getValueType tests */

  //  @Test
  //  def testGetValueType() = {
  //
  //    //	setup
  //    val actualObject = ObjectInsideSimpleProperty("Pera")
  //
  //    //	assert
  //    assertEquals(getValueType(actualObject), COMPLEX_TYPE)
  //    assertEquals(getValueType(new EmptyClass), ENTITY_TYPE)
  //    assertEquals(getValueType(List()), SCALA_LIST_TYPE)
  //    assertEquals(getValueType(Map()), SCALA_MAP_TYPE)
  //    assertEquals(getValueType(100), PRIMITIVE_TYPE)
  //
  //  }

  @Test
  def testGetObjectProperties() = {

    //	setup
    val complexObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val actualObject = ObjectWithComplexProperty(5, true, complexObject, List(1, 2, 3))

    val actualObjectProperties = getObjectProperties(actualObject, "", Some(typeOf[ObjectWithComplexProperty]))

    //	assert
    assertEquals(actualObjectProperties.size, 4)
    assertEquals(actualObjectProperties("id").asInstanceOf[Property].value, 5)
    assertEquals(actualObjectProperties("state").asInstanceOf[Property].value, true)
    assertEquals(actualObjectProperties("complexObject").asInstanceOf[Property].value, complexObject)
    assertEquals(actualObjectProperties("list").asInstanceOf[Property].value, List(1, 2, 3))
  }

  @Test
  def testReflectObject() = {

    //	setup
    val expectedObject = ObjectInsideSimpleProperty("200")

    //	method
    val parentObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val actualObject = reflectObject("o", parentObject, getObjectType(parentObject, COMPLEX_TYPE)).get

    //	assert
    assertEquals(actualObject, expectedObject)

  }

  @Test
  def testGetFieldValueFromGetter() = {

    //	setup
    val actualObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))

    //	method
    val actualFieldUsername = getFieldValueFromGetter("_username", actualObject, getObjectType(actualObject, COMPLEX_TYPE)).get
    val actualFieldAge = getFieldValueFromGetter("_age", actualObject, getObjectType(actualObject, COMPLEX_TYPE)).get
    val actualFieldObjectInside = getFieldValueFromGetter("o", actualObject, getObjectType(actualObject, COMPLEX_TYPE)).get

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
    val actualUnknownField = getFieldValueFromGetter("_sid", actualObject, getObjectType(actualObject, COMPLEX_TYPE))

    //	assert
    assertEquals(actualUnknownField, None)

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
  def testAssertPrimitivesWithoutExpectedProperties = {

    val complexObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val actualObject = ObjectWithComplexProperty(5, true, complexObject, List(1, 2, 3))

    val actualObjectPrimitvePropertiesList = Map("id" -> Property("id", 5), "state" -> Property("state", true), "complexObject" -> Property("complexObject", complexObject), "list" -> Property("list", List(1, 2, 3)))
    val fabutReport = new FabutReport

    //	method
    val unexpectedPrimitiveProperties = reflectPrimitiveProperties(0, actualObjectPrimitvePropertiesList, actualObject, getObjectType(actualObject, COMPLEX_TYPE), Map(), fabutReport)

    assertEquals(fabutReport.result, AssertType.ASSERT_SUCCESS)
    assertEquals(unexpectedPrimitiveProperties.size, 0)
  }

  @Test
  def testAssertPrimitivesWithExpectedProperties = {

    //	setup
    val complexObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val actualObject = ObjectWithComplexProperty(10000, true, complexObject, List(5, 6, 7))

    val actualObjectPrimitvePropertiesList = Map("id" -> Property("id", 5), "state" -> Property("state", true), "complexObject" -> Property("complexObject", complexObject), "list" -> Property("list", List(1, 2, 3)))
    val actualObjectPropertiesList = Map("id" -> Property("id", 5), "list" -> Property("list", List(1, 2, 3)))
    val fabutReport = new FabutReport

    //	method
    val unexpectedPrimitiveProperties = reflectPrimitiveProperties(0, actualObjectPrimitvePropertiesList, actualObject, getObjectType(actualObject, COMPLEX_TYPE), actualObjectPropertiesList, fabutReport)

    //	assert
    assertEquals(fabutReport.result, AssertType.ASSERT_SUCCESS)
    assertEquals(unexpectedPrimitiveProperties.size, 0)
  }

  @Test
  def testCreateEmptyCopy = {

    //	setup
    val complexObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val originalObject = ObjectWithComplexProperty(10000, true, complexObject, List(5, 6, 7))

    val copiedObject = createEmptyCopy(ObjectInsideSimpleProperty("200"), Some(typeOf[ObjectInsideSimpleProperty])).asInstanceOf[ObjectInsideSimpleProperty]

    assertEquals("", copiedObject.id)
  }

  @Test
  def testCreateEmptyCopyFromComplexObject = {

    //	setup
    val complexObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val originalObject = ObjectWithComplexProperty(10000, true, complexObject, List(5, 6, 7))

    //	method
    val copiedObject = createEmptyCopy(originalObject, Some(typeOf[ObjectWithComplexProperty])).asInstanceOf[ObjectWithComplexProperty]

    //	assert
    assertEquals(0, copiedObject.id)
    assertEquals(false, copiedObject.state)
    assertEquals(null, copiedObject.complexObject)
    assertEquals(null, copiedObject.list)
  }

  @Test
  def testCreateCopy1 = {

    //	setup
    val complexObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val originalObject = ObjectWithComplexProperty(10000, true, complexObject, List(5, 6, 7))

    assertObjects(1, 1)
    //	method
    val copiedObject = createCopy(originalObject).asInstanceOf[ObjectWithComplexProperty]

    assertEquals(10000, copiedObject.id)
    assertEquals(true, copiedObject.state)
    assertEquals(complexObject, copiedObject.complexObject)
    assertEquals(complexObject.username, "pera")
    assertEquals(complexObject.age, 40)
    assertEquals(complexObject.o, ObjectInsideSimpleProperty("200"))
    assertEquals(List(5, 6, 7), copiedObject.list)

  }

  @Test
  def testComplex = {
    //	setup
    val complexObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val actualObject = ObjectWithComplexProperty(10000, true, complexObject, List(5, 6, 7))

    assertObjects(actualObject, null)
  }

  @Test
  def testCreateExpectedPropertiesMap {

    val actualPropertiesSeq = Seq(Property("id", null))
    val actualPropertiesMap = createExpectedPropertiesMap(actualPropertiesSeq)

    println(actualPropertiesMap)
  }

  @Test
  def testEntityGetFieldFromGetter() = {

    Fabut

    val expected = new EntityTierOneType
    expected._property = "Pera"
    expected.id = 3

    val actual = new EntityTierOneType
    actual._property = "Pera"
    actual.id = 333333

    val tier2 = new EntityTierTwoType
    tier2.id = 3
    tier2._property = ("hello")

    val tier3 = new EntityTierThreeType
    tier3.a_id = 3
    tier3._property = ("hello")
    tier3.a_subProperty = actual

    // val x = getObjectProperties(actual, "", Some(typeOf[EntityTierOneType]))

    val y = getObjectProperties(tier3, "", Some(typeOf[EntityTierThreeType]))

    //assertObjects(tier3, tier3)

    println(y)
  }
}