package eu.execom.fabut

import eu.execom.fabut.enums.AssertableType._
import eu.execom.fabut.enums.NodeCheckType._
import eu.execom.fabut.exception.CopyException
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.pair.{AssertPair, SnapshotPair}
import eu.execom.fabut.property.{AbstractProperty, IgnoredProperty, NotNullProperty, NullProperty, Property}
import eu.execom.fabut.report.FabutReportBuilder
import eu.execom.fabut.util.ReflectionUtil._
import org.junit.Assert

import scala.collection.immutable.Map
import scala.collection.mutable.{ListBuffer, Map => MutableMap}
import scala.reflect.runtime.universe.Type

/**
 * Tool for smart asserting two objecting, or asserting object with list of
 * custom properties. Object asserting is done by asserting all the fields
 * inside the given object, if the field is primitive the tool will do user
 * specified assert for primitives, if not, tool will perform smart assert on
 * that field.
 *
 */
class FabutObjectAssert(fabutTest: IFabutTest) extends Assert {

  val DOT: String = "."
  val EMPTY_STRING = ""
  val ASSERTED = true
  val ASSERT_FAIL = false
  val ISOMORPHIC_GRAPH = true
  val NOT_ISOMORPHIC_GRAPH = false
  val PROPERTY = true
  val SUBPROPERTY = true

  fabutAssert_=(this)

  /** The parameter snapshot. */
  private val _parameterSnapshot: ListBuffer[SnapshotPair] = ListBuffer()

  def parameterSnapshot: List[SnapshotPair] = _parameterSnapshot.toList

  def initParametersSnapshot {
    _parameterSnapshot.clear
  }

  private val _types: scala.collection.mutable.Map[AssertableType, List[Type]] = MutableMap()

  _types(COMPLEX_TYPE) = fabutTest.complexTypes
  _types(IGNORED_TYPE) = fabutTest.ignoredTypes
  _types(ENTITY_TYPE) = List()

  /**
   * Asserts object with with expected properties, every field of object must have property for it or assert will fail.
   *
   *
   * @param actualObject
   * the actual object
   * @param expectedProperties
   * expected properties for actual object
   * @param report
   *
   * @return
   * <code> true </code> if objects can be asserted, <code> false </code> otherwise.
   *
   */
  def assertObjectWithProperties(actual: Any, expectedProperties: Map[String, AbstractProperty])(implicit report: FabutReportBuilder): Boolean = {

    var result = ASSERTED

    val assertableType = getAssertableType(actual)

    if (assertableType == IGNORED_TYPE) {
      return ASSERTED
    }

    val actualProperties = getObjectProperties(actual, getObjectType(actual, assertableType)).values

    actualProperties.foreach {
      case actualProperty =>

        val expectedProperty = if (expectedProperties.contains(actualProperty.path)) {
          Some(expectedProperties(actualProperty.path))
        } else {
          None
        }

        expectedProperty match {
          case expectedProperty if (expectedProperty.isDefined) =>
            result &= assertProperty(actualProperty.path, actualProperty.value, expectedProperty.get, expectedProperties, new NodesList, PROPERTY)
          case expectedProperty if (hasInnerProperties(actualProperty.path, expectedProperties)) =>
            result &= assertInnerProperty(actualProperty.path, actualProperty.value, expectedProperties, new NodesList)
          case _ =>
            report.noPropertyForField(actualProperty.path, actualProperty)
            result &= ASSERT_FAIL
        }
    }
    if (result) {
      this.afterAssertObject(actual, !SUBPROPERTY)
    }

    result
  }

  /**
   * Asserts two objects, if objects are primitives it will rely on custom
   * user assert for primitives, if objects are complex it will assert them by
   * values of their fields.
   *
   * @param expectedObject
   * @param actualObject
   * @param expectedChangedProperties
   * properties from this collection take priority over the fields from expected object
   * @param report
   *
   * @return <code> true </code> if objects can be asserted, <code> false </code> otherwise.
   *
   */
  def assertObjects(expected: Any, actual: Any, expectedChangedProperties: Map[String, AbstractProperty])(implicit report: FabutReportBuilder): Boolean = {

    val pair = AssertPair(EMPTY_STRING, expected, actual, getAssertableType(actual), !PROPERTY)

    val expectedProperties = if (expectedChangedProperties.isEmpty) {
      getObjectProperties(expected, getObjectType(expected, getAssertableType(expected)))
    } else {
      expectedChangedProperties
    }

    val assertResult = assertPair(EMPTY_STRING, pair, expectedProperties, new NodesList)

    if (assertResult) {
      afterAssertObject(actual, !SUBPROPERTY)
    }
    assertResult
  }

