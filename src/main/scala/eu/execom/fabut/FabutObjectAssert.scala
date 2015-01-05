package eu.execom.fabut

import eu.execom.fabut.AssertableType._
import eu.execom.fabut.NodeCheckType._
import eu.execom.fabut.FieldType._
import eu.execom.fabut.exception.CopyException
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.pair.{AssertPair, SnapshotPair}
import eu.execom.fabut.property.{IProperty, IgnoredProperty, NotNullProperty, NullProperty, Property}
import eu.execom.fabut.report.FabutReportBuilder
import eu.execom.fabut.util.ReflectionUtil._
import org.junit.Assert
import scala.collection.mutable.{ListBuffer, Map => MutableMap}
import scala.reflect.runtime.universe.Type

/**
 * Tool for smart asserting two objecting, or asserting object with list of
 * custom properties. Object asserting is done by asserting all the fields
 * inside the given object, if the field is primitive the tool will do user
 * specified assert for primitives, if not, tool will perform smart assert on
 * that field.
 */
class FabutObjectAssert(val fabut: Fabut) extends Assert {

  initUtils(this)

  /** The parameter snapshot. */
  private val _parameterSnapshot: ListBuffer[SnapshotPair] = ListBuffer()

  private val _types: MutableMap[AssertableType, List[Type]] = MutableMap()
  _types(COMPLEX_TYPE) = fabut.complexTypes()
  _types(IGNORED_TYPE) = fabut.ignoredTypes()
  _types(ENTITY_TYPE) = List()

  def parameterSnapshot(): List[SnapshotPair] = _parameterSnapshot.toList

  /**
   * Asserts object with with expected properties, every field of object must have property for it or assert will fail.
   *
   *
   * @param actual
   * the actual object
   * @param expectedProperties
   * expected properties for actual object
   * @param report
   * - assert report builder
   * @return
   * <code> true </code> if objects can be asserted, <code> false </code> otherwise.
   */
  def assertObjectWithProperties(actual: Any, expectedProperties: Map[String, IProperty])(implicit report: FabutReportBuilder): Boolean = getAssertableType(actual) match {
    case IGNORED_TYPE => ASSERTED
    case assertableType =>
      val actualProperties = getObjectProperties(actual, getClassType(actual, assertableType),FOR_ASSERT).values
      val result = actualProperties.forall(actualProperty =>
        if (expectedProperties.contains(actualProperty.path)) {
          assertProperty(actualProperty.path, actualProperty.value, expectedProperties(actualProperty.path), expectedProperties, new NodesList, PROPERTY)
        } else if (hasInnerProperties(actualProperty.path, expectedProperties)) {
          assertInnerProperty(actualProperty.path, actualProperty.value, expectedProperties, new NodesList)
        } else {
          report.noPropertyForField(actualProperty.path, actual)
          ASSERT_FAIL
        }
      )
      if (result) afterAssertObject(actual, !SUBPROPERTY)

      result
  }

  /**
   * Asserts two objects, if objects are primitives it will rely on custom
   * user assert for primitives, if objects are complex it will assert them by
   * values of their fields.
   *
   * @param expected
   * the expected object
   * @param actual
   * the actual object
   * @param changedProperties
   * properties from this collection take priority over the fields from expected object
   * @param report
   * - assert report builder
   *
   * @return <code> true </code> if objects can be asserted, <code> false </code> otherwise.
   */
  def assertObjects(expected: Any, actual: Any, changedProperties: Map[String, IProperty])(implicit report: FabutReportBuilder): Boolean = {
    val pair = AssertPair(EMPTY_STRING, expected, actual, getAssertableType(actual), !PROPERTY)

    val expectedProperties = if (changedProperties.isEmpty) {
      getObjectProperties(expected, getClassType(expected, getAssertableType(expected)),FOR_ASSERT)
    } else {
      changedProperties
    }
    val assertResult = assertPair(EMPTY_STRING, pair, expectedProperties, new NodesList)

    if (assertResult) this.afterAssertObject(actual, !SUBPROPERTY)

    assertResult
  }

  /**
   * Makes snapshot of specified parameters.
   *
   * @param parameters
   * array of parameters
   * @param report
   * - assert report builder
   */
  def takeSnapshot(parameters: Any*)(implicit report: FabutReportBuilder): Boolean = {

    initParametersSnapshot()

    parameters.forall(entity => try {
      _parameterSnapshot += SnapshotPair(!ASSERTED, entity, createCopy(entity))
      ASSERTED
    } catch {
      case e: CopyException =>
        report.noCopy(entity)
        ASSERT_FAIL
    })
  }

