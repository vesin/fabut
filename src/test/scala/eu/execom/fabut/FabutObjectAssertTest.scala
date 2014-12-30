package eu.execom.fabut

import eu.execom.fabut.AssertableType._
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
  val TIER_ONE_TYPE_PROPERTY = "property"
  val TIER_TWO_TYPE_PROPERTY = "property.property"
  val LIST_PROPERTY = "property"
  val EMPTY_STRING = ""
  val BOOLEAN_PROPERTY = true


  @Test
  def testAssertObjectWithPropertiesUndefinedType(): Unit = {
    //    setup
    val undefinedType = new UndefinedType
    val report = new FabutReportBuilder()

    //    method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(undefinedType, Map())(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsUndefinedType(): Unit = {
    //    setup
    val actual = new UndefinedType
    val expected = new TierOneType(TEST)
    val report = new FabutReportBuilder()

    //    method
    val assertResult = fabutObjectAssert().assertObjects(expected, actual, Map())(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesIgnoreType(): Unit = {
    //	setup
    val ignoredType = IgnoredType(TEST)

    //	method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(ignoredType, Map())(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }


  @Test
  def testAssertObjectWithPropertiesNoProperty(): Unit = {
    //	setup
    val tierOneType = new TierOneType(TEST)

    //  method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierOneType, Map())(new FabutReportBuilder)

    //	assert
    assertFalse(assertResult)
  }

  /**
   * Test if class doesn't have any properties
   */
  @Test
  def testAssertObjectWithPropertiesNoPropertyEmptyClass(): Unit = {
    //	setup
    val tierOneType = new EmptyClass

    //  method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierOneType, Map())(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNotNullPropertyActuallyNull(): Unit = {
    //	setup
    val tierOneType = new TierOneType(null)
    val notNullProperty = notNull(TIER_ONE_TYPE_PROPERTY)
    val ignoredProperty = ignored("PROPERTY")
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> notNullProperty, "PROPERTY" -> ignoredProperty)

    //  method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNotNullProperty(): Unit = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val notNullProperty = notNull(TIER_ONE_TYPE_PROPERTY)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> notNullProperty)

    //  method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNullPropertyActuallyNotNull(): Unit = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val isNullProperty = isNull(TIER_ONE_TYPE_PROPERTY)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> isNullProperty)

    //  method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesNullProperty(): Unit = {
    //	setup
    val tierOneType = new TierOneType(null)
    val isNullProperty = isNull(TIER_ONE_TYPE_PROPERTY)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> isNullProperty)
    //  method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesIgnoredProperty(): Unit = {
    //	setup
    val tierOneType = new TierOneType(null)
    val ignoredProperty = ignored(TIER_ONE_TYPE_PROPERTY)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> ignoredProperty)

    //  method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyExpectedNullActualNull(): Unit = {
    //	setup
    val tierOneType = new TierOneType(null)
    val valueProperty = value(TIER_ONE_TYPE_PROPERTY, null)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> valueProperty)

    //  method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyActualNull(): Unit = {
    //	setup
    val tierOneType = new TierOneType(null)
    val property = value(TIER_ONE_TYPE_PROPERTY, TEST)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)
    //  method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyExpectedNull(): Unit = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val property = value(TIER_ONE_TYPE_PROPERTY, null)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)

    //  method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyEqual(): Unit = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val property = value(TIER_ONE_TYPE_PROPERTY, TEST)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)

    //  method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithPropertiesChangedPropertyNotEqual(): Unit = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val property = value(TIER_ONE_TYPE_PROPERTY, TEST + TEST)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)

    //  method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierOneType, properties)(new FabutReportBuilder)

    //	assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesTierTwoTypeChangedPropertyEqual(): Unit = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val tierTwoType = new TierTwoType(tierOneType)
    val property = value(TIER_TWO_TYPE_PROPERTY, TEST)
    val properties = Map(TIER_TWO_TYPE_PROPERTY -> property)

    //	method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierTwoType, properties)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesTierTwoTypeChangedPropertyNotEqual(): Unit = {
    //	  setup
    val tierOneType = new TierOneType(TEST)
    val tierTwoType = new TierTwoType(tierOneType)
    val property = value(TIER_TWO_TYPE_PROPERTY, TEST + TEST)
    val properties = Map(TIER_TWO_TYPE_PROPERTY -> property)
    val report = new FabutReportBuilder

    //    method

    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierTwoType, properties)(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesTierTwoTypeWithIgnoredTierOneType(): Unit = {
    //	setup
    val ignoredType = new IgnoredType(TEST + TEST)
    val tierTwoTypeWithIgnoredType = new TierTwoTypeWithIgnoredType(ignoredType)
    val property = value(TIER_TWO_TYPE_PROPERTY, TEST)
    val properties = Map(TIER_ONE_TYPE_PROPERTY -> property)
    val report = new FabutReportBuilder
    //    method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(tierTwoTypeWithIgnoredType, properties)(report)
    //    assert
    assertTrue(assertResult)

  }

  @Test
  def testAssertObjectsWithPropertiesComplexObjectEqualValues(): Unit = {
    //	setup
    val actualInsideSimple = ObjectInsideSimpleProperty("3301")
    val a1 = ObjectInsideSimpleProperty("3301")
    val a2 = ObjectInsideSimpleProperty("5000")
    val e1 = ObjectInsideSimpleProperty("3301")
    val e2 = ObjectInsideSimpleProperty("5000")
    val actualSimpleObject = ObjectWithSimpleProperties("mika", 22, actualInsideSimple)
    val actual = ObjectWithComplexProperty(900, BOOLEAN_PROPERTY, actualSimpleObject, List(a1, a2))

    val properties = Map(
      "id" -> Property("id", 900),
      "state" -> Property("state", BOOLEAN_PROPERTY),
      "complexObject.username" -> Property("complexObject.username", "mika"),
      "complexObject.age" -> Property("complexObject.age", 22l),
      "complexObject.o._id" -> Property("complexObject.o._id", "3301"),
      "list" -> Property("list", List(e1, e2)))
    val report = new FabutReportBuilder

    //	method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(actual, properties)(report)
    //	assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithComplexPropertiesAndComplexListEqual(): Unit = {
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
    val actual = ObjectWithComplexProperty(900, BOOLEAN_PROPERTY, actualSimpleObject, List(a1, a2, a3, actualInsideSimple))
    val expected = ObjectWithComplexProperty(900, BOOLEAN_PROPERTY, expectedSimpleObject, List(e1, e2, e3, expectedInsideSimple))
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert().assertObjects(expected, actual, Map())(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertListElementsEqual(): Unit = {
    //	  setup
    val actualList = List(1, 2, 3)
    val expectedList = List(1, 2, 3)
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert().assertList(EMPTY_STRING, 0, actualList, expectedList, Map(), new NodesList)(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertListElementsNotEqual(): Unit = {
    //	  setup
    val actualList = List(3, 2, 5, 8)
    val expectedList = List(4, 3, 6, 7)

    //    method	
    val assertResult = fabutObjectAssert().assertList(EMPTY_STRING, 0, actualList, expectedList, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertListElementsDifferentListSize(): Unit = {
    //	  setup
    val actualList = List(3, 2, 3)
    val expectedList = List(4, 3, 6, 7)

    //    method
    val assertResult = fabutObjectAssert().assertList(EMPTY_STRING, 0, actualList, expectedList, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertListElementsDifferentTypeEqual(): Unit = {
    //	  setup
    val actualList = List(3, 22l, "pera", true)
    val expectedList = List(3, 22l, "pera", true)

    //    method
    val assertResult = fabutObjectAssert().assertList(EMPTY_STRING, 0, actualList, expectedList, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)

  }

  @Test
  def testAssertObjectsWithPropertiesListTypeEqual(): Unit = {
    //    setup
    val listType = new ListType(List(6, 5, 4, 3, 2, 1))
    val property = value(LIST_PROPERTY, List(6, 5, 4, 3, 2, 1))
    val properties = Map(LIST_PROPERTY -> property)

    //    method
    val assertResult = fabutObjectAssert().assertObjectWithProperties(listType, properties)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesListTypeNotEqual(): Unit = {
    //	  setup
    val listType = new ListType(List(6, 5, 0, 1, 2, 1))
    val property = value(LIST_PROPERTY, List(6, 5, 4, 3, 2, 1))
    val properties = Map(LIST_PROPERTY -> property)

    //    method
    val report = new FabutReportBuilder
    val assertResult = fabutObjectAssert().assertObjectWithProperties(listType, properties)(report)

    //    assert
    assertFalse(assertResult)

  }

  @Test
  def testAssertMapElementsDifferentSize(): Unit = {
    //	  setup
    val actualMap: Map[Any, Any] = Map(1 -> 10, 2 -> 20)
    val expectedMap: Map[Any, Any] = Map(1 -> 10, 2 -> 20, 3 -> 30)

    //    method
    val assertResult = fabutObjectAssert().assertMap(EMPTY_STRING, actualMap, expectedMap, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertMapElementsEqual(): Unit = {
    //	setup
    val actualMap: Map[Any, Any] = Map(1 -> 10, 3 -> 30, 2 -> 20)
    val expectedMap: Map[Any, Any] = Map(1 -> 10, 2 -> 20, 3 -> 30)

    //    method
    val assertResult = fabutObjectAssert().assertMap(EMPTY_STRING, actualMap, expectedMap, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertMapElementsNotEqual(): Unit = {
    //	setup
    val actualMap: Map[Any, Any] = Map(1 -> 10, 3 -> 21, 2 -> 31)
    val expectedMap: Map[Any, Any] = Map(1 -> 10, 2 -> 20, 3 -> 30)

    //    method
    val assertResult = fabutObjectAssert().assertMap(EMPTY_STRING, actualMap, expectedMap, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  /** TODO ASSERT COMPLEX ELEMENTS MAP */

  @Test
  def testAssertMapElementsTierOneTypeValuesEqual(): Unit = {
    //	setup
    val actualMap: Map[Any, Any] = Map(1 -> new TierOneType(TEST), 3 -> new TierOneType(TEST + TEST + TEST), 2 -> new TierOneType(TEST + TEST))
    val expectedMap: Map[Any, Any] = Map(1 -> new TierOneType(TEST), 2 -> new TierOneType(TEST + TEST), 3 -> new TierOneType(TEST + TEST + TEST))

    //    method
    val assertResult = fabutObjectAssert().assertMap(EMPTY_STRING, actualMap, expectedMap, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertMapElementsTierOneTypeValuesNotEqual(): Unit = {
    //	setup
    val actualMap: Map[Any, Any] = Map(1 -> new TierOneType(TEST + TEST), 3 -> new TierOneType(TEST + TEST + TEST), 2 -> new TierOneType(TEST + TEST))
    val expectedMap: Map[Any, Any] = Map(1 -> new TierOneType(TEST), 2 -> new TierOneType(TEST), 3 -> new TierOneType(TEST + TEST + TEST))

    //    method
    val assertResult = fabutObjectAssert().assertMap(EMPTY_STRING, actualMap, expectedMap, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertMapElementsTierOneTypeKeysEqual(): Unit = {
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
    val assertResult = fabutObjectAssert().assertMap(EMPTY_STRING, actualMap, expectedMap, Map(), new NodesList)(report)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeEqual(): Unit = {
    //	  setup
    val actual = new TierOneType(TEST)
    val expected = new TierOneType(TEST)
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert().assertObjects(expected, actual, Map())(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeNotEqual(): Unit = {
    //	  setup
    val actual = new TierOneType(TEST)
    val expected = new TierOneType(TEST + TEST)
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert().assertObjects(expected, actual, Map())(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeEqual(): Unit = {
    //	  setup
    val actual = new TierTwoType(new TierOneType(TEST))
    val expected = new TierTwoType(new TierOneType(TEST))
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert().assertObjects(expected, actual, Map())(report)

    //    assert  
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeNotEqual(): Unit = {
    //	  setup
    val actual = new TierTwoType(new TierOneType(TEST + TEST))
    val expected = new TierTwoType(new TierOneType(TEST))
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert().assertObjects(expected, actual, Map())(report)

    //    assert  
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectWithSimplePropertiesEqual(): Unit = {
    //    setup
    val actual = ObjectWithSimpleProperties("pera", 43, ObjectInsideSimpleProperty("marko"))
    val expected = ObjectWithSimpleProperties("pera", 43, ObjectInsideSimpleProperty("marko"))
    val report = new FabutReportBuilder

    //	  method
    val assertResult = fabutObjectAssert().assertObjects(expected, actual, Map())(report)
    //	  assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectWithSimplePropertiesNotEqual(): Unit = {
    //    setup
    val actual = ObjectWithSimpleProperties("pera", 43, ObjectInsideSimpleProperty("markoooo"))
    val expected = ObjectWithSimpleProperties("pera", 43, ObjectInsideSimpleProperty("marko"))

    val report = new FabutReportBuilder

    //	  method
    val assertResult = fabutObjectAssert().assertObjects(expected, actual, Map())(report)

    //	  assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsTrivialClassesWithRecursiveGraphsEqual(): Unit = {
    //	setup
    val actual_a = new A(null, "mika")
    val actual_b = new B(actual_a, "pera")
    actual_a.b = actual_b

    val expected_a = new A(null, "mika")
    val expected_b = new B(expected_a, "pera")
    expected_a.b = expected_b

    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert().assertObjects(expected_a, actual_a, Map())(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTypesRecursiveGraphEqual(): Unit = {
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
    val assertResult = fabutObjectAssert().assertObjects(eTierSixType, aTierSixType, Map())(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTypesWithRecursiveGraphNotEqual(): Unit = {
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
    val assertResult = fabutObjectAssert().assertObjects(eTierThreeType, aTierThreeType, Map())(report)

    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsWithPropertiesTierTypesRecursiveGraphEqual(): Unit = {
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
    val assertResult = fabutObjectAssert().assertObjectWithProperties(aTierSixType, properties)(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testWithRecursiveGraphWithTrivialObjects(): Unit = {
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
    val assertResult = fabutObjectAssert().assertObjects(expected_a, actual_a, Map())(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTrivialClassesRecursiveIsomorphicGraphs(): Unit = {
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
    val assertResult = fabutObjectAssert().assertObjects(e_a1, a_a1, Map())(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeChangedPropertyEqual(): Unit = {
    //	  setup
    val aTierOneType = new TierOneType(TEST + TEST)

    val eTierOneType = new TierOneType(TEST)

    val properties = Map(
      "property" -> Property("property", TEST + TEST))

    val report = new FabutReportBuilder
    //    method

    val assertResult = fabutObjectAssert().assertObjects(eTierOneType, aTierOneType, properties)(report)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeChangedPropertyNotEqual(): Unit = {
    //	  setup
    val aTierOneType = new TierOneType(TEST)
    val eTierOneType = new TierOneType(TEST + TEST + TEST)

    val properties = Map(
      "_property" -> Property("_property", TEST + TEST))

    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert().assertObjects(eTierOneType, aTierOneType, properties)(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsTierOneTypeIgnored(): Unit = {
    //	  setup
    val aTierOneType = new TierOneType(TEST)
    val eTierOneType = new TierOneType(TEST + TEST + TEST)

    val properties = Map(
      "property" -> IgnoredProperty("property"))

    val report = new FabutReportBuilder
    //    method
    val assertResult = fabutObjectAssert().assertObjects(eTierOneType, aTierOneType, properties)(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeChangedPropertyEqual(): Unit = {
    //	  setup
    val aTierOneType = new TierOneType(TEST + TEST)
    val aTierTwoType = TierTwoType(aTierOneType)

    val eTierOneType = new TierOneType(TEST + TEST + TEST)
    val eTierTwoType = TierTwoType(eTierOneType)

    val property = value("property", new TierOneType(TEST + TEST))
    val properties = createExpectedPropertiesMap(property)

    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert().assertObjects(eTierTwoType, aTierTwoType, properties)(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeChangedPropertyNotEqual(): Unit = {
    //	  setup
    val aTierOneType = new TierOneType(TEST)
    val aTierTwoType = TierTwoType(aTierOneType)

    val eTierOneType = new TierOneType(TEST + TEST)
    val eTierTwoType = TierTwoType(eTierOneType)

    val property = value("_property", new TierOneType(TEST + TEST + TEST))
    val properties = createExpectedPropertiesMap(property)

    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert().assertObjects(eTierTwoType, aTierTwoType, properties)(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertObjectsTierTwoTypeChangedPropertyIgnored(): Unit = {
    //	  setup
    val aTierOneType = new TierOneType(TEST)
    val aTierTwoType = TierTwoType(aTierOneType)

    val eTierOneType = new TierOneType(TEST + TEST)
    val eTierTwoType = TierTwoType(eTierOneType)

    val property = value("property", new TierOneType(TEST + TEST + TEST))
    val ignoredProperty = ignored("property")
    val properties = createExpectedPropertiesMap(property, ignoredProperty)

    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert().assertObjects(eTierTwoType, aTierTwoType, properties)(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsIgnoredProperty(): Unit = {
    //	  setup
    val aProperty = Property(TEST, new TierOneType(TEST))
    val eProperty = ignored(TIER_ONE_TYPE_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert().assertProperty(aProperty.path, aProperty, eProperty, Map(), new NodesList, BOOLEAN_PROPERTY)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsNullProperty(): Unit = {
    //    setup
    val eProperty = isNull(TIER_ONE_TYPE_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert().assertProperty(TEST, null, eProperty, Map(), new NodesList, BOOLEAN_PROPERTY)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsNullPropertyNotNull(): Unit = {
    //    setup
    val aProperty = Property(TEST, new TierOneType(TEST))
    val eProperty = isNull(TIER_ONE_TYPE_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert().assertProperty(aProperty.path, aProperty.value, eProperty, Map(), new NodesList, BOOLEAN_PROPERTY)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsNotNullProperty(): Unit = {
    //    setup
    val aProperty = Property(TEST, new TierOneType(TEST))
    val eProperty = notNull(TIER_ONE_TYPE_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert().assertProperty(aProperty.path, aProperty.value, eProperty, Map(), new NodesList, BOOLEAN_PROPERTY)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsNotNullPropertyIsNull(): Unit = {
    //	  setup
    val aProperty = Property(TEST, null)
    val eProperty = notNull(TIER_ONE_TYPE_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert().assertProperty(aProperty.path, aProperty.value, eProperty, Map(), new NodesList, BOOLEAN_PROPERTY)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsPrimitivePropertyEqual(): Unit = {
    //	  setup
    val aProperty = Property(TEST, 2000)
    val eProperty = Property(TEST, 2000)

    //    method
    val assertResult = fabutObjectAssert().assertProperty(aProperty.path, aProperty.value, eProperty, Map(), new NodesList, BOOLEAN_PROPERTY)(new FabutReportBuilder)
    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyIsPrimitivePropertyNotEqual(): Unit = {
    //	  setup
    val aProperty = Property(TEST, 2000)
    val eProperty = Property(TEST, 200000)

    //    method
    val assertResult = fabutObjectAssert().assertProperty(aProperty.path, aProperty.value, eProperty, Map(), new NodesList, BOOLEAN_PROPERTY)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertPropertyExpectedPropertyisComplexEqual(): Unit = {
    //	  setup
    val aProperty = Property(TEST, new TierOneType(TEST))
    val eProperty = Property(TEST, new TierOneType(TEST))

    //    method
    val assertResult = fabutObjectAssert().assertProperty(aProperty.path, aProperty.value, eProperty, Map(), new NodesList, BOOLEAN_PROPERTY)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPairCaseListType(): Unit = {
    //	setup
    val assertPair = AssertPair(TEST, List(TEST), List(TEST), SCALA_LIST_TYPE, !BOOLEAN_PROPERTY)

    //    method	
    val assertResult = fabutObjectAssert().assertPair(EMPTY_STRING, assertPair, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testeAssertPairCaseMapType(): Unit = {
    //	  setup
    val assertPair = AssertPair(TEST, Map(TEST -> TEST), Map(TEST -> TEST), SCALA_MAP_TYPE, !BOOLEAN_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert().assertPair(EMPTY_STRING, assertPair, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPairCaseIgnoredType(): Unit = {
    //	  setup
    val assertPair = AssertPair(TEST, IgnoredType(TEST), IgnoredType(TEST), IGNORED_TYPE, !BOOLEAN_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert().assertPair(EMPTY_STRING, assertPair, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPairCaseComplexType(): Unit = {
    //	 setup
    val assertPair = new AssertPair(TEST, new TierOneType(TEST), new TierOneType(TEST), COMPLEX_TYPE, !BOOLEAN_PROPERTY)

    //   method
    val assertResult = fabutObjectAssert().assertPair(EMPTY_STRING, assertPair, Map(TIER_ONE_TYPE_PROPERTY -> Property(TIER_ONE_TYPE_PROPERTY, TEST)), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPairCasePrimitiveType(): Unit = {
    //	 setup
    val assertPair = new AssertPair(TEST, 123, 123, PRIMITIVE_TYPE, !BOOLEAN_PROPERTY)

    //   method
    val assertResult = fabutObjectAssert().assertPair(EMPTY_STRING, assertPair, Map(TIER_ONE_TYPE_PROPERTY -> Property(TIER_ONE_TYPE_PROPERTY, TEST)), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testHasInnerPropertiesCaseTrue(): Unit = {
    //	method
    val assertResult = fabutObjectAssert().hasInnerProperties(TIER_ONE_TYPE_PROPERTY, Map(TIER_ONE_TYPE_PROPERTY -> Property(TIER_ONE_TYPE_PROPERTY, TEST)))

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testHasInnerPropertiesCaseFalse(): Unit = {
    //    method
    val assertResult = fabutObjectAssert().hasInnerProperties(TEST, Map(TIER_ONE_TYPE_PROPERTY -> Property(TIER_ONE_TYPE_PROPERTY, TEST)))

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertInnerProperty(): Unit = {
    //	 setup
    val tierOneType = new TierOneType(TEST)

    //    method
    val assertResult = fabutObjectAssert().assertInnerProperty(TIER_ONE_TYPE_PROPERTY, tierOneType, Map(TIER_TWO_TYPE_PROPERTY -> Property(TIER_TWO_TYPE_PROPERTY, TEST)), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testExtractPropertiesWithMatchingParentExist(): Unit = {
    //	  method
    val properties = Map("a.b.c" -> NullProperty("a"), "a.b.d" -> NullProperty("b"), "c.d.a" -> NullProperty("c"))

    val expected = Map("b.c" -> NullProperty("a"), "b.d" -> NullProperty("b")).asInstanceOf[Map[String, IProperty]]

    //    setup
    val extracts = fabutObjectAssert().extractInnerPropertiesByParentName("a", properties)

    //    assert
    assertEquals(extracts, expected)
  }

  @Test
  def testExtractPropertiesWithMatchingParentDontExist(): Unit = {
    //	  method
    val properties = Map("a.b.c" -> NullProperty("a"), "a.b.d" -> NullProperty("b"), "c.d.a" -> NullProperty("c"))

    //    setup
    val extracts = fabutObjectAssert().extractInnerPropertiesByParentName(TEST, properties)

    //    assert
    assertEquals(extracts, Map())
  }

  @Test
  def testAssertSubfieldsExistInChangedProperties(): Unit = {
    //    setup
    val actual = new TierOneType(TEST)
    val expected = new TierOneType(TEST + TEST)
    val assertPair = AssertPair("", expected, actual, COMPLEX_TYPE, BOOLEAN_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert().assertSubfields(EMPTY_STRING, assertPair, Map(TIER_ONE_TYPE_PROPERTY -> Property(TIER_ONE_TYPE_PROPERTY, TEST)), new NodesList)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertSubfieldsDontExistInChangedProperties(): Unit = {
    //    setup
    val actual = new TierOneType(TEST)
    val expected = new TierOneType(TEST + TEST)
    val assertPair = AssertPair("", actual, expected, COMPLEX_TYPE, BOOLEAN_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert().assertSubfields(EMPTY_STRING, assertPair, Map(), new NodesList)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertPrimitivesEqual(): Unit = {
    //	setup
    val pair = AssertPair(TEST, 200, 200, PRIMITIVE_TYPE, !BOOLEAN_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert().assertPrimitives(pair)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertPrimitivesNotEqual(): Unit = {
    //	setup
    val pair = AssertPair(TEST, 200, 250, PRIMITIVE_TYPE, !BOOLEAN_PROPERTY)

    //    method
    val assertResult = fabutObjectAssert().assertPrimitives(pair)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testTakeSnapshotWithNormalClass(): Unit = {
    //	setup
    val tierOneType = new TierOneType(TEST)
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert().takeSnapshot(tierOneType)(report)
    val parameterSnapshot = fabutObjectAssert().parameterSnapshot()
    assertEquals(parameterSnapshot.size, 1)

    fabutObjectAssert().assertObjects(parameterSnapshot.head.actual, new TierOneType(TEST), Map())(report)
    fabutObjectAssert().assertObjects(parameterSnapshot.head.expected, new TierOneType(TEST), Map())(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testTakeSnapshotWithBadCopyClass(): Unit = {
    //	setup
    val tierOneType = new BadCopyClass(TEST, TEST)
    val report = new FabutReportBuilder

    //    method
    val assertResult = fabutObjectAssert().takeSnapshot(tierOneType)(report)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testNodesListContainsPairTrue(): Unit = {
    //	setup
    val nodesList = new NodesList

    val actual = new TierOneType(TEST)
    val expected = new TierOneType(TEST)
    nodesList.addPair(expected, actual)

    //    method
    val assertResult = nodesList.containsPair(expected, actual)

    //    assert
    assertTrue(assertResult)
  }

}