  /**
   * Makes snapshot of specified parameters.
   *
   * @param parameters
   * array of parameters
   */
  def takeSnapshot(parameters: Any*)(implicit report: FabutReportBuilder): Boolean = {

    var result = ASSERTED

    initParametersSnapshot

    parameters.foreach { entity =>
      try {
        val snapshotPair = SnapshotPair(!ASSERTED, entity, createCopy(entity))
        _parameterSnapshot += snapshotPair
      } catch {
        case e: CopyException =>
          report.noCopy(entity)
          result &= ASSERT_FAIL;
      }
    }
    result
  }

  /**
   * Asserts object pair
   *
   * @param propertyName
   * name of current property
   * @param pair
   * object pair for asserting
   * @param properties
   * properties collection of expected changed properties
   * @param nodesList
   * list of objects that have been asserted\
   *
   * @return <code>true</code> if objects can be asserted, <code>false</code>
   *         otherwise.
   */
  def assertPair(propertyName: String, pair: AssertPair, properties: Map[String, AbstractProperty], nodesList: NodesList)(implicit report: FabutReportBuilder): Boolean = {

    pair.assertableType match {
      case SCALA_LIST_TYPE =>
        assertList(propertyName, 0, pair.actual.asInstanceOf[List[Any]], pair.expected.asInstanceOf[List[Any]], properties, nodesList, PROPERTY)
      case SCALA_MAP_TYPE =>
        assertMap(propertyName, pair.actual.asInstanceOf[Map[Any, Any]], pair.expected.asInstanceOf[Map[Any, Any]], properties, nodesList, PROPERTY)
      case ENTITY_TYPE =>
        assertEntityPair(pair.path, pair, properties, nodesList)
      case IGNORED_TYPE =>
        report.ignoredType(pair)
        ASSERTED
      case COMPLEX_TYPE =>
        if (pair.expected.asInstanceOf[AnyRef] eq pair.actual.asInstanceOf[AnyRef]) {
          return ASSERTED
        }
        assertSubfields(propertyName, pair, properties, nodesList)
      case PRIMITIVE_TYPE =>
        assertPrimitives(pair, propertyName)
      case _ =>
        throw new IllegalStateException("Uknown assert type: " + pair.assertableType)
    }
  }

  /**
   * Handles asserting of actual object by checking expected object type of property
   * and by the type it creates a pair for assert or ignores it.
   *
   * @param propertyName
   * name of the current property
   * @param actualObject
   * actual object
   * @param property
   * the expected property containing expected information
   * @param changedProperties
   * collection of properties that exclude fields from expected object
   * @param nodesList
   * list of object that have already been asserted
   * @param isProperty
   * is actual property, important for entities
   * @param report
   *
   * @return <code> true </code> if actual property is successfully asserted with expected <code> false </code> otherwise.
   */
  def assertProperty(propertyName: String, actualObject: Any, property: AbstractProperty, changedProperties: Map[String, AbstractProperty], nodesList: NodesList, isProperty: Boolean)(implicit report: FabutReportBuilder): Boolean = {

    val result = property match {
      case field: IgnoredProperty =>
        report.reportIgnoreProperty(field.path)
        ASSERTED
      case field: NullProperty =>
        val ok = if (actualObject == null) ASSERTED else ASSERT_FAIL
        report.notNullProperty(field.path, ok)
        ok
      case field: NotNullProperty =>
        val ok = if (actualObject != null) ASSERTED else ASSERT_FAIL
        report.nullProperty(field.path, ok)
        ok
      case expectedProperty: Property => {

        val pair = AssertPair(propertyName, expectedProperty.value, actualObject, getAssertableType(actualObject), isProperty)
        val properties = getObjectProperties(pair.expected, getObjectType(pair.expected, pair.assertableType))

        if (pair.assertableType == COMPLEX_TYPE) {

          val nodeCheckType = nodesList.nodeCheck(pair)

          nodeCheckType match {
            case NEW_PAIR =>
              nodesList.addPair(pair.expected.asInstanceOf[AnyRef], pair.actual.asInstanceOf[AnyRef])
              assertPair(propertyName, pair, properties, nodesList)
            case CONTAINS_PAIR =>
              if (nodesList.containsPair(pair.expected.asInstanceOf[AnyRef], pair.actual.asInstanceOf[AnyRef])) {
                ISOMORPHIC_GRAPH
              } else {
                report.checkByReference(pair.path, pair.actual, !ASSERTED)
                NOT_ISOMORPHIC_GRAPH
              }
          }
        } else {
          assertPair(propertyName, pair, properties, nodesList)
        }
      }
      case _ => ASSERTED
    }

    result
  }

