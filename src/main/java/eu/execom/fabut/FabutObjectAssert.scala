package eu.execom.fabut

import scala.collection.immutable.Map
import scala.collection
import scala.reflect.runtime.universe.{ Type, TypeTag, typeOf, Symbol, InstanceMirror }
import eu.execom.fabut.enums.AssertType._
import eu.execom.fabut.enums.AssertableType._
import eu.execom.fabut.enums.NodeCheckType._
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.model.ObjectWithComplexProperty
import eu.execom.fabut.model.ObjectWithSimpleProperties
import eu.execom.fabut.model.TrivialClasses._
import eu.execom.fabut.util.ReflectionUtil._
import scala.collection.mutable.{ Map => MutableMap }
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import org.junit.Assert
import eu.execom.fabut.model.ObjectWithComplexProperty
import eu.execom.fabut.model.NormalClass
import eu.execom.fabut.model.TierTwoType
import eu.execom.fabut.model.EntityTierOneType
import eu.execom.fabut.model.EntityTierThreeType
import eu.execom.fabut.property.Property
import eu.execom.fabut.property.IProperty
import eu.execom.fabut.property.IgnoredProperty
import eu.execom.fabut.property.Property
import eu.execom.fabut.property.NullProperty
import eu.execom.fabut.model.EmptyClass
import eu.execom.fabut.property.NotNullProperty
import eu.execom.fabut.property.IgnoredProperty
import eu.execom.fabut.property.NullProperty
import eu.execom.fabut.property.NotNullProperty
import eu.execom.fabut.pair.AssertPair
import eu.execom.fabut.model.IgnoredType
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.graph.IsomorphicNodePair
import eu.execom.fabut.pair.SnapshotPair
import eu.execom.fabut.exception.CopyException
import eu.execom.fabut.pair.SnapshotPair
import scala.collection.mutable.ListBuffer
import eu.execom.fabut.pair.AssertPair

class FabutObjectAssert(fabutTest: IFabutTest) extends Assert {

  val DOT: String = "."
  val EMPTY_STRING = ""
  val ASSERTED = true
  val ASSERT_FAIL = false
  val ISOMORPHIC_GRAPH = true
  val NOT_ISOMORPHIC_GRAPH = false

  private val _parameterSnapshot: ListBuffer[SnapshotPair] = ListBuffer()

  def parameterSnapshot: List[SnapshotPair] = _parameterSnapshot.toList

  def initParametersSnapshot {
    _parameterSnapshot.clear
  }

  private var _types: MutableMap[AssertableType, List[Type]] = MutableMap()

  _types(COMPLEX_TYPE) = fabutTest.complexTypes
  _types(IGNORED_TYPE) = fabutTest.ignoredTypes
  _types(ENTITY_TYPE) = List()

  /**
   * Asserts object with with expected properties, every field of object must have property for it or assert will fail.
   *
   * @param report
   * @param actualObject
   * 	the actual object
   * @param properties
   * 	expected properties of actual object
   * @param nodesList
   * 	list of complex objects that have been asserted
   * @return
   * 	<code> true </code> if objects can be asserted, <code> false </code> otherwise.
   *
   */
  def assertObjectWithProperties(report: FabutReportBuilder, actualObject: Any, properties: Map[String, IProperty]): Boolean = {

    val actualObjectType = getAssertableType(actualObject) match {
      case IGNORED_TYPE =>
        return ASSERTED
      case assertableType =>
        assertableType
    }

    val actualProperties: Map[String, Property] = getObjectProperties(actualObject, getObjectType(actualObject, actualObjectType))
    var result = ASSERTED

    actualProperties.values.foreach {
      case actualProperty: Property =>

        val expectedProperty = if (properties.contains(actualProperty.path)) {
          Some(properties(actualProperty.path))
        } else {
          None
        }

        expectedProperty match {
          case expectedProperty if (expectedProperty.isDefined) =>
            result &= assertProperty(actualProperty.path, actualProperty.value, expectedProperty.get, properties, new NodesList, true)(report)
          case expectedProperty if (hasInnerProperties(actualProperty.path, properties)) =>
            result &= assertInnerProperty(actualProperty.path, actualProperty.value, properties, new NodesList, report)
          case _ =>
            // there is no matching property for field -- check if good 
            report.noPropertyForField(actualProperty.path, actualProperty)
            result &= ASSERT_FAIL
        }
    }
    if (result) {
      this.afterAssertObject(actualObject, false)
    }

    result
  }

  /**
   *  Asserts two objects, if objects are primtiive it will assert based on custom user assert for primitives,
   *  if complex it will assert them by the fields values.
   *
   *  @param report
   *  @param expectedObject
   *  @param actualObject
   *  @param expectedChangedProperties
   *  		properties from this list take priority over the fields from expected object
   *  @return
   *  		<code> true </code> if objects can be asserted, <code> false </code> otherwise.
   *
   */
  def assertObjects(report: FabutReportBuilder, expectedObject: Any, actualObject: Any, expectedChangedProperties: Map[String, IProperty]): Boolean = {

    val isProperty = false
    val pair = AssertPair(EMPTY_STRING, expectedObject, actualObject, getAssertableType(actualObject), isProperty)

    val expectedProperties = if (expectedChangedProperties.isEmpty) {
      getObjectProperties(expectedObject, getObjectType(expectedObject, getAssertableType(expectedObject)))
    } else {
      expectedChangedProperties
    }

    val assertResult = assertPair(EMPTY_STRING, pair, expectedProperties, new NodesList)(report)

    if (assertResult) {
      afterAssertObject(actualObject, false)
    }
    assertResult
  }

  /**
   *  Handles asserting of actual object by checking expected object type of property
   *   and by the type it creates a pair for assert or ignores it.
   *
   *  @param actualProperty
   *  @param expectedProperty
   *  @param changedProperties
   *  @param nodesList
   *  @param report
   *  @return
   *  	<code> true </code> if actual property is successfully asserted with expected <code> false </code> otherwise.
   */
  def assertProperty(fieldName: String, actualObject: Any, property: IProperty, changedProperties: Map[String, IProperty], nodesList: NodesList, isProperty: Boolean)(implicit report: FabutReportBuilder): Boolean = {
    property match {
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

        val pair = AssertPair(fieldName, expectedProperty.value, actualObject, getAssertableType(actualObject), isProperty)
        val properties = getObjectProperties(pair.expected, getObjectType(pair.expected, pair.assertableType))

        if (pair.assertableType == COMPLEX_TYPE) {

          val nodeCheckType = nodesList.nodeCheck(pair)

          nodeCheckType match {
            case NEW_PAIR =>
              nodesList.addPair(pair.expected, pair.actual)
              assertPair(fieldName, pair, properties, nodesList)
            case CONTAINS_PAIR =>
              if (nodesList.containsPair(pair.expected, pair.actual)) {
                ISOMORPHIC_GRAPH
              } else {
                report.checkByReference(pair.path, pair.actual, false)
                NOT_ISOMORPHIC_GRAPH
              }
          }
        } else {
          assertPair(fieldName, pair, properties, nodesList)
        }

      }
      case _ => ASSERTED
    }
  }

  /**
   *  Helper method that checks for
   */
  def hasInnerProperties(parent: String, properties: Map[String, IProperty]): Boolean = {

    properties.keys.find(property => property.startsWith(parent)).getOrElse(return ASSERT_FAIL)

    ASSERTED

  }

  def assertPair(fieldName: String, pair: AssertPair, changedProperties: Map[String, IProperty], nodesList: NodesList)(implicit report: FabutReportBuilder): Boolean = {

    pair.assertableType match {
      case SCALA_LIST_TYPE =>
        assertListElements(fieldName, 0, pair.actual.asInstanceOf[List[Any]], pair.expected.asInstanceOf[List[Any]])(report)
      case SCALA_MAP_TYPE =>
        assertMapElements(fieldName, pair.actual.asInstanceOf[Map[Any, Any]], pair.expected.asInstanceOf[Map[Any, Any]])(report)
      case ENTITY_TYPE =>
        assertEntityPair(report, pair.path, pair, changedProperties, nodesList)
      case IGNORED_TYPE =>
        report.ignoredType(pair)
        ASSERTED
      case COMPLEX_TYPE =>
        assertSubfields(fieldName, pair, changedProperties, nodesList)
      case PRIMITIVE_TYPE =>
        assertPrimitives(pair, fieldName)
      case _ =>
        throw new IllegalStateException("Uknown assert type: " + pair.assertableType)
    }

  }

  def assertEntityPair(report: FabutReportBuilder, propertyName: String, pair: AssertPair, properties: Map[String, IProperty], nodesList: NodesList): Boolean = {
    throw new IllegalStateException("Entities are not supported!")
  }

  def assertInnerProperty(parentName: String, parentObject: Any, changedProperties: Map[String, IProperty], nodesList: NodesList, report: FabutReportBuilder): Boolean = {
    val extracts = extractPropertiesWithMatchingParent(parentName, changedProperties)
    report.increaseDepth(parentName)
    val result = assertObjectWithProperties(report, parentObject, extracts)
    report.decreaseDepth
    result
  }

  def extractPropertiesWithMatchingParent(parent: String, properties: Map[String, IProperty]): Map[String, IProperty] =
    properties collect { case (name: String, value: IProperty) if name.startsWith(parent + DOT) => (name.replaceFirst(parent + DOT, ""), value) }

  /**
   *  Helper class for asserting lists, goes through each object
   *    in actual list and asserts with corresponding in expected list
   *  @param position
   *  	actual position of objects we are asserting in list
   *  @param namePath
   *   	full name in object chain
   *  @param actualList
   *  	list in actual object
   *  @param expectedList
   *   	list in expected object
   *  @param isComplexType
   *  	if type exists in types complex list, if not it is valued as None
   */
  def assertListElements(propertyName: String, position: Int, actualList: List[_], expectedList: List[_])(implicit report: FabutReportBuilder): Boolean = {

    var result: Boolean = true

    if (actualList.size != expectedList.size) {
      report.listDifferentSizeComment(propertyName, expectedList.size, actualList.size)
      //      report.addCollectionSizeExceptionMessage("", actualList.size, expectedList.size)
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
          assertObjects(report, expected, actual, Map())
        }
        result &= assertListElements(propertyName, position + 1, actualList.tail, expectedList.tail)
      }
      case Nil => return result
    }

    report.decreaseDepth

    result
  }
  /**
   *  Helper class for asserting maps, for each key from actual map,
   *    checks if it exists in expected and asserts values
   *
   *  @param namePath
   *   	full name in object chain
   *  @param actualMap
   *  	map in actual object
   *  @param expectedMap
   *   	map in expected object
   */
  def assertMapElements(propertyName: String, actualMap: Map[Any, Any], expectedMap: Map[Any, Any])(implicit report: FabutReportBuilder): Boolean = {

    var result = ASSERTED

    if (actualMap.size != expectedMap.size) {
      report.mapDifferentSizeComment(propertyName, expectedMap.size, actualMap.size)
      //      report.addCollectionSizeExceptionMessage("", actualMap.size, expectedMap.size)
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

          result &= assertObjects(report, expected, actual, Map())

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

  def assertParameterSnapshot(report: FabutReportBuilder): Boolean = {
    var ok = true
    parameterSnapshot.foreach {
      case snapshotPair: SnapshotPair => {
        ok &= assertObjects(report, snapshotPair.expected, snapshotPair.actual, Map())
      }
    }
    initParametersSnapshot
    ok
  }

  def afterAssertObject(theObject: Any, isSubproperty: Boolean): Boolean = {
    ASSERT_FAIL
  }

  def assertPrimitives(pair: AssertPair, propertyName: String)(implicit report: FabutReportBuilder) = {

    try {
      fabutTest.customAssertEquals(pair.expected, pair.actual)
      report.asserted(pair, propertyName)
      ASSERTED
    } catch {
      case e: AssertionError =>
        report.assertFail(pair, propertyName)
        //        report.addPropertiesExceptionMessage(pair.path, pair.actual, pair.expected)
        ASSERT_FAIL
    }
  }

  def assertSubfields(propertyName: String, pair: AssertPair, changedProperties: Map[String, IProperty], nodesList: NodesList)(implicit report: FabutReportBuilder) = {

    val objectType = getObjectType(pair.expected, getAssertableType(pair.expected))
    val propertiesToBeAsserted = getObjectProperties(pair.expected, objectType)

    report.increaseDepth(propertyName)
    var ret = ASSERTED

    propertiesToBeAsserted foreach {
      case (propertyName, property) =>
        val expectedProperty = obtainProperty(property, propertyName, changedProperties)
        val actual = getFieldValueFromGetter(propertyName, pair.actual, objectType).getOrElse(throw new NoSuchElementException("Actual doesnt exist"))

        ret &= assertProperty(propertyName, actual, expectedProperty, changedProperties, nodesList, true)
    }

    report.decreaseDepth

    ret
  }

  def obtainProperty(property: IProperty, propertyPath: String, properties: Map[String, IProperty]) = {

    if (properties.contains(propertyPath)) {
      properties(propertyPath)
    } else {
      property
    }
  }

  /**
   *  Makes snapshot of specified
   */
  def takeSnapshot(report: FabutReportBuilder, parameters: Seq[Any]): Boolean = {

    initParametersSnapshot

    var result = ASSERTED
    parameters.foreach {
      entity =>
        try {
          val snapshotPair = SnapshotPair(false, entity, createCopy(entity))
          _parameterSnapshot += snapshotPair
        } catch {
          case e: CopyException => // TODO add to report copy exception message
            report.noCopy(entity)
            result &= ASSERT_FAIL;
        }
    }
    result
  }

  /**
   * Get the types
   *
   *  @return the types
   */

  def types: MutableMap[AssertableType, List[Type]] = _types

  def getEntityTypes =
    _types(ENTITY_TYPE)

}

