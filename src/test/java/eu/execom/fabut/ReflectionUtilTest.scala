package eu.execom.fabut

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.typeOf
import org.junit.Assert._
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
import eu.execom.fabut.model.BadCopyClass
import eu.execom.fabut.model.BadCopyClass
import eu.execom.fabut.model.BadCopyClass
import eu.execom.fabut.exception.CopyException
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.DifferentPropertyClass
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.NotInTypes
import eu.execom.fabut.model.NotInTypes
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.TierTwoType
import eu.execom.fabut.model.TrivialClasses._

class ReflectionUtilTest extends AbstractFabutObjectAssertTest {

  val TEST = "test"

  @Test
  def testGetObjectProperties() = {

    //	setup
    val complexObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val actualObject = ObjectWithComplexProperty(5, true, complexObject, List(1, 2, 3))

    val actualObjectProperties = getObjectProperties(actualObject, Some(typeOf[ObjectWithComplexProperty]))

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
    //    val unexpectedPrimitiveProperties = reflectPrimitiveProperties(0, actualObjectPrimitvePropertiesList, actualObject, getObjectType(actualObject, COMPLEX_TYPE), Map(), fabutReport)

    //    assertEquals(fabutReport.result, true)
    //    assertEquals(unexpectedPrimitiveProperties.size, 0)
  }

  @Test
  def testAssertPrimitivesWithExpectedProperties = {

    //	setup
    val complexObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val actualObject = ObjectWithComplexProperty(10000, true, complexObject, List(5, 6, 7))

    val actualObjectPrimitvePropertiesList = Map("id" -> Property("id", 5), "state" -> Property("state", true), "complexObject" -> Property("complexObject", complexObject), "list" -> Property("list", List(1, 2, 3)))
    val actualObjectPropertiesList = Map("id" -> Property("id", 5), "list" -> Property("list", List(1, 2, 3)))
    val fabutReport = new FabutReport

    //    //	method
    //    val unexpectedPrimitiveProperties = reflectPrimitiveProperties(0, actualObjectPrimitvePropertiesList, actualObject, getObjectType(actualObject, COMPLEX_TYPE), actualObjectPropertiesList, fabutReport)
    //
    //    //	assert
    //    assertEquals(fabutReport.result, true)
    //    assertEquals(unexpectedPrimitiveProperties.size, 0)
  }

  @Test
  def testCreateEmptyCopy = {

    //	setup
    val complexObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val originalObject = ObjectWithComplexProperty(10000, true, complexObject, List(5, 6, 7))

    val copiedObject = createEmptyCopy(ObjectInsideSimpleProperty("200"), Some(typeOf[ObjectInsideSimpleProperty])).get.asInstanceOf[ObjectInsideSimpleProperty]

    assertEquals("", copiedObject.id)
  }

  @Test(expected = classOf[CopyException])
  def testCreateEmptyCopyBadCopyClass = {

    val originalObject = new BadCopyClass(TEST, TEST)

    val copiedObject = createEmptyCopy(originalObject, Some(typeOf[BadCopyClass]))

  }

  @Test
  def testCreateEmptyCopyFromComplexObject = {

    //	setup
    val complexObject = ObjectWithSimpleProperties("pera", 40, ObjectInsideSimpleProperty("200"))
    val originalObject = ObjectWithComplexProperty(10000, true, complexObject, List(5, 6, 7))

    //	method
    val copiedObject = createEmptyCopy(originalObject, Some(typeOf[ObjectWithComplexProperty])).get.asInstanceOf[ObjectWithComplexProperty]

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
  def testCopyPropertyPrimitiveProperty {
    //    setup
    val primitiveProperty = TEST
    //    method
    val assertResult = copyProperty(primitiveProperty, new NodesList)
    //    assert
    assertEquals(null, assertResult)
  }
  @Test
  def testCopyPropertyComplexProperty = {
    //    setup
    val complexProperty = TierOneType(TEST)
    //    method
    val assertResult = copyProperty(complexProperty, new NodesList)
    //    assert
    assertTrue(assertResult.isInstanceOf[TierOneType])
    assertEquals(TEST, assertResult.asInstanceOf[TierOneType]._property)
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
    val tierOneType = TierOneType(TEST)
    val tierOneTypeEmptyCopy = new TierOneType()
    //    method
    val assertResult = copyValueTo(tierOneType, "_property", TEST, tierOneTypeEmptyCopy)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testCopyValueFieldNameDoesntExist {
    //    setup
    val tierOneType = TierOneType(TEST)
    val tierOneTypeEmptyCopy = new TierOneType()
    //    method
    val assertResult = copyValueTo(tierOneType, TEST, TEST, tierOneTypeEmptyCopy)
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testCopyValueCopiedObjectIsNotInTypes {
    //    setup
    val notInTypes = NotInTypes(TEST)
    val notInTypesEmpty = new NotInTypes()
    //    method
    val assertResult = copyValueTo(notInTypes, "_property", TEST, notInTypesEmpty)
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

  @Test
  def testCopyPropertyListPropertyComplexTypesTODO {

  }

  @Test
  def testCopyPropertyMapPropertyComplexTypesTODO = {

  }

  @Test
  def testCreateExpectedPropertiesMap {

    val actualPropertiesSeq = Seq(Property("id", null))
    val actualPropertiesMap = createExpectedPropertiesMap(actualPropertiesSeq)

    println(actualPropertiesMap)
  }

  //  @Test
  //  def testEntityGetFieldFromGetter() = {
  //
  //    Fabut
  //
  //    val expected = new EntityTierOneType("Pera", 3)
  //
  //    val actual = new EntityTierOneType("Pera", 333333)
  //
  //    val tier2 = new EntityTierTwoType
  //    tier2.id = 3
  //    tier2._property = ("hello")
  //
  //    val tier3 = new EntityTierThreeType
  //    tier3.a_id = 3
  //    tier3._property = ("hello")
  //    tier3.a_subProperty = actual
  //
  //    // val x = getObjectProperties(actual, "", Some(typeOf[EntityTierOneType]))
  //
  //    val y = getObjectProperties(tier3, Some(typeOf[EntityTierThreeType]))
  //
  //    //assertObjects(tier3, tier3)
  //
  //    println(y)
  //  }
}