  /**
   * Determines if collection of properties has inner properties in it.
   *
   * @param parent
   * @param properties
   *
   * @return
   */
  def hasInnerProperties(parent: String, properties: Map[String, AbstractProperty]): Boolean = {

    properties.keys.find(property => property.startsWith(parent)).getOrElse(return ASSERT_FAIL)

    ASSERTED

  }

  /**
   * Asserts inner properties of parent object
   *
   * @param parentName
   * @param parentObject
   * @param properties
   * @param nodesList
   * @param report
   *
   * @return <code> true </code> if inner object properties are successfully asserted with expected properties<code> false </code> otherwise.
   */
  def assertInnerProperty(parentName: String, parentObject: Any, properties: Map[String, AbstractProperty], nodesList: NodesList)(implicit report: FabutReportBuilder): Boolean = {
    val extracts = extractPropertiesWithMatchingParent(parentName, properties)
    report.increaseDepth(parentName)
    val result = assertObjectWithProperties(parentObject, extracts)
    report.decreaseDepth
    result
  }

  /**
   * Asserts two entities.
   *
   * @param propertyName
   * @param pair
   * @param properties
   * @param nodesList
   * @param report
   *
   * @return <code> true </code> if objects can be asserted, <code> false </code>
   *         otherwise.
   */
  def assertEntityPair(propertyName: String, pair: AssertPair, properties: Map[String, AbstractProperty], nodesList: NodesList)(implicit report: FabutReportBuilder): Boolean = {
    throw new IllegalStateException("Entities are not supported!")
  }

  /**
   * Assert subfields of an actual object with ones from expected object, it
   * gets the fields by invoking getters of actual/expected objects via
   * reflection, properties passed have priority over expected object fields.
   *
   * @param parentName
   * name of subfields parent object
   * @param pair
   * the subfield pair
   * @param properties
   * @param nodesList
   * @param report
   *
   * @return <code>true</code> if objects can be asserted, <code>false</code>
   *         otherwise.
   */
  def assertSubfields(parentName: String, pair: AssertPair, properties: Map[String, AbstractProperty], nodesList: NodesList)(implicit report: FabutReportBuilder) = {
    var ret = ASSERTED

    val objectType = getObjectType(pair.expected, getAssertableType(pair.expected))
    val propertiesToBeAsserted = getObjectProperties(pair.expected, objectType)

    report.increaseDepth(parentName)

    propertiesToBeAsserted foreach {
      case (propertyName, property) =>
        val expectedProperty = obtainProperty(property, propertyName, properties)

        val actual = getFieldValueFromGetter(propertyName, pair.actual, objectType).getOrElse {
          throw new NoSuchElementException("Actual doesnt exist")
        }

        ret &= assertProperty(propertyName, actual, expectedProperty, properties, nodesList, PROPERTY)
    }

    report.decreaseDepth

    ret
  }

  /**
   * Asserts two primitives using abstract method assertEqualsObjects, reports
   * result and returns it. Primitives are any class not marked as complex
   * type, entity type or ignored type.
   *
   *
   * @param propertyName
   * name of the current property
   * @param pair
   * @param report
   * @return - <code> true </code> if and only if objects are asserted, <code>false</code>
   *         if method customAssertEquals throws  { @link AssertionError}.
   */
  def assertPrimitives(pair: AssertPair, propertyName: String)(implicit report: FabutReportBuilder) = {

    try {
      fabutTest.customAssertEquals(pair.expected, pair.actual)
      report.asserted(pair, propertyName)
      ASSERTED
    } catch {
      case e: AssertionError =>
        report.assertFail(pair, propertyName)
        ASSERT_FAIL
    }
  }

