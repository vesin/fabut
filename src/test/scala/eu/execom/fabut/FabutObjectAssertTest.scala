package eu.execom.fabut

import eu.execom.fabut.AssertableType._
import eu.execom.fabut.Fabut._
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.model.TrivialClasses._
import eu.execom.fabut.model._
import eu.execom.fabut.pair.AssertPair
import eu.execom.fabut.property.{IProperty, IgnoredProperty, NullProperty, Property}
import eu.execom.fabut.report.FabutReportBuilder
import org.junit.Assert.{assertTrue, _}
import org.junit.Test

class FabutObjectAssertTest extends AbstractFabutObjectAssertTest {

  val TEST = "test"
  val DOT = "."
  val TIER_ONE_TYPE_PROPERTY = "_property"
  val TIER_TWO_TYPE_PROPERTY = "_property._property"
  val LIST_PROPERTY = "_property"
  val EMPTY_STRING = ""


  @Test(expected = classOf[IllegalStateException])
  def testAssertObjectWithPropertiesUndefinedType = {
    //    setup
    val undefinedType = new UndefinedType
    val report = new FabutReportBuilder()

    //    method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(undefinedType, Map())(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test(expected = classOf[IllegalStateException])
  def testAssertObjecsUndefinedType = {
    //    setup
    val actual = new UndefinedType
    val expected = new TierOneType(TEST)
    val report = new FabutReportBuilder()

    //    method
    val assertResult = fabutObjectAssert.assertObjects(expected, actual, Map())(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesIgnoreType = {
    //	setup
    val ignoredType = IgnoredType(TEST)

    //	method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(ignoredType, Map())(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }


  @Test
  def testAssertObjectWithPropertiesNoProperty = {
    //	setup
    val tierOneType = new TierOneType(TEST)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierOneType, Map())(new FabutReportBuilder)

    //	assert
    assertFalse(assertResult)
  }

  /**
   * Test if class doesn't have any properties
   */
  @Test
  def testAssertObjectWithPropertiesNoPropertyEmptyClass = {
    //	setup
    val tierOneType = new EmptyClass

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierOneType, Map())(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNotNullPropertyActuallyNull() = {
    //	setup
    val tierOneType = new TierOneType(null)
    val notNullProperty = notNull(TIER_ONE_TYPE_PROPERTY)
    val ignoredProperty = ignored("PROPERTY")
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> notNullProperty, "PROPERTY" -> ignoredProperty)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNotNullProperty() = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val notNullProperty = Fabut.notNull(TIER_ONE_TYPE_PROPERTY)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> notNullProperty)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNullPropertyActuallyNotNull() = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val isNullProperty = Fabut.isNull(TIER_ONE_TYPE_PROPERTY)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> isNullProperty)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNullProperty() = {
    //	setup
    val tierOneType = new TierOneType(null)
    val isNullProperty = Fabut.isNull(TIER_ONE_TYPE_PROPERTY)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> isNullProperty)
    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesIgnoredProperty() = {
    //	setup
    val tierOneType = new TierOneType(null)
    val ignoredProperty = Fabut.ignored(TIER_ONE_TYPE_PROPERTY)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> ignoredProperty)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyExpectedNullActualNull() = {
    //	setup
    val tierOneType = new TierOneType(null)
    val valueProperty = Fabut.value(TIER_ONE_TYPE_PROPERTY, null)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> valueProperty)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyActualNull() = {
    //	setup
    val tierOneType = new TierOneType(null)
    val property = Fabut.value(TIER_ONE_TYPE_PROPERTY, TEST)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)
    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyExpectedNull() = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val property = Fabut.value(TIER_ONE_TYPE_PROPERTY, null)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyEqual = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val property = Fabut.value(TIER_ONE_TYPE_PROPERTY, TEST)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyNotEqual = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val property = Fabut.value(TIER_ONE_TYPE_PROPERTY, TEST + TEST)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)

    //  method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesTierTwoTypeChangedPropertyEqual = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val tierTwoType = new TierTwoType(tierOneType)
    val property = value(TIER_TWO_TYPE_PROPERTY, TEST)
    val properties = Map(TIER_TWO_TYPE_PROPERTY -> property)

    //	method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierTwoType, properties)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesTierTwoTypeChangedPropertyNotEqual = {
    //	  setup
    val tierOneType = new TierOneType(TEST)
    val tierTwoType = new TierTwoType(tierOneType)
    val property = value(TIER_TWO_TYPE_PROPERTY, TEST + TEST)
    val properties = Map(TIER_TWO_TYPE_PROPERTY -> property)

    //    method
    val report = new FabutReportBuilder
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierTwoType, properties)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesTierTwoTypeWithIgnoredTierOneType = {
    //	setup
    val ignoredType = new IgnoredType(TEST + TEST)
    val tierTwoTypeWithIgnoredType = new TierTwoTypeWithIgnoredType(ignoredType)
    val property = value(TIER_TWO_TYPE_PROPERTY, TEST)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)
    //    method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(tierTwoTypeWithIgnoredType, properties)(new FabutReportBuilder)
    //    assert
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
    val assertResult = fabutObjectAssert.assertObjectWithProperties(actual, properties)(new FabutReportBuilder)

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
    val a3 = ObjectInsideSimpleProperty("200")
    val e3 = ObjectInsideSimpleProperty("200")
    val actualSimpleObject = ObjectWithSimpleProperties("mika", 22, actualInsideSimple)
    val expectedSimpleObject = ObjectWithSimpleProperties("mika", 22, expectedInsideSimple)
    val actual = ObjectWithComplexProperty(900, true, actualSimpleObject, List(a1, a2, a3, actualInsideSimple))
    val expected = ObjectWithComplexProperty(900, true, expectedSimpleObject, List(e1, e2, e3, expectedInsideSimple))