  def initParametersSnapshot(): Unit = _parameterSnapshot.clear()

  /**
   * Asserts object pair
   *
   * @param propertyName
   * name of current property
   * @param pair
   * object pair for asserting
   * @param changedProperties
   * properties collection of changed properties
   * @param nodesList
   * list of objects that have been asserted
   * @param report
   * - assert report builder
   *
   * @return <code>true</code> if objects can be asserted, <code>false</code>
   *         otherwise.
   */
  def assertPair(propertyName: String, pair: AssertPair, changedProperties: Map[String, IProperty], nodesList: NodesList)(implicit report: FabutReportBuilder): Boolean = pair.assertableType match {
    case SCALA_LIST_TYPE => assertList(propertyName, 0, pair.actual.asInstanceOf[List[Any]], pair.expected.asInstanceOf[List[Any]], changedProperties, nodesList)
    case SCALA_MAP_TYPE => assertMap(propertyName, pair.actual.asInstanceOf[Map[Any, Any]], pair.expected.asInstanceOf[Map[Any, Any]], changedProperties, nodesList)
    case ENTITY_TYPE => assertEntityPair(pair.path, pair, changedProperties, nodesList)
    case IGNORED_TYPE => report.ignoredType(pair); ASSERTED
    case COMPLEX_TYPE => (pair.expected, pair.actual) match {
        case (expected:AnyRef, actual: AnyRef) if expected.eq(actual) => ASSERTED
        case _ =>  assertSubfields(propertyName, pair, changedProperties, nodesList) }
    case PRIMITIVE_TYPE => assertPrimitives(pair)
  }

  /**
   * Handles asserting of actual object by checking expected object type of property
   * and by the type it creates a pair for assert or ignores it.
   *
   * @param propertyName
   * - name of the current property
   * @param actualObject
   * - the actual object
   * @param property
   * - the expected property containing expected information
   * @param changedProperties
   * - collection of properties that exclude fields from expected object
   * @param nodesList
   * - list of object that have already been asserted
   * @param isProperty
   * - is actual property, important for entities
   * @param report
   * - assert report builder
   *
   * @return <code> true </code> if actual property is successfully asserted with expected <code> false </code> otherwise.
   */
  def assertProperty(propertyName: String, actualObject: Any, property: IProperty, changedProperties: Map[String, IProperty], nodesList: NodesList, isProperty: Boolean)(implicit report: FabutReportBuilder): Boolean =
    property match {
      case property: IgnoredProperty => report.reportIgnoreProperty(property.path)
        ASSERTED
      case property: NullProperty =>
        val ok = actualObject == null
        report.notNullProperty(property.path, ok)
        ok
      case property: NotNullProperty =>
        val ok = actualObject != null
        report.nullProperty(property.path, ok)
        ok
      case expectedProperty: Property =>
        val pair = AssertPair(propertyName, expectedProperty.value, actualObject, getAssertableType(actualObject), isProperty)
        val expectedProperties = getObjectProperties(pair.expected, getClassType(pair.expected, pair.assertableType),FOR_ASSERT)
        pair.assertableType match {
          case COMPLEX_TYPE =>
            nodesList.nodeCheck(pair) match {
              case NEW_PAIR =>
                nodesList.addPair(pair.expected, pair.actual)
                assertPair(propertyName, pair, expectedProperties, nodesList)
              case CONTAINS_PAIR =>
                if (nodesList.containsPair(pair.expected, pair.actual)) {
                  report.checkByReference(pair.path, pair.actual, ISOMORPHIC_GRAPH)
                  ISOMORPHIC_GRAPH
                } else {
                  report.checkByReference(pair.path, pair.actual, NOT_ISOMORPHIC_GRAPH)
                  NOT_ISOMORPHIC_GRAPH
                }
            }
          case _ => assertPair(propertyName, pair, expectedProperties, nodesList)
        }
    }


  /**
   * Determines if collection of properties has inner properties in it.
   *
   * @param parentName
   * - the name of parent property
   * @param properties
   * - collection of properties that exclude fields from expected object
   *
   * @return <code> true </code> if there are inner properties for selected name in collection list <code> false </code> otherwise.
   */
  def hasInnerProperties(parentName: String, properties: Map[String, IProperty]): Boolean = properties.keys.exists(property => property.startsWith(parentName))

  /**
   * Asserts inner properties of parent object.
   *
   * @param parentName
   * - the name of parent property
   * @param parentObject
   * - parent property
   * @param properties
   * - changed properties
   * @param nodesList
   * - list of object that have already been asserted
   * @param report
   * - assert report builder
   *
   * @return <code> true </code> if inner object properties are successfully asserted with expected properties<code> false </code> otherwise.
   */
  def assertInnerProperty(parentName: String, parentObject: Any, properties: Map[String, IProperty], nodesList: NodesList)(implicit report: FabutReportBuilder): Boolean = {
    val extracts = extractInnerPropertiesByParentName(parentName, properties)
    report.increaseDepth(parentName)
    val result = assertObjectWithProperties(parentObject, extracts)
    report.decreaseDepth()
    result
  }

  /**
   * Asserts two entities.
   *
   * @param propertyName
   * - name of the entity if it is inner property
   * @param pair
   * - object pair for asserting
   * @param properties
   * - changed properties
   * @param nodesList
   * - list of object that have already been asserted
   * @param report
   * - assert report builder
   *
   * @return <code> true </code> if objects can be asserted, <code> false </code>
   *         otherwise.
   */
  def assertEntityPair(propertyName: String, pair: AssertPair, properties: Map[String, IProperty], nodesList: NodesList)(implicit report: FabutReportBuilder): Boolean = throw new IllegalStateException("Entities are not supported!")

  /**
   * Assert subfields of an actual object with ones from expected object, it
   * gets the fields by invoking getters of actual/expected objects via
   * reflection, properties passed have priority over expected object fields.
   *
   * @param parentName
   * - name of subfields parent object
   * @param pair
   * - the subfield pair
   * @param properties
   * - changed properties
   * @param nodesList
   * - list of object that have already been asserted
   * @param report
   * - assert report builder
   *
   * @return <code>true</code> if objects can be asserted, <code>false</code>
   *         otherwise.
   */
  def assertSubfields(parentName: String, pair: AssertPair, properties: Map[String, IProperty], nodesList: NodesList)(implicit report: FabutReportBuilder) = {
    val objectType = getClassType(pair.expected, getAssertableType(pair.expected))
    val subProperties = getObjectProperties(pair.expected, objectType,FOR_ASSERT)

    report.increaseDepth(parentName)
    val result = subProperties.forall {
      case (propertyName, property) =>
        val expectedProperty = obtainProperty(property, propertyName, properties)
        val actualObject = getFieldValueFromGetter(propertyName, pair.actual, objectType.get).get
        assertProperty(propertyName, actualObject, expectedProperty, properties, nodesList, PROPERTY)
    }
    report.decreaseDepth()
    result
  }

  /**
   * Asserts two primitives using abstract method assertEqualsObjects, reports
   * result and returns it. Primitives are any class not marked as complex
   * type, entity type or ignored type.
   *
   * @param pair
   * - the object pair
   * @param report
   * - assert report builder
   * @return - <code> true </code> if and only if objects are asserted, <code> false </code>
   *         if method customAssertEquals throws  { @link AssertionError}.
   */
  def assertPrimitives(pair: AssertPair)(implicit report: FabutReportBuilder) = try {
    fabut.customAssertEquals(pair.expected, pair.actual); ASSERTED
    //report.asserted(pair, pair.path)
  } catch {
    case e: AssertionError => report.assertFail(pair, pair.path); ASSERT_FAIL
  }

  /**
   * Handles list asserting. It traverses through the list by list index start
   * from 0 and going up to list size and asserts every two elements on
   * matching index. Lists cannot be asserted if their sizes are different.
   *
   * @param propertyName
   * - name of current property
   * @param position
   * - current position of the list element we are asserting
   * @param expectedList
   * - expected list
   * @param actualList
   * - actual list
   * @param properties
   * - changed properties
   * @param nodesList
   * - list of object that had been asserted
   * @param report
   * - assert report builder
   *
   * @return - <code> true </code> if every element from expected list with
   *         index <em>i</em> is asserted with element from actual list with
   *         index <em>i</em>, <code> false </code> otherwise.
   */
  def assertList(propertyName: String, position: Int, actualList: List[Any], expectedList: List[Any], properties: Map[String, IProperty], nodesList: NodesList)(implicit report: FabutReportBuilder): Boolean = (actualList, expectedList) match{
    case _ if actualList.size != expectedList.size =>
      report.listDifferentSizeComment(propertyName, expectedList.size, actualList.size)
      ASSERT_FAIL
    case _ =>
      report.increaseDepth(propertyName)
      report.assertingListElements(propertyName, position)
      val listPairs = for ((actual, expected) <- actualList zip expectedList) yield (actual, expected)
      val result = listPairs.forall {
        case (actual, expected) => getAssertableType(actual) match {
          case PRIMITIVE_TYPE => assertPrimitives(new AssertPair(propertyName, expected, actual, PRIMITIVE_TYPE))
          case _ => assertObjects(expected, actual, properties) }
      }
      report.decreaseDepth()
      result
  }

  /**
   * Asserts two maps.
   *
   * @param propertyName
   * - name of current property
   * @param actualMap
   * - actual map
   * @param expectedMap
   * - expected map
   * @param properties
   * - changed properties
   * @param nodesList
   * - list of object that had been asserted
   * @param report
   * - assert report builder
   *
   * @return <code> true </code> if every element from actual map is same as corresponding one in expected map
   *         <code> false </code> otherwise.
   */
  def assertMap(propertyName: String, actualMap: Map[Any, Any], expectedMap: Map[Any, Any], properties: Map[String, IProperty],
                nodesList: NodesList)(implicit report: FabutReportBuilder): Boolean = (actualMap, expectedMap) match {
    case _ if actualMap.size != expectedMap.size =>
      report.mapDifferentSizeComment(propertyName, expectedMap.size, actualMap.size)
      ASSERT_FAIL
    case _ =>
      report.increaseDepth(propertyName)
      val result = actualMap.keys.forall {
        name => try {
          report.assertingMapKey(name)
          assertObjects(expectedMap(name), actualMap(name), properties)
        } catch {
          case e: NoSuchElementException =>
            report.mapMissingKeyInExpected(name)
            val differentKeys = expectedMap.keys.toList.diff(actualMap.keys.toList)
            differentKeys.foreach(key => report.mapMissingKeyInActual(key))
            ASSERT_FAIL
        }
      }
      report.decreaseDepth()
      result
    }



  /**
   * Obtains property by following rules: if there is property
   * in the collection of properties matching path with fieldName, it returns it. Otherwise, it returns the expected property
   *
   * @param property
   * - expected property
   * @param properties
   * - collection of properties
   *
   * @return property if there is property with same path as
   *         specified in list of properties, otherwise from expected property
   */
  def obtainProperty(property: IProperty, propertyPath: String, properties: Map[String, IProperty]): IProperty = properties.getOrElse(propertyPath, property)

  /**
   * Extracts properties from specified collection that have same parent as
   * specified one.
   *
   * @param parent
   * - name of parent object
   * @param properties
   * - changed properties
   *
   * @return the collection of inner properties for given parent name without parent prefix
   */
  def extractInnerPropertiesByParentName(parent: String, properties: Map[String, IProperty]): Map[String, IProperty] = properties.collect {
    case (name: String, value: IProperty) if name.startsWith(parent + DOT) => (name.replaceFirst(parent + DOT, EMPTY_STRING), value) }

  /**
   * Asserts current parameters states with snapshot previously taken.
   *
   * @param report
   * - assert report builder
   *
   * @return <code>true</code> if successful , <code>false </code> otherwise.
   */
  def assertParameterSnapshot(implicit report: FabutReportBuilder): Boolean = {
    val result = parameterSnapshot().forall { snapshotPair => assertObjects(snapshotPair.expected, snapshotPair.actual, Map()) }
    initParametersSnapshot()
    result
  }

  /**
   * Checks if object of entity type and if it is mark it as asserted entity,
   * in other case do nothing.
   *
   * @param theObject
   * - the object
   * @param isSubproperty
   * - is object a subproperty
   *
   * @return <code>true</code> if successful , <code>false </code> otherwise.
   */
  def afterAssertObject(theObject: Any, isSubproperty: Boolean): Boolean = ASSERT_FAIL

  /**
   * Get the types
   *
   * @return the Fabut types
   */

  def types: MutableMap[AssertableType, List[Type]] = _types

  /**
   * Gets the entity types.
   *
   * @return the entity types
   */
  def getEntityTypes: List[Type] = _types(ENTITY_TYPE)
}