  /**
   * Handles list asserting. It traverses through the list by list index start
   * from 0 and going up to list size and asserts every two elements on
   * matching index. Lists cannot be asserted if their sizes are different.
   * @param propertyName
   * name of current property
   * @param report
   * assert report builder
   * @param expected
   * expected list
   * @param actual
   * actual list
   * @param properties
   * list of excluded properties
   * @param nodesList
   * list of object that had been asserted
   * @param isProperty
   * is it parent object or its member
   * @return - <code>true</code> if every element from expected list with
   *         index <em>i</em> is asserted with element from actual list with
   *         index <em>i</em>, <code>false</code> otherwise.
   */
  def assertList(propertyName: String, position: Int, actualList: List[_], expectedList: List[_], properties: Map[String, AbstractProperty],
                 nodesList: NodesList, isProperty: Boolean)(implicit report: FabutReportBuilder): Boolean = {

    var result = ASSERTED

    if (actualList.size != expectedList.size) {
      report.listDifferentSizeComment(propertyName, expectedList.size, actualList.size)
      return ASSERT_FAIL
    }

    report.increaseDepth(propertyName)
    report.assertingListElements(propertyName, position)

    actualList match {
      case head :: tail => {
        val actual = actualList.head
        val expected = expectedList.head

        if (getAssertableType(head) == PRIMITIVE_TYPE) {
          try {
            fabutTest.customAssertEquals(expected, actual)
            result &= ASSERTED
          } catch {
            case e: AssertionError =>
              val pair = new AssertPair(EMPTY_STRING, expected, actual, PRIMITIVE_TYPE, true)
              report.assertFail(pair, propertyName)
              result &= ASSERT_FAIL
          }
        } else {
          assertObjects(expected, actual, properties)
        }
        result &= assertList(propertyName, position + 1, actualList.tail, expectedList.tail, properties, nodesList, isProperty)
      }
      case Nil => return result
    }

    report.decreaseDepth

    result
  }

  /**
   * Asserts two maps.
   *
   * @param propertyName
   * @param actualMap
   * @param expectedMap
   * @param properties
   * @param nodesList
   * @param isProperty
   * @param report
   *
   * @return
   */
  def assertMap(propertyName: String, actualMap: Map[Any, Any], expectedMap: Map[Any, Any], properties: Map[String, AbstractProperty],
                nodesList: NodesList, isProperty: Boolean)(implicit report: FabutReportBuilder): Boolean = {

    var result = ASSERTED

    if (actualMap.size != expectedMap.size) {
      report.mapDifferentSizeComment(propertyName, expectedMap.size, actualMap.size)
      return ASSERT_FAIL
    }

    report.increaseDepth(propertyName)

    actualMap.keys.foreach {
      (name: Any) =>
        try {
          report.assertingMapKey(name)

          val actual = actualMap(name)
          val expected = expectedMap(name)

          val actualType = getObjectType(actual, getAssertableType(actual))
          val expectedType = getObjectType(expected, getAssertableType(expected))

          result &= assertObjects(expected, actual, properties)

        } catch {

          case e: NoSuchElementException =>
            report.mapMissingKeyInExpected(name)
            val differentExpectedKey = expectedMap.keys.toList diff (actualMap.keys.toList)
            report.mapMissingKeyInActual(differentExpectedKey.head)
            result &= ASSERT_FAIL
        }
    }

    report.decreaseDepth
    result

  }

  /**
   * Obtains property by following rules: if there is {@link IProperty}
   * in the collection of properties matching path with fieldName, it returns it. Otherwise, it returns the expected property
   *
   * @param property
   * expected property
   * property path
   * @param properties
   * collection of properties
   * @return { @link IProperty} if there is property with same path as
   *                 specified in list of properties, otherwise from expected property
   */
  def obtainProperty(property: AbstractProperty, propertyPath: String, properties: Map[String, AbstractProperty]) = {

    if (properties.contains(propertyPath)) {
      properties(propertyPath)
    } else {
      property
    }
  }

  /**
   * Extracts properties from specified collection that have same parent as
   * specified one.
   *
   * @param parent
   * @param properties
   *
   * @return
   */
  def extractPropertiesWithMatchingParent(parent: String, properties: Map[String, AbstractProperty]): Map[String, AbstractProperty] = {
    properties collect {
      case (name: String, value: AbstractProperty) if name.startsWith(parent + DOT) =>
        (name.replaceFirst(parent + DOT, EMPTY_STRING), value)
    }
  }

  /**
   * Asserts current parameters states with snapshot previously taken.
   *
   * @param report
   *
   * @return true, if successful
   */
  def assertParameterSnapshot(implicit report: FabutReportBuilder): Boolean = {
    var ok = true
    parameterSnapshot.foreach {
      case snapshotPair: SnapshotPair => {
        ok &= assertObjects(snapshotPair.expected, snapshotPair.actual, Map())
      }
    }
    initParametersSnapshot
    ok
  }

  /**
   * Checks if object of entity type and if it is mark it as asserted entity,
   * in other case do nothing.
   *
   * @param object
   * the object
   * @param isSubproperty
   * is object subproperty
   *
   * @return true, if successful
   */
  def afterAssertObject(theObject: Any, isSubproperty: Boolean): Boolean = {
    ASSERT_FAIL
  }

  /**
   * Get the types
   *
   * @return the types
   */

  def types: MutableMap[AssertableType, List[Type]] = _types

  /**
   * Gets the entity types.
   *
   * @return the entity types
   */
  def getEntityTypes =
    _types(ENTITY_TYPE)

}