    //    method
    val assertResult = fabutObjectAssert.assertObjects(expected, actual, Map())(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertListElementsEqual = {
    //	  setup
    val actualList = List(1, 2, 3)
    val expectedList = List(1, 2, 3)

    //    method
    val assertResult = fabutObjectAssert.assertList(EMPTY_STRING, 0, actualList, expectedList, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertListElementsNotEqual = {
    //	  setup
    val actualList = List(3, 2, 5, 8)
    val expectedList = List(4, 3, 6, 7)

    //    method	
    val assertResult = fabutObjectAssert.assertList(EMPTY_STRING, 0, actualList, expectedList, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertListElementsDifferentListSize = {
    //	  setup
    val actualList = List(3, 2, 3)
    val expectedList = List(4, 3, 6, 7)

    //    method
    val assertResult = fabutObjectAssert.assertList(EMPTY_STRING, 0, actualList, expectedList, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertListElementsDifferentTypeEqual = {
    //	  setup
    val actualList = List(3, 22l, "pera", true)
    val expectedList = List(3, 22l, "pera", true)

    //    method
    val assertResult = fabutObjectAssert.assertList(EMPTY_STRING, 0, actualList, expectedList, Map(), new NodesList)(new FabutReportBuilder)

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
    val assertResult = fabutObjectAssert.assertObjectWithProperties(listType, properties)(new FabutReportBuilder)

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
    val report = new FabutReportBuilder
    val assertResult = fabutObjectAssert.assertObjectWithProperties(listType, properties)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)

  }

  @Test
  def testAssertMapElementsDifferentSize = {
    //	  setup
    val actualMap: Map[Any, Any] = Map(1 -> 10, 2 -> 20)
    val expectedMap: Map[Any, Any] = Map(1 -> 10, 2 -> 20, 3 -> 30)

    //    method
    val assertResult = fabutObjectAssert.assertMap(EMPTY_STRING, actualMap, expectedMap, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertMapElementsEqual = {
    //	setup
    val actualMap: Map[Any, Any] = Map(1 -> 10, 3 -> 30, 2 -> 20)
    val expectedMap: Map[Any, Any] = Map(1 -> 10, 2 -> 20, 3 -> 30)

    //    method
    val assertResult = fabutObjectAssert.assertMap(EMPTY_STRING, actualMap, expectedMap, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertMapElementsNotEqual = {
    //	setup
    val actualMap: Map[Any, Any] = Map(1 -> 10, 3 -> 21, 2 -> 31)
    val expectedMap: Map[Any, Any] = Map(1 -> 10, 2 -> 20, 3 -> 30)

    //    method
    val assertResult = fabutObjectAssert.assertMap(EMPTY_STRING, actualMap, expectedMap, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  /** TODO ASSERT COMPLEX ELEMENTS MAP */

  @Test
  def testAssertMapElementsTierOneTypeValuesEqual = {
    //	setup
    val actualMap: Map[Any, Any] = Map(1 -> new TierOneType(TEST), 3 -> new TierOneType(TEST + TEST + TEST), 2 -> new TierOneType(TEST + TEST))
    val expectedMap: Map[Any, Any] = Map(1 -> new TierOneType(TEST), 2 -> new TierOneType(TEST + TEST), 3 -> new TierOneType(TEST + TEST + TEST))

    //    method
    val assertResult = fabutObjectAssert.assertMap(EMPTY_STRING, actualMap, expectedMap, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertMapElementsTierOneTypeValuesNotEqual = {
    //	setup
    val actualMap: Map[Any, Any] = Map(1 -> new TierOneType(TEST + TEST), 3 -> new TierOneType(TEST + TEST + TEST), 2 -> new TierOneType(TEST + TEST))
    val expectedMap: Map[Any, Any] = Map(1 -> new TierOneType(TEST), 2 -> new TierOneType(TEST), 3 -> new TierOneType(TEST + TEST + TEST))

    //    method
    val assertResult = fabutObjectAssert.assertMap(EMPTY_STRING, actualMap, expectedMap, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertMapElementsTierOneTypeKeysEqual = {
    //	setup

    val t1 = new TierOneType(TEST)
    val t2 = new TierOneType(TEST + TEST)
    val t3 = new TierOneType(TEST + TEST + TEST)

    val actualMap: Map[Any, Any] = Map(
      t1 -> TEST,
      t2 -> (TEST + TEST),
      t3 -> (TEST + TEST + TEST))

    val expectedMap: Map[Any, Any] = Map(
      t2 -> (TEST + TEST),
      t1 -> TEST,
      t3 -> (TEST + TEST + TEST))

    //    method

    val report = new FabutReportBuilder
    val assertResult = fabutObjectAssert.assertMap(EMPTY_STRING, actualMap, expectedMap, Map(), new NodesList)(report)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeEqual = {
    //	  setup
    val actual = new TierOneType(TEST)
    val expected = new TierOneType(TEST)
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.assertObjects(expected, actual, Map())(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeNotEqual = {
    //	  setup
    val actual = new TierOneType(TEST)
    val expected = new TierOneType(TEST + TEST)
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.assertObjects(expected, actual, Map())(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeEqual = {
    //	  setup
    val actual = new TierTwoType(new TierOneType(TEST))
    val expected = new TierTwoType(new TierOneType(TEST))
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.assertObjects(expected, actual, Map())(report)

    //    assert  
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeNotEqual = {
    //	  setup
    val actual = new TierTwoType(new TierOneType(TEST + TEST))
    val expected = new TierTwoType(new TierOneType(TEST))
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.assertObjects(expected, actual, Map())(report)

    //    assert  
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithSimplePropertiesEqual() = {
    //    setup
    val actual = ObjectWithSimpleProperties("pera", 43, ObjectInsideSimpleProperty("marko"))
    val expected = ObjectWithSimpleProperties("pera", 43, ObjectInsideSimpleProperty("marko"))
    val report = new FabutReportBuilder

    //	  method
    val assertResult = fabutObjectAssert.assertObjects(expected, actual, Map())(report)

    //	  assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithSimplePropertiesNotEqual() = {
    //    setup
    val actual = ObjectWithSimpleProperties("pera", 43, ObjectInsideSimpleProperty("markoooo"))
    val expected = ObjectWithSimpleProperties("pera", 43, ObjectInsideSimpleProperty("marko"))

    val report = new FabutReportBuilder

    //	  method
    val assertResult = fabutObjectAssert.assertObjects(expected, actual, Map())(report)

    //	  assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsTrivialClassesWithRecursiveGraphsEqual = {
    //	setup
    val actual_a = new A(null, "mika")
    val actual_b = new B(actual_a, "pera")
    actual_a.b = actual_b

    val expected_a = new A(null, "mika")
    val expected_b = new B(expected_a, "pera")
    expected_a.b = expected_b

    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.assertObjects(expected_a, actual_a, Map())(report)

    //    assert
    assertTrue(assertResult)
  }

  // FIXME case when you forget to add types of elements, check it out
  @Test
  def testAssertObjectsTierTypesRecursiveGraphEqual = {
    //	  setup
    val aTierThreeType = new TierThreeType(null)
    val aTierFourType = TierFourType(aTierThreeType)
    val aTierFiveType = TierFiveType(aTierFourType, TEST)
    val aTierSixType = TierSixType(aTierFiveType)
    aTierThreeType._property = aTierSixType

    val eTierThreeType = new TierThreeType(null)
    val eTierFourType = TierFourType(eTierThreeType)
    val eTierFiveType = TierFiveType(eTierFourType, TEST)
    val eTierSixType = TierSixType(eTierFiveType)
    eTierThreeType._property = eTierSixType

    val report = new FabutReportBuilder

    //	method
    val assertResult = fabutObjectAssert.assertObjects(eTierSixType, aTierSixType, Map())(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTypesWithRecursiveGraphNotEqual = {
    //	setup
    val aTierThreeType = new TierThreeType(null)
    val aTierFourType = TierFourType(aTierThreeType)
    val aTierFiveType = TierFiveType(aTierFourType, TEST + TEST)
    val aTierSixType = TierSixType(aTierFiveType)
    aTierThreeType._property = aTierSixType

    //    method
    val eTierThreeType = new TierThreeType(null)
    val eTierFourType = TierFourType(eTierThreeType)
    val eTierFiveType = TierFiveType(eTierFourType, TEST)
    val eTierSixType = TierSixType(eTierFiveType)
    eTierThreeType._property = eTierSixType
    val report = new FabutReportBuilder

    //    assert
    val assertResult = fabutObjectAssert.assertObjects(eTierThreeType, aTierThreeType, Map())(report)

    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesTierTypesRecursiveGraphEqual = {
    //	setup
    val aTierThreeType = new TierThreeType(null)
    val aTierFourType = TierFourType(aTierThreeType)
    val aTierFiveType = TierFiveType(aTierFourType, TEST)
    val aTierSixType = TierSixType(aTierFiveType)
    aTierThreeType._property = aTierSixType

    val eTierThreeType = new TierThreeType(null)
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

    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.assertObjectWithProperties(aTierSixType, properties)(report)

    //    assert
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

    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.assertObjects(expected_a, actual_a, Map())(report)

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

    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.assertObjects(e_a1, a_a1, Map())(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeChangedPropertyEqual = {
    //	  setup
    val aTierOneType = new TierOneType(TEST + TEST)

    val eTierOneType = new TierOneType(TEST)

    val properties = Map(
      "_property" -> Property("_property", TEST + TEST))

    val report = new FabutReportBuilder
    //    method

    val assertResult = fabutObjectAssert.assertObjects(eTierOneType, aTierOneType, properties)(report)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeChangedPropertyNotEqual = {
    //	  setup
    val aTierOneType = new TierOneType(TEST)
    val eTierOneType = new TierOneType(TEST + TEST + TEST)

    val properties = Map(
      "_property" -> Property("_property", TEST + TEST))

    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.assertObjects(eTierOneType, aTierOneType, properties)(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeIgnored = {
    //	  setup
    val aTierOneType = new TierOneType(TEST)
    val eTierOneType = new TierOneType(TEST + TEST + TEST)

    val properties = Map(
      "_property" -> IgnoredProperty("_property"))

    val report = new FabutReportBuilder
    //    method
    val assertResult = fabutObjectAssert.assertObjects(eTierOneType, aTierOneType, properties)(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeChangedPropertyEqual = {
    //	  setup
    val aTierOneType = new TierOneType(TEST + TEST)
    val aTierTwoType = TierTwoType(aTierOneType)

    val eTierOneType = new TierOneType(TEST + TEST + TEST)
    val eTierTwoType = TierTwoType(eTierOneType)

    val property = Fabut.value("_property", new TierOneType(TEST + TEST))
    val properties = Fabut.createExpectedPropertiesMap(property)

    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.assertObjects(eTierTwoType, aTierTwoType, properties)(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeChangedPropertyNotEqual = {
    //	  setup
    val aTierOneType = new TierOneType(TEST)
    val aTierTwoType = TierTwoType(aTierOneType)

    val eTierOneType = new TierOneType(TEST + TEST)
    val eTierTwoType = TierTwoType(eTierOneType)

    val property = Fabut.value("_property", new TierOneType(TEST + TEST + TEST))
    val properties = Fabut.createExpectedPropertiesMap(property)

    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.assertObjects(eTierTwoType, aTierTwoType, properties)(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeChangedPropertyIgnored = {
    //	  setup
    val aTierOneType = new TierOneType(TEST)
    val aTierTwoType = TierTwoType(aTierOneType)

    val eTierOneType = new TierOneType(TEST + TEST)
    val eTierTwoType = TierTwoType(eTierOneType)

    val property = Fabut.value("_property", new TierOneType(TEST + TEST + TEST))
    val ignoredProperty = Fabut.ignored("_property")
    val properties = Fabut.createExpectedPropertiesMap(property, ignoredProperty)

    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.assertObjects(eTierTwoType, aTierTwoType, properties)(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsIgnoredProperty = {
    //	  setup
    val aProperty = Property(TEST, new TierOneType(TEST))
    val eProperty = ignored(TIER_ONE_TYPE_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty.path, aProperty, eProperty, Map(), new NodesList, true)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsNullProperty = {
    //    setup
    val eProperty = isNull(TIER_ONE_TYPE_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert.assertProperty(TEST, null, eProperty, Map(), new NodesList, true)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsNullPropertyNotNull = {
    //    setup
    val aProperty = Property(TEST, new TierOneType(TEST))
    val eProperty = isNull(TIER_ONE_TYPE_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty.path, aProperty.value, eProperty, Map(), new NodesList, true)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsNotNullProperty = {
    //    setup
    val aProperty = Property(TEST, new TierOneType(TEST))
    val eProperty = notNull(TIER_ONE_TYPE_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty.path, aProperty.value, eProperty, Map(), new NodesList, true)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsNotNullPropertyIsNull = {
    //	  setup
    val aProperty = Property(TEST, null)
    val eProperty = notNull(TIER_ONE_TYPE_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty.path, aProperty.value, eProperty, Map(), new NodesList, true)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsPrimitivePropertyEqual = {
    //	  setup
    val aProperty = Property(TEST, 2000)
    val eProperty = Property(TEST, 2000)

    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty.path, aProperty.value, eProperty, Map(), new NodesList, true)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsPrimitivePropertyNotEqual = {
    //	  setup
    val aProperty = Property(TEST, 2000)
    val eProperty = Property(TEST, 200000)

    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty.path, aProperty.value, eProperty, Map(), new NodesList, true)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyisComplexEqual = {
    //	  setup
    val aProperty = Property(TEST, new TierOneType(TEST))
    val eProperty = Property(TEST, new TierOneType(TEST))

    //    method
    val assertResult = fabutObjectAssert.assertProperty(aProperty.path, aProperty.value, eProperty, Map(), new NodesList, true)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPairCaseListType = {
    //	setup
    val assertPair = AssertPair(TEST, List(TEST), List(TEST), SCALA_LIST_TYPE, false)

    //    method	
    val assertResult = fabutObjectAssert.assertPair(EMPTY_STRING, assertPair, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testeAssertPairCaseMapType = {
    //	  setup
    val assertPair = AssertPair(TEST, Map(TEST -> TEST), Map(TEST -> TEST), SCALA_MAP_TYPE, false)

    //    method
    val assertResult = fabutObjectAssert.assertPair(EMPTY_STRING, assertPair, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPairCaseIgnoredType = {
    //	  setup
    val assertPair = AssertPair(TEST, IgnoredType(TEST), IgnoredType(TEST), IGNORED_TYPE, false)

    //    method
    val assertResult = fabutObjectAssert.assertPair(EMPTY_STRING, assertPair, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPairCaseComplexType = {
    //	 setup
    val assertPair = new AssertPair(TEST, new TierOneType(TEST), new TierOneType(TEST), COMPLEX_TYPE, false)

    //   method
    val assertResult = fabutObjectAssert.assertPair(EMPTY_STRING, assertPair, Map(TIER_ONE_TYPE_PROPERTY -> Property(TIER_ONE_TYPE_PROPERTY, TEST)), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPairCasePrimitiveType = {
    //	 setup
    val assertPair = new AssertPair(TEST, 123, 123, PRIMITIVE_TYPE, false)

    //   method
    val assertResult = fabutObjectAssert.assertPair(EMPTY_STRING, assertPair, Map(TIER_ONE_TYPE_PROPERTY -> Property(TIER_ONE_TYPE_PROPERTY, TEST)), new NodesList)(new FabutReportBuilder)

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
    val tierOneType = new TierOneType(TEST)

    //    method
    val assertResult = fabutObjectAssert.assertInnerProperty(TIER_ONE_TYPE_PROPERTY, tierOneType, Map(TIER_TWO_TYPE_PROPERTY -> Property(TIER_TWO_TYPE_PROPERTY, TEST)), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testExtractPropertiesWithMatchingParentExist = {
    //	  method
    val properties = Map("a.b.c" -> NullProperty("a"), "a.b.d" -> NullProperty("b"), "c.d.a" -> NullProperty("c"))

    val expected = Map("b.c" -> NullProperty("a"), "b.d" -> NullProperty("b")).asInstanceOf[Map[String, IProperty]]

    //    setup
    val extracts = fabutObjectAssert.extractInnerPropertiesByParentName("a", properties)

    //    assert
    assertEquals(extracts, expected)
  }

  @Test
  def testExtractPropertiesWithMatchingParentDontExist = {
    //	  method
    val properties = Map("a.b.c" -> NullProperty("a"), "a.b.d" -> NullProperty("b"), "c.d.a" -> NullProperty("c"))
    val expected = Map("b.c" -> NullProperty("a"), "b.d" -> NullProperty("b")).asInstanceOf[Map[String, IProperty]]

    //    setup
    val extracts = fabutObjectAssert.extractInnerPropertiesByParentName(TEST, properties)

    //    assert
    assertEquals(extracts, Map())
  }

  @Test
  def testAssertSubfieldsExistInChangedProperties = {
    //    setup
    val actual = new TierOneType(TEST)
    val expected = new TierOneType(TEST + TEST)
    val assertPair = AssertPair("", expected, actual, COMPLEX_TYPE, true)

    //    method
    val assertResult = fabutObjectAssert.assertSubfields(EMPTY_STRING, assertPair, Map(TIER_ONE_TYPE_PROPERTY -> Property(TIER_ONE_TYPE_PROPERTY, TEST)), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertSubfieldsDontExistInChangedProperties = {
    //    setup
    val actual = new TierOneType(TEST)
    val expected = new TierOneType(TEST + TEST)
    val assertPair = AssertPair("", actual, expected, COMPLEX_TYPE, true)

    //    method
    val assertResult = fabutObjectAssert.assertSubfields(EMPTY_STRING, assertPair, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertPrimitivesEqual = {
    //	setup
    val pair = AssertPair(TEST, 200, 200, PRIMITIVE_TYPE, false)

    //    method
    val assertResult = fabutObjectAssert.assertPrimitives(pair)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPrimitivesNotEqual = {
    //	setup
    val pair = AssertPair(TEST, 200, 250, PRIMITIVE_TYPE, false)

    //    method
    val assertResult = fabutObjectAssert.assertPrimitives(pair)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testTakeSnapshotWithNormalClass = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.takeSnapshot(tierOneType)(report)
    val parameterSnapshot = fabutObjectAssert.parameterSnapshot
    assertEquals(parameterSnapshot.size, 1)

    fabutObjectAssert.assertObjects(parameterSnapshot.head.actual, new TierOneType(TEST), Map())(report)
    fabutObjectAssert.assertObjects(parameterSnapshot.head.expected, new TierOneType(TEST), Map())(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testTakeSnapshotWithBadCopyClass = {
    //	setup
    val tierOneType = new BadCopyClass(TEST, TEST)
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert.takeSnapshot(tierOneType)(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testNodesListContainsPairTrue = {
    //	setup
    val nodesList = new NodesList

    val actual = new TierOneType(TEST)
    val expected = new TierOneType(TEST)

    val pair = AssertPair(TEST, expected, actual, COMPLEX_TYPE, false)

    nodesList.addPair(expected, actual)

    //    method
    val assertResult = nodesList.containsPair(expected, actual)

    //    assert
    assertTrue(assertResult)
  }

}