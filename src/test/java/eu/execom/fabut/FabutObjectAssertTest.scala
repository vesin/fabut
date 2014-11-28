package eu.execom.fabut

import org.junit.Assert._
import org.junit.Assert.assertTrue
import org.junit.Test
import eu.execom.fabut.Fabut._
import eu.execom.fabut.model.EmptyClass
import eu.execom.fabut.model.IgnoredType
import eu.execom.fabut.model.ListType
import eu.execom.fabut.model.NoGettersType
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.model.ObjectWithComplexProperty
import eu.execom.fabut.model.ObjectWithSimpleProperties
import eu.execom.fabut.model.TierFiveType
import eu.execom.fabut.model.TierFourType
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.TierThreeType
import eu.execom.fabut.model.TierTwoType
import eu.execom.fabut.model.TierTwoTypeWithIgnoredType
import eu.execom.fabut.model.TrivialClasses._
import eu.execom.fabut.model.TierSixType
import eu.execom.fabut.property.IProperty
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.property.Property
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.TierTwoType
import eu.execom.fabut.property.IgnoredProperty
import eu.execom.fabut.property.IgnoredProperty
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.pair.AssertPair
import eu.execom.fabut.enums.AssertableType._
import eu.execom.fabut.enums.NodeCheckType._
import eu.execom.fabut.model.EntityTierOneType
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.pair.AssertPair
import org.w3c.dom.Node
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.property.NullProperty
import eu.execom.fabut.pair.AssertPair
import eu.execom.fabut.model.BadCopyClass
import eu.execom.fabut.model.TierOneType
import eu.execom.fabut.model.TierSixType
import eu.execom.fabut.pair.AssertPair
import eu.execom.fabut.model.TierThreeType
import eu.execom.fabut.model.TierFiveType
import eu.execom.fabut.model.TierFourType

class FabutObjectAssertTest extends AbstractFabutObjectAssertTest {

  //	setup
  //    method
  //	assert

  val TEST = "test"
  val DOT = "."
  val TIER_ONE_TYPE_PROPERTY = "_property"
  val TIER_TWO_TYPE_PROPERTY = "_property._property"
  val LIST_PROPERTY = "_property"

  @Test
  def testAssertObjectWithPropertiesIgnoreType = {
    //	setup
    val ignoredType = new IgnoredType(TEST)

    //	method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, ignoredType, Map(), new NodesList)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNoGettersType() = {
    //	setup
    val noGettersType = new NoGettersType(TEST)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, noGettersType, Map(), new NodesList)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNoProperty = {
    //	setup
    val tierOneType = new TierOneType(TEST)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierOneType, Map(), new NodesList)

    //	assert
    assertFalse(assertResult)
  }

  /**
   *   Test if class doesn't have any properties
   */
  @Test
  def testAssertObjectWithPropertiesNoPropertyEmptyClass = {
    //	setup
    val tierOneType = new EmptyClass

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierOneType, Map(), new NodesList)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNotNullPropertyActuallyNull() = {
    //	setup
    val tierOneType = TierOneType(null)
    val notNullProperty = notNull(TIER_ONE_TYPE_PROPERTY)
    val ignoredProperty = ignored("PROPERTY")
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> notNullProperty, "PROPERTY" -> ignoredProperty)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierOneType, properties, new NodesList)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNotNullProperty() = {
    //	setup
    val tierOneType = TierOneType(TEST)
    val notNullProperty = Fabut.notNull(TIER_ONE_TYPE_PROPERTY)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> notNullProperty)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierOneType, properties, new NodesList)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNullPropertyActuallyNotNull() = {
    //	setup
    val tierOneType = TierOneType(TEST)
    val isNullProperty = Fabut.isNull(TIER_ONE_TYPE_PROPERTY)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> isNullProperty)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierOneType, properties, new NodesList)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNullProperty() = {
    //	setup
    val tierOneType = TierOneType(null)
    val isNullProperty = Fabut.isNull(TIER_ONE_TYPE_PROPERTY)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> isNullProperty)
    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierOneType, properties, new NodesList)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesIgnoredProperty() = {
    //	setup
    val tierOneType = TierOneType(null)
    val ignoredProperty = Fabut.ignored(TIER_ONE_TYPE_PROPERTY)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> ignoredProperty)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierOneType, properties, new NodesList)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyExpectedNullActualNull() = {
    //	setup
    val tierOneType = TierOneType(null)
    val valueProperty = Fabut.value(TIER_ONE_TYPE_PROPERTY, null)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> valueProperty)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierOneType, properties, new NodesList)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyActualNull() = {
    //	setup
    val tierOneType = TierOneType(null)
    val property = Fabut.value(TIER_ONE_TYPE_PROPERTY, TEST)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)
    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierOneType, properties, new NodesList)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyExpectedNull() = {
    //	setup
    val tierOneType = TierOneType(TEST)
    val property = Fabut.value(TIER_ONE_TYPE_PROPERTY, null)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierOneType, properties, new NodesList)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyEqual = {
    //	setup
    val tierOneType = TierOneType(TEST)
    val property = Fabut.value(TIER_ONE_TYPE_PROPERTY, TEST)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierOneType, properties, new NodesList)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyNotEqual = {
    //	setup
    val tierOneType = TierOneType(TEST)
    val property = Fabut.value(TIER_ONE_TYPE_PROPERTY, TEST + TEST)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierOneType, properties, new NodesList)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesTierTwoTypeChangedPropertyEqual = {

    //	setup
    val tierOneType = TierOneType(TEST)
    val tierTwoType = new TierTwoType(tierOneType)
    val property = value(TIER_TWO_TYPE_PROPERTY, TEST)
    val properties = Map(TIER_TWO_TYPE_PROPERTY -> property)
    //	method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierTwoType, properties, new NodesList)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesTierTwoTypeChangedPropertyNotEqual = {
    //	  setup
    val tierOneType = TierOneType(TEST)
    val tierTwoType = new TierTwoType(tierOneType)
    val property = value(TIER_TWO_TYPE_PROPERTY, TEST + TEST)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)
    //    method
    val report = new FabutReport
    val assertResult = fabutObjectAssert.assertObjectWithProperties(report, tierTwoType, properties, new NodesList)
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesTierTwoTypeWithIgnoredTierOneType = {

    val ignoredType = new IgnoredType(TEST + TEST)
    val tierTwoTypeWithIgnoredType = new TierTwoTypeWithIgnoredType(ignoredType)
    val property = value(TIER_TWO_TYPE_PROPERTY, TEST)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)

    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, tierTwoTypeWithIgnoredType, properties, new NodesList)

    assertTrue(assertResult)

  }

  @Test
  def testAssertObjectsWithPropertiesComplexObjectEqualValues = {

    //	setup
    val actualInsideSimple = ObjectInsideSimpleProperty("3301")
    val a1 = ObjectInsideSimpleProperty("3301")
    val a2 = ObjectInsideSimpleProperty("5000")
    val e1 = ObjectInsideSimpleProperty("3301")
    val e2 = ObjectInsideSimpleProperty("5000")
    val actualSimpleObject = ObjectWithSimpleProperties("mika", 22, actualInsideSimple)
    val actual = ObjectWithComplexProperty(900, true, actualSimpleObject, List(a1, a2))

    val properties = Map(
      "id" -> Property("id", 900),
      "state" -> Property("state", true),
      "complexObject._username" -> Property("complexObject._username", "mika"),
      "complexObject._age" -> Property("complexObject._age", 22),
      "complexObject.o.id" -> Property("complexObject.o.id", "3301"),
      "list" -> Property("list", List(e1, e2)))

    //	method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, actual, properties, new NodesList)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithComplexPropertiesAndComplexListEqual = {

    //    setup
    val actualInsideSimple = ObjectInsideSimpleProperty("property")
    val expectedInsideSimple = ObjectInsideSimpleProperty("property")
    val a1 = ObjectInsideSimpleProperty("3301")
    val e1 = ObjectInsideSimpleProperty("3301")
    val a2 = ObjectInsideSimpleProperty("200")
    val e2 = ObjectInsideSimpleProperty("200")
    val actualSimpleObject = ObjectWithSimpleProperties("mika", 22, actualInsideSimple)
    val expectedSimpleObject = ObjectWithSimpleProperties("mika", 22, expectedInsideSimple)
    val actual = ObjectWithComplexProperty(900, true, actualSimpleObject, List(a1, a2, a1, actualInsideSimple))
    val expected = ObjectWithComplexProperty(900, true, expectedSimpleObject, List(e1, e2, e1, expectedInsideSimple))

    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, expected, actual, Map())
    //    assert

    assertTrue(assertResult)
  }

  @Test
  def testAssertListElementsEqual = {
    //	  setup
    val actualList = List(1, 2, 3)
    val expectedList = List(1, 2, 3)
    //    method
    val assertResult = fabutObjectAssert.assertListElements(0, actualList, expectedList)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertListElementsNotEqual = {
    //	  setup
    val actualList = List(3, 2, 5, 8)
    val expectedList = List(4, 3, 6, 7)
    //    method	
    val assertResult = fabutObjectAssert.assertListElements(0, actualList, expectedList)(new FabutReport)
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertListElementsDifferentListSize = {
    //	  setup
    val actualList = List(3, 2, 3)
    val expectedList = List(4, 3, 6, 7)
    //    method
    val assertResult = fabutObjectAssert.assertListElements(0, actualList, expectedList)(new FabutReport)
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertListElementsDifferentTypeEqual = {
    //	  setup
    val actualList = List(3, 22l, "pera", true)
    val expectedList = List(3, 22l, "pera", true)
    //    method
    val assertResult = fabutObjectAssert.assertListElements(0, actualList, expectedList)(new FabutReport)
    //    assert
    assertTrue(assertResult)

  }

  @Test
  def testAssertObjectsWithPropertiesListTypeEqual = {
    //    setup
    val listType = new ListType(List(6, 5, 4, 3, 2, 1))
    val property = value(LIST_PROPERTY, List(6, 5, 4, 3, 2, 1))
    val properties = Map(LIST_PROPERTY -> property)
    //    method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, listType, properties, new NodesList)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesListTypeNotEqual = {
    //	  setup
    val listType = new ListType(List(6, 5, 0, 1, 2, 1))
    val property = value(LIST_PROPERTY, List(6, 5, 4, 3, 2, 1))
    val properties = Map(LIST_PROPERTY -> property)
    //    method
    val report = new FabutReport
    val assertResult = fabutObjectAssert.assertObjectWithProperties(report, listType, properties, new NodesList)
    //    assert
    assertFalse(assertResult)

  }

  @Test
  def testAssertMapElementsDifferentSize = {
    //	  setup
    val actualMap: Map[Any, Any] = Map(1 -> 10, 2 -> 20)
    val expectedMap: Map[Any, Any] = Map(1 -> 10, 2 -> 20, 3 -> 30)
    //    method
    val assertResult = fabutObjectAssert.assertMapElements(actualMap, expectedMap)(new FabutReport)
    //    assert
    assertFalse(assertResult)
  }
  @Test
  def testAssertMapElementsEqual = {
    //	setup
    val actualMap: Map[Any, Any] = Map(1 -> 10, 3 -> 30, 2 -> 20)
    val expectedMap: Map[Any, Any] = Map(1 -> 10, 2 -> 20, 3 -> 30)
    //    method
    val assertResult = fabutObjectAssert.assertMapElements(actualMap, expectedMap)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }
  @Test
  def testAssertMapElementsNotEqual = {
    //	setup
    val actualMap: Map[Any, Any] = Map(1 -> 10, 3 -> 21, 2 -> 31)
    val expectedMap: Map[Any, Any] = Map(1 -> 10, 2 -> 20, 3 -> 30)
    //    method
    val assertResult = fabutObjectAssert.assertMapElements(actualMap, expectedMap)(new FabutReport)
    //    assert
    assertFalse(assertResult)
  }

  /** TODO ASSERT COMPLEX ELEMENTS MAP */

  @Test
  def testAssertMapElementsTierOneTypeValuesEqual = {
    //	setup
    val actualMap: Map[Any, Any] = Map(1 -> TierOneType(TEST), 3 -> TierOneType(TEST + TEST + TEST), 2 -> TierOneType(TEST + TEST))
    val expectedMap: Map[Any, Any] = Map(1 -> TierOneType(TEST), 2 -> TierOneType(TEST + TEST), 3 -> TierOneType(TEST + TEST + TEST))
    //    method
    val assertResult = fabutObjectAssert.assertMapElements(actualMap, expectedMap)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertMapElementsTierOneTypeValuesNotEqual = {
    //	setup
    val actualMap: Map[Any, Any] = Map(1 -> TierOneType(TEST + TEST), 3 -> TierOneType(TEST + TEST + TEST), 2 -> TierOneType(TEST + TEST))
    val expectedMap: Map[Any, Any] = Map(1 -> TierOneType(TEST), 2 -> TierOneType(TEST), 3 -> TierOneType(TEST + TEST + TEST))
    //    method
    val assertResult = fabutObjectAssert.assertMapElements(actualMap, expectedMap)(new FabutReport)
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertMapElementsTierOneTypeKeysEqual = {

    val actualMap: Map[Any, Any] = Map(
      TierOneType(TEST) -> TEST,
      TierOneType(TEST + TEST) -> (TEST + TEST),
      TierOneType(TEST + TEST + TEST) -> (TEST + TEST + TEST))

    val expectedMap: Map[Any, Any] = Map(
      TierOneType(TEST + TEST) -> (TEST + TEST),
      TierOneType(TEST) -> TEST,
      TierOneType(TEST + TEST + TEST) -> (TEST + TEST + TEST))

    //    method
    val assertResult = fabutObjectAssert.assertMapElements(actualMap, expectedMap)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeEqual = {
    //	  setup
    val actual = new TierOneType(TEST)
    val expected = new TierOneType(TEST)
    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, expected, actual, Map())
    //    assert
    assertTrue(assertResult)

  }

  @Test
  def testAssertObjectsTierOneTypeNotEqual = {
    //	  setup
    val actual = new TierOneType(TEST)
    val expected = new TierOneType(TEST + TEST)
    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, expected, actual, Map())
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeEqual = {
    //	  setup
    val actual = new TierTwoType(new TierOneType(TEST))
    val expected = new TierTwoType(new TierOneType(TEST))
    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, expected, actual, Map())
    //    assert  
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeNotEqual = {
    //	  setup
    val actual = new TierTwoType(new TierOneType(TEST + TEST))
    val expected = new TierTwoType(new TierOneType(TEST))
    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, expected, actual, Map())
    //    assert  
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithSimplePropertiesEqual() = {
    //    setup
    val actual = ObjectWithSimpleProperties("pera", 43, ObjectInsideSimpleProperty("marko"))
    val expected = ObjectWithSimpleProperties("pera", 43, ObjectInsideSimpleProperty("marko"))
    //	  method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, expected, actual, Map())
    //	  assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithSimplePropertiesNotEqual() = {

    //    setup
    val actual = ObjectWithSimpleProperties("pera", 43, ObjectInsideSimpleProperty("markoooo"))
    val expected = ObjectWithSimpleProperties("pera", 43, ObjectInsideSimpleProperty("marko"))

    //	  method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, expected, actual, Map())

    //	  assert
    assertFalse(assertResult)
  }

  // recursive graphs

  @Test
  def testAssertObjectsTrivialClassesWithRecursiveGraphsEqual = {

    val actual_a = new A(null, "mika")
    val actual_b = new B(actual_a, "pera")
    actual_a.b = actual_b

    val expected_a = new A(null, "mika")
    val expected_b = new B(expected_a, "pera")
    expected_a.b = expected_b

    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, expected_a, actual_a, Map())

    //    assert
    assertTrue(assertResult)
  }

  // FIXME case when you forget to add types of elements, check it out
  @Test
  def testAssertObjectsTierTypesRecursiveGraphEqual = {

    val aTierThreeType = TierThreeType(null)
    val aTierFourType = TierFourType(aTierThreeType)
    val aTierFiveType = TierFiveType(aTierFourType, TEST)
    val aTierSixType = TierSixType(aTierFiveType)
    aTierThreeType._property = aTierSixType

    val eTierThreeType = TierThreeType(null)
    val eTierFourType = TierFourType(eTierThreeType)
    val eTierFiveType = TierFiveType(eTierFourType, TEST)
    val eTierSixType = TierSixType(eTierFiveType)
    eTierThreeType._property = eTierSixType

    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, eTierSixType, aTierSixType, Map())

    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTypesWithRecursiveGraphNotEqual = {

    val aTierThreeType = TierThreeType(null)
    val aTierFourType = TierFourType(aTierThreeType)
    val aTierFiveType = TierFiveType(aTierFourType, TEST)
    val aTierSixType = TierSixType(aTierFiveType)
    aTierThreeType._property = aTierSixType

    val eTierThreeType = TierThreeType(null)
    val eTierFourType = TierFourType(eTierThreeType)
    val eTierFiveType = TierFiveType(eTierFourType, TEST + TEST)
    val eTierSixType = TierSixType(eTierFiveType)
    eTierThreeType._property = eTierSixType

    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, eTierSixType, aTierSixType, Map())

    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesTierTypesRecursiveGraphEqual = {

    val aTierThreeType = TierThreeType(null)
    val aTierFourType = TierFourType(aTierThreeType)
    val aTierFiveType = TierFiveType(aTierFourType, TEST)
    val aTierSixType = TierSixType(aTierFiveType)
    aTierThreeType._property = aTierSixType

    val eTierThreeType = TierThreeType(null)
    val eTierFourType = TierFourType(eTierThreeType)
    val eTierFiveType = TierFiveType(eTierFourType, TEST)
    val eTierSixType = TierSixType(eTierFiveType)
    eTierThreeType._property = eTierSixType

    val properties = Map(
      "_property" -> Property(TEST, eTierFiveType),
      "_property._property" -> Property("_property._property", eTierFourType),
      "_property._simpleProperty" -> Property("_property._simpleProperty", TEST),
      "_property._property._property" -> Property("_property._property._property", eTierThreeType),
      "_property._property._property._property" -> Property("_property._property._property._property", eTierSixType))

    val assertResult = fabutObjectAssert.assertObjectWithProperties(new FabutReport, aTierSixType, properties, new NodesList)

    assertTrue(assertResult)
  }

  @Test
  def testWithRecursiveGraphWithTrivialObjects() = {

    //	  setup
    val actual_b = new B(null, "pera")
    val actual_a = new A(actual_b, "mika")
    val actual_d = new D(null)
    val actual_c = new C(actual_d, "zelja")
    val actual_e = new E(actual_a)
    actual_b.c = actual_c
    actual_d.e = actual_e

    val expected_b = new B(null, "pera")
    val expected_a = new A(expected_b, "mika")
    val expected_d = new D(null)
    val expected_c = new C(expected_d, "zelja")
    val expected_e = new E(expected_a)
    expected_b.c = expected_c
    expected_d.e = expected_e

    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, expected_a, actual_a, Map())

    //    assert
    assertTrue(assertResult)

  }

  @Test
  def testAssertObjectsTrivialClassesRecursiveIsomorphicGraphs() = {

    //	setup actual

    val a_b1 = new B(null, "b")
    val a_a1 = new A(a_b1, "a")

    val a_b2 = new B(null, "_b")
    val a_a2 = new A(a_b2, "_a")

    val a_b4 = new B(null, "b2")
    val a_a4 = new A(a_b4, "a2")
    val a_c4 = new C(a_a1, "c2")

    val a_b3 = new B(null, "b1")
    val a_a3 = new A(a_b3, "a1")
    val a_c3 = new C(a_a4, "c1")

    val a_c1 = new C(a_a2, "c")
    val a_c2 = new C(a_a3, "_c")

    a_b1.c = a_c1
    a_b2.c = a_c2
    a_b3.c = a_c3
    a_b4.c = a_c4

    //	setup expected
    val e_b1 = new B(null, "b")
    val e_a1 = new A(e_b1, "a")

    val e_b2 = new B(null, "_b")
    val e_a2 = new A(e_b2, "_a")

    val e_b4 = new B(null, "b2")
    val e_a4 = new A(e_b4, "a2")

    val e_b3 = new B(null, "b1")
    val e_a3 = new A(e_b3, "a1")
    val e_c3 = new C(e_a4, "c1")

    val e_c1 = new C(e_a2, "c")
    val e_c2 = new C(e_a3, "_c")
    val e_c4 = new C(e_c3, "c2")

    e_b1.c = e_c1
    e_b2.c = e_c2
    e_b3.c = e_c3
    e_b4.c = e_c4

    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, e_a1, a_a1, Map())
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeChangedPropertyEqual = {
    //	  setup
    val aTierOneType = TierOneType(TEST + TEST)

    val eTierOneType = TierOneType(TEST)

    val properties = Map(
      "_property" -> Property("_property", TEST + TEST))
    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, eTierOneType, aTierOneType, properties)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeChangedPropertyNotEqual = {
    //	  setup
    val aTierOneType = TierOneType(TEST)
    val eTierOneType = TierOneType(TEST + TEST + TEST)

    val properties = Map(
      "_property" -> Property("_property", TEST + TEST))
    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, eTierOneType, aTierOneType, properties)
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeIgnored = {
    //	  setup
    val aTierOneType = TierOneType(TEST)
    val eTierOneType = TierOneType(TEST + TEST + TEST)

    val properties = Map(
      "_property" -> IgnoredProperty("_property"))
    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, eTierOneType, aTierOneType, properties)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeChangedPropertyEqual = {
    //	  setup
    val aTierOneType = TierOneType(TEST)
    val aTierTwoType = TierTwoType(aTierOneType)

    val eTierOneType = TierOneType(TEST + TEST + TEST)
    val eTierTwoType = TierTwoType(eTierOneType)

    val properties = Map(
      "_property._property" -> Property("_property._property", TEST))
    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, eTierTwoType, aTierTwoType, properties)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeChangedPropertyNotEqual = {
    //	  setup
    val aTierOneType = TierOneType(TEST)
    val aTierTwoType = TierTwoType(aTierOneType)

    val eTierOneType = TierOneType(TEST + TEST)
    val eTierTwoType = TierTwoType(eTierOneType)

    val properties = Map(
      "_property._property" -> Property("_property._property", TEST))
    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, eTierTwoType, aTierTwoType, properties)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeChangedPropertyIgnored = {
    //	  setup
    val aTierOneType = TierOneType(TEST)
    val aTierTwoType = TierTwoType(aTierOneType)

    val eTierOneType = TierOneType(TEST + TEST)
    val eTierTwoType = TierTwoType(eTierOneType)

    val properties = Map(
      "_property._property" -> Property("_property._property", TEST + TEST + TEST), "_property._property" -> IgnoredProperty("_property._property"))
    //    method
    val assertResult = fabutObjectAssert.assertObjects(new FabutReport, eTierTwoType, aTierTwoType, properties)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsIgnoredProperty = {
    //	  setup
    val aProperty = Property(TEST, TierOneType(TEST))
    val eProperty = ignored(TIER_ONE_TYPE_PROPERTY)
    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty, eProperty, Map(), new NodesList, true)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsNullProperty = {
    //    setup
    val aProperty = Property(TEST, null)
    val eProperty = isNull(TIER_ONE_TYPE_PROPERTY)
    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty, eProperty, Map(), new NodesList, true)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsNullPropertyNotNull = {
    //    setup
    val aProperty = Property(TEST, TierOneType(TEST))
    val eProperty = isNull(TIER_ONE_TYPE_PROPERTY)
    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty, eProperty, Map(), new NodesList, true)(new FabutReport)
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsNotNullProperty = {
    //    setup
    val aProperty = Property(TEST, TierOneType(TEST))
    val eProperty = notNull(TIER_ONE_TYPE_PROPERTY)
    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty, eProperty, Map(), new NodesList, true)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsNotNullPropertyIsNull = {
    //	  setup
    val aProperty = Property(TEST, null)
    val eProperty = notNull(TIER_ONE_TYPE_PROPERTY)
    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty, eProperty, Map(), new NodesList, true)(new FabutReport)
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsPrimitivePropertyEqual = {
    //	  setup
    val aProperty = Property(TEST, 2000)
    val eProperty = Property(TEST, 2000)
    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty, eProperty, Map(), new NodesList, true)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsPrimitivePropertyNotEqual = {
    //	  setup
    val aProperty = Property(TEST, 2000)
    val eProperty = Property(TEST, 200000)
    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty, eProperty, Map(), new NodesList, true)(new FabutReport)
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyisComplexEqual = {
    //	  setup
    val aProperty = Property(TEST, TierOneType(TEST))
    val eProperty = Property(TEST, TierOneType(TEST))
    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty, eProperty, Map(), new NodesList, true)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPairCaseListType = {
    //	setup
    val assertPair = AssertPair(TEST, List(TEST), List(TEST), SCALA_LIST_TYPE, false)
    //    method	
    val assertResult = fabutObjectAssert.assertPair(assertPair, Map(), new NodesList)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testeAssertPairCaseMapType = {
    //	  setup
    val assertPair = AssertPair(TEST, Map(TEST -> TEST), Map(TEST -> TEST), SCALA_MAP_TYPE, false)
    //    method
    val assertResult = fabutObjectAssert.assertPair(assertPair, Map(), new NodesList)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPairCaseIgnoredType = {
    //	  setup
    val assertPair = AssertPair(TEST, IgnoredType(TEST), IgnoredType(TEST), IGNORED_TYPE, false)
    //    method
    val assertResult = fabutObjectAssert.assertPair(assertPair, Map(), new NodesList)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPairCaseComplexType = {
    //	 setup
    val assertPair = new AssertPair(TEST, TierOneType(TEST), TierOneType(TEST), COMPLEX_TYPE, false)
    //   method
    val assertResult = fabutObjectAssert.assertPair(assertPair, Map(TIER_ONE_TYPE_PROPERTY -> Property(TIER_ONE_TYPE_PROPERTY, TEST)), new NodesList)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPairCasePrimitiveType = {
    //	 setup
    val assertPair = new AssertPair(TEST, 123, 123, PRIMITIVE_TYPE, false)
    //   method
    val assertResult = fabutObjectAssert.assertPair(assertPair, Map(TIER_ONE_TYPE_PROPERTY -> Property(TIER_ONE_TYPE_PROPERTY, TEST)), new NodesList)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testHasInnerPropertiesCaseTrue = {
    //	method
    val assertResult = fabutObjectAssert.hasInnerProperties(TIER_ONE_TYPE_PROPERTY, Map(TIER_ONE_TYPE_PROPERTY -> Property(TIER_ONE_TYPE_PROPERTY, TEST)))
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testHasInnerPropertiesCaseFalse = {

    //    method
    val assertResult = fabutObjectAssert.hasInnerProperties(TEST, Map(TIER_ONE_TYPE_PROPERTY -> Property(TIER_ONE_TYPE_PROPERTY, TEST)))
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertInnerProperty = {
    //	 setup
    val tierOneType = TierOneType(TEST)
    //    method
    val assertResult = fabutObjectAssert.assertInnerProperty(TIER_ONE_TYPE_PROPERTY, tierOneType, Map(TIER_TWO_TYPE_PROPERTY -> Property(TIER_TWO_TYPE_PROPERTY, TEST)), new NodesList, new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testExtractPropertiesWithMatchingParentExist = {
    //	  method
    val properties = Map("a.b.c" -> NullProperty("a"), "a.b.d" -> NullProperty("b"), "c.d.a" -> NullProperty("c"))
    val expected = Map("b.c" -> NullProperty("a"), "b.d" -> NullProperty("b")).asInstanceOf[Map[String, IProperty]]
    //    setup
    val extracts = fabutObjectAssert.extractPropertiesWithMatchingParent("a", properties)

    //    assert
    assertEquals(extracts, expected)

  }

  @Test
  def testExtractPropertiesWithMatchingParentDontExist = {
    //	  method
    val properties = Map("a.b.c" -> NullProperty("a"), "a.b.d" -> NullProperty("b"), "c.d.a" -> NullProperty("c"))
    val expected = Map("b.c" -> NullProperty("a"), "b.d" -> NullProperty("b")).asInstanceOf[Map[String, IProperty]]
    //    setup
    val extracts = fabutObjectAssert.extractPropertiesWithMatchingParent(TEST, properties)

    //    assert
    assertEquals(extracts, Map())

  }

  @Test
  def testAssertSubfieldsExistInChangedProperties = {

    //    setup
    val tierOneType = TierOneType(TEST)
    //    method
    val assertResult = fabutObjectAssert.assertSubfields(tierOneType, Map(TIER_ONE_TYPE_PROPERTY -> Property(TIER_ONE_TYPE_PROPERTY, TEST)), new NodesList)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertSubfieldsDontExistInChangedProperties = {

    //    setup
    val tierOneType = TierOneType(TEST)
    //    method
    val assertResult = fabutObjectAssert.assertSubfields(tierOneType, Map(), new NodesList)(new FabutReport)
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertPrimitivesEqual = {
    //	setup
    val pair = AssertPair(TEST, 200, 200, PRIMITIVE_TYPE, false)
    //    method
    val assertResult = fabutObjectAssert.assertPrimitives(pair)(new FabutReport)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPrimitivesNotEqual = {
    //	setup
    val pair = AssertPair(TEST, 200, 250, PRIMITIVE_TYPE, false)
    //    method
    val assertResult = fabutObjectAssert.assertPrimitives(pair)(new FabutReport)
    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testTakeSnapshotWithNormalClass = {
    //	setup
    val tierOneType = TierOneType(TEST)
    //    method
    val assertResult = fabutObjectAssert.takeSnapshot(new FabutReport, tierOneType)
    val parameterSnapshot = fabutObjectAssert.parameterSnapshot
    assertEquals(parameterSnapshot.size, 1)

    fabutObjectAssert.assertObjects(new FabutReport, parameterSnapshot.head.actual, TierOneType(TEST), Map())
    fabutObjectAssert.assertObjects(new FabutReport, parameterSnapshot.head.expected, TierOneType(TEST), Map())
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testTakeSnapshotWithBadCopyClass = {
    //	setup
    val tierOneType = new BadCopyClass(TEST, TEST)
    //    method
    val assertResult = fabutObjectAssert.takeSnapshot(new FabutReport, tierOneType)
    //    assert
    assertFalse(assertResult)
  }

  /** TEST NODELIST*/
  @Test
  def testNodesListContainsPairTrue = {

    val nodesList = new NodesList

    val actual = TierOneType(TEST)
    val expected = TierOneType(TEST)

    val pair = AssertPair(TEST, expected, actual, COMPLEX_TYPE, false)

    nodesList.addPair(expected, actual)

    val assertResult = nodesList.containsPair(expected, actual)

    assertTrue(assertResult)
  }

  //	 setup

  //   method

  //	 assert

  //
  //  @Test
  //  def testAssertObjectsWithNullObjects() = {
  //
  //    //	setup
  //    val a_b = new B(null, "pera")
  //    val a_a = new A(a_b, "mika")
  //    val a_c = new C(null, "zelja")
  //    a_b.c = a_c
  //
  //    val e_b = new B(null, "pera")
  //    val e_a = new A(e_b, "mika")
  //    val e_c = new C(null, "zelja")
  //    e_b.c = e_c
  //
  //    //	assert
  //    assertObjects(e_a, a_a)
  //  }
  //
  //  @Test(expected = classOf[AssertionError])
  //  def testAssertObjectsWithNullProperties() = {
  //
  //    //	setup
  //    val a_b = new B(null, null)
  //    val a_a = new A(a_b, "mika")
  //    val a_c = new C(a_a, "zelja")
  //    a_b.c = a_c
  //
  //    val e_b = new B(null, "pera")
  //    val e_a = new A(e_b, "mika")
  //    val e_c = new C(e_a, null)
  //    e_b.c = e_c
  //
  //    //	assert
  //    assertObjects(e_a, a_a)
  //  }
  //
  //  /**
  //   *  unused expected property =>  "unsuedProperty"
  //   *  property id is missing
  //   */
  //  @Test(expected = classOf[AssertionError])
  //  def testAssertObjectWithExpectedPropertiesMissingAndUnusedProperty() = {
  //
  //    //	setup
  //    val actualInsideSimple = ObjectInsideSimpleProperty("3301")
  //    val expectedInsideSimple = ObjectInsideSimpleProperty("3301")
  //    val a1 = ObjectInsideSimpleProperty("3301")
  //    val e1 = ObjectInsideSimpleProperty("3333")
  //    val a2 = ObjectInsideSimpleProperty("5000")
  //    val e2 = ObjectInsideSimpleProperty("5001")
  //    val actualSimpleObject = ObjectWithSimpleProperties("mika", 22, actualInsideSimple)
  //    val expectedSimpleObject = ObjectWithSimpleProperties("mika", 221, expectedInsideSimple)
  //    val actual = ObjectWithComplexProperty(900, true, actualSimpleObject, List(a1, a2))
  //    val expected = ObjectWithComplexProperty(900, true, expectedSimpleObject, List(e1, e2))
  //
  //    //	assert
  //    assertObject(
  //      actual,
  //      value("unsuedProperty", 200),
  //      value("state", true),
  //      value("complexObject._username", "mika"),
  //      value("complexObject._age", 22),
  //      value("complexObject.o.id", "3301"),
  //      value("list", List(a1, a2)))
  //  }
  //
  //  @Test
  //  def testAssertObjectWithAllExpectedProperties() = {
  //
  //    //	setup
  //    val actualSimpleObject = ObjectWithSimpleProperties("pera", 22, ObjectInsideSimpleProperty("33"))
  //
  //    //	assert
  //    assertObject(actualSimpleObject, value("_username", "pera"), value("_age", 22), value("o.id", "33"))
  //
  //  }
  //
  //  @Test
  //  def testAssertObjectWithExpectedSimpleList() = {
  //
  //    //	setup
  //    val actualInsideSimple = ObjectInsideSimpleProperty("3301")
  //    val expectedInsideSimple = ObjectInsideSimpleProperty("3301")
  //    val actualSimpleObject = ObjectWithSimpleProperties("mika", 22, actualInsideSimple)
  //    val expectedSimpleObject = ObjectWithSimpleProperties("mika", 22, expectedInsideSimple)
  //    val actual = ObjectWithComplexProperty(900, true, actualSimpleObject, List(1, 5))
  //    val expected = ObjectWithComplexProperty(900, true, expectedSimpleObject, List(1, 4))
  //
  //    //	assert
  //    assertObjects(actual, expected, value("list", List(1, 5)), ignored("list") /*, value("mika", "hello")*/ )
  //  }
  //
  //  @Test
  //  def testAssertObjectWithMapAndListInsideObjects() = {
  //
  //    //	setup
  //    val actual = ObjectWithMap(900, ObjectWithSimpleMap(Map(ObjectInsideSimpleProperty("2") -> 22, 2 -> ObjectInsideSimpleProperty("3301"))), ObjectWithSimpleList(List(1, 2, 3)), Map(1 -> 4))
  //    val expected = ObjectWithMap(900, ObjectWithSimpleMap(Map(2 -> ObjectInsideSimpleProperty("331"), 1 -> 5)), ObjectWithSimpleList(List(1, 2, 3)), Map(1 -> 4))
  //
  //    //	assert
  //    assertObjects(expected, actual, value("complexMapObject.map", Map(ObjectInsideSimpleProperty("2") -> 22, 2 -> ObjectInsideSimpleProperty("3301"))))
  //
  //  }
  //
  //  @Test
  //  def testObjectWithSimpleMapWithAllExpectedProperties() = {
  //
  //    //	setup
  //    val actual = ObjectWithSimpleMap(Map(1 -> 2, 2 -> 6))
  //
  //    //	assert
  //    assertObject(actual, value("map", Map(1 -> 2, 2 -> 6)))
  //  }
  //
  //  @Test
  //  def testCreateCopyPrimitives() = {
  //
  //    //	setup
  //    val actualInsideObject = ObjectInsideSimpleProperty("222")
  //    val actualObject = CopyCaseClass("111", "Petar", actualInsideObject, List(ObjectInsideSimpleProperty("1"), ObjectInsideSimpleProperty("2"), ObjectInsideSimpleProperty("3")), Map(1 -> ObjectInsideSimpleProperty("1")))
  //    val expectedObject = createCopy(actualObject)
  //
  //    //	assert
  //    assertObjects(expectedObject, actualObject)
  //  }
  //
  //  @Test
  //  def testListWithComplexElements() = {
  //
  //    //	setup
  //    val i1 = ObjectInsideSimpleProperty("44")
  //    val i2 = ObjectInsideSimpleProperty("44")
  //    val o1 = ObjectWithSimpleProperties("Petar", 2, i1)
  //    val o2 = ObjectWithSimpleProperties("Petar", 2, i2)
  //
  //    //	assert
  //    assertObjects(List(o1, o1, o1, o1, i1), List(o2, o1, o2, o1, ObjectInsideSimpleProperty("44")))
  //  }
  //
  //  @Test
  //  def testObjectsWithSimpleElements() {
  //
  //    //	setup
  //    val actual = "Hello World"
  //    val expected = "Hello World"
  //
  //    //	assert
  //    assertObjects(expected, actual)
  //  }
  //
  //  @Test
  //  def testMapWithComplexElements() = {
  //
  //    //	setup
  //    val i1 = ObjectInsideSimpleProperty("simple")
  //    val i2 = ObjectInsideSimpleProperty("simple")
  //    val o1 = ObjectWithSimpleProperties("Petar", 2, i1)
  //    val o2 = ObjectWithSimpleProperties("Petar", 2, i2)
  //
  //    //	assert
  //    assertObjects(Map(1 -> i1, 2 -> i2, i1 -> o1), Map(1 -> i2, 2 -> i2, i1 -> o2))
  //  }
  //
  //  @Test(expected = classOf[AssertionError])
  //  def testTypeMismatchException() = {
  //
  //    val actualSimpleObject = ObjectWithSimpleProperties("mika", 22, ObjectInsideSimpleProperty("3301"))
  //    val actual = ObjectWithComplexProperty(900, true, actualSimpleObject, List(1, 4))
  //
  //    assertObjects(actual, List(1, 2, 3))
  //  }
  //
  //  @Test
  //  def test() = {
  //    val x = new EmptyClass
  //    Fabut.assertObjects(x, x)
  //  }

}