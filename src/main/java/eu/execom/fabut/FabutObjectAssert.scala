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

class FabutObjectAssert(fabutTest: IFabutTest) extends Assert {

  val DOT: String = "."
  val EMPTY_STRING = ""
  val ASSERTED = true
  val ASSERT_FAIL = false
  val ISOMORPHIC_GRAPH = true
  val NOT_ISOMORPHIC_GRAPH = false

  private var _parameterSnapshot: List[SnapshotPair] = List()
  def parameterSnapshot: List[SnapshotPair] = _parameterSnapshot

  var nameStack: String = ""
  var depth: Int = 0
  var types: MutableMap[AssertableType, List[Type]] = MutableMap()

  types(COMPLEX_TYPE) = fabutTest.getComplexTypes
  types(IGNORED_TYPE) = fabutTest.getIgnoredTypes
  types(ENTITY_TYPE) = List()

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
  def assertObjectWithProperties(report: FabutReport, actualObject: Any, properties: Map[String, IProperty], nodesList: NodesList): Boolean = {

    if (getValueType(actualObject) == IGNORED_TYPE)
      return true

    val actualObjectType = getValueType(actualObject)
    val actualProperties: Map[String, Property] = getObjectProperties(actualObject, getObjectType(actualObject, actualObjectType))

    var ret: Boolean = true

    actualProperties.values.foreach {
      case actualProperty: Property =>

        val expectedProperty: Option[IProperty] = try {
          Some(properties(actualProperty.path))
        } catch {
          case e: NoSuchElementException => None
        }

        expectedProperty match {
          case expectedProperty if (expectedProperty.isDefined) =>
            ret &= assertProperty(actualProperty, expectedProperty.get, properties, nodesList, true)(report)
          case expectedProperty if (hasInnerProperties(actualProperty.path, properties)) =>
            ret &= assertInnerProperty(actualProperty.path, actualProperty.value, properties, nodesList, report)
          case _ =>
            report.addMissingExpectedPropertyMessage(actualProperty.path)
            ret &= false
        }
    }
    ret
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
  def assertObjects(report: FabutReport, expectedObject: Any, actualObject: Any, expectedChangedProperties: Map[String, IProperty]): Boolean = {

    val pair = AssertPair(EMPTY_STRING, expectedObject, actualObject, getValueType(actualObject), false)
    val expectedProperties = if (expectedChangedProperties.isEmpty) {
      getObjectProperties(expectedObject, getObjectType(expectedObject, getValueType(expectedObject)))
    } else {
      expectedChangedProperties
    }

    val assertResult = assertPair(pair, expectedProperties, new NodesList)(report)
    //    if (assertResult) {
    //      afterAssertResult(actual, false)
    //    }
    return assertResult

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
  def assertProperty(actualProperty: Property, expectedProperty: IProperty, changedProperties: Map[String, IProperty], nodesList: NodesList, isProperty: Boolean)(implicit report: FabutReport): Boolean = {
    expectedProperty match {
      case _: IgnoredProperty =>
        ASSERTED
      case _: NullProperty =>
        if (actualProperty.value != null) {
          report.addNullExpectedException(actualProperty.path, actualProperty.value)
          ASSERT_FAIL
        } else {
          ASSERTED
        }

      case _: NotNullProperty =>
        if (actualProperty.value == null) {
          report.addNotNullExpectedException(actualProperty.path, actualProperty.value)
          ASSERT_FAIL
        } else {
          ASSERTED
        }

      case expectedProperty: Property => {

        val pair = AssertPair(actualProperty.getPath, expectedProperty.value, actualProperty.value, getValueType(actualProperty.value), isProperty)
        val properties = getObjectProperties(pair.expected, getObjectType(pair.expected, pair.assertableType))

        if (pair.assertableType == COMPLEX_TYPE) {

          val nodeCheckType = nodesList.nodeCheck(pair)

          nodeCheckType match {
            case NEW_PAIR =>
              nodesList.addPair(pair.expected, pair.actual)
              assertPair(pair, properties, nodesList)
            case CONTAINS_PAIR =>
              if (nodesList.containsPair(pair.expected, pair.actual))
                ISOMORPHIC_GRAPH
              else
                NOT_ISOMORPHIC_GRAPH
          }
        } else {
          assertPair(pair, properties, nodesList)
        }

      }
    }
  }

  /**
   *  Helper method that checks for
   */
  def hasInnerProperties(parent: String, properties: Map[String, IProperty]): Boolean = {

    val ret = properties.keys.find(property => property.startsWith(parent))

    if (ret.isDefined)
      ASSERTED
    else
      ASSERT_FAIL

  }

  def assertPair(pair: AssertPair, changedProperties: Map[String, IProperty], nodesList: NodesList)(implicit report: FabutReport): Boolean = {

    pair.assertableType match {
      case SCALA_LIST_TYPE =>
        assertListElements(0, pair.actual.asInstanceOf[List[Any]], pair.expected.asInstanceOf[List[Any]])(report)
      case SCALA_MAP_TYPE =>
        assertMapElements(pair.actual.asInstanceOf[Map[Any, Any]], pair.expected.asInstanceOf[Map[Any, Any]])(report)
      case ENTITY_TYPE =>
        assertEntityById(pair.path, pair.actual, pair.expected)
      case IGNORED_TYPE =>
        println("Report ignored type? ")
        true
      // TO-DO what?
      case COMPLEX_TYPE =>

        assertSubfields(pair.actual, changedProperties, nodesList)

      case PRIMITIVE_TYPE => {
        assertPrimitives(pair)
      }
    }

  }

  def assertInnerProperty(parentName: String, parentObject: Any, changedProperties: Map[String, IProperty], nodesList: NodesList, report: FabutReport): Boolean = {
    val extracts = extractPropertiesWithMatchingParent(parentName, changedProperties)
    assertObjectWithProperties(report, parentObject, extracts, nodesList)
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
  def assertListElements(position: Int, actualList: List[_], expectedList: List[_])(implicit report: FabutReport): Boolean = {

    var ret: Boolean = true

    if (actualList != expectedList) {
      report.addCollectionSizeExceptionMessage("", actualList.size, expectedList.size)
      return ASSERT_FAIL
    }

    actualList match {
      case head :: tail => {
        val actual = actualList.head
        val expected = expectedList.head
        if (getValueType(head) == PRIMITIVE_TYPE) {
          try {
            fabutTest.customAssertEquals(expected, actual)
            ret &= ASSERTED
          } catch {
            case e: AssertionError =>
              report.addListPropertyException(position, actual, expected)
              ret &= ASSERT_FAIL
          }
        } else {
          assertObjects(report, expected, actual, Map())
        }
        ret &= assertListElements(position + 1, actualList.tail, expectedList.tail)
      }
      case Nil => return ret
    }

    ret

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
  def assertMapElements(actualMap: Map[Any, Any], expectedMap: Map[Any, Any])(implicit report: FabutReport): Boolean = {

    var ret = true

    if (actualMap.size != expectedMap.size) {
      report.addCollectionSizeExceptionMessage("", actualMap.size, expectedMap.size)
      return ASSERT_FAIL
    }

    actualMap.keys.foreach {
      (name: Any) =>
        try {
          val actual = actualMap(name)
          val expected = expectedMap(name)
          val actualType = getObjectType(actual, getValueType(actual))
          val expectedType = getObjectType(expected, getValueType(expected))
          if (actualType != expectedType) {
            report.addNonMatchingTypesMessage(actualType.toString, expectedType.toString)
            ret &= ASSERT_FAIL
          } else if (!actualType.isDefined) {
            try {
              fabutTest.customAssertEquals(actual, expected)
            } catch {
              case e: AssertionError =>
                report.addPropertiesExceptionMessage(s" for key ${name} ", actual, expected)
                ret &= ASSERT_FAIL
            }
          } else {
            ret &= assertObjects(report, expected, actual, Map())
          }
        } catch {
          case e: NoSuchElementException =>
            report.addKeyNotFoundInExpectedMapMessage(name.toString)
        }
    }
    ret

  }

  def assertParameterSnapshot(report: FabutReport): Boolean = {
    var ok = false
    parameterSnapshot.foreach {
      case snapshotPair: SnapshotPair => {
        ok &= assertObjects(report, snapshotPair.expected, snapshotPair.actual, Map())
      }
    }

    initParametersSnapshot
    return ok
  }

  def initParametersSnapshot {
    _parameterSnapshot = List()
  }

  /** NOT USED*/
  def getReference(depth: Int, path: String, expectedObject: Any): Any = {
    if (depth > 0) {
      try {
        val newExpectedObjectName = path.split('.').toList.head
        val newExpectedObject = reflectObject(newExpectedObjectName, expectedObject, getObjectType(expectedObject, COMPLEX_TYPE)).get
        getReference(depth - 1, path.stripPrefix(newExpectedObjectName + DOT), newExpectedObject)
      } catch {
        case e: NoSuchElementException =>
      }
    } else {
      expectedObject
    }
  }

  /** NOT USED*/
  /**
   *  Method checks if two objects have isomorphic property graphs
   *
   *  How: We pass the position of the node to which recursion returns,
   *  in actual object 'depth'
   *
   *  @param depth
   *  	if chain of objects is ABCD and returns to object B is 2nd => 1234 returns to node 2,
   *   	depth is the returning to node number
   *  @param namePath
   *  	used to guide the expected object through reflection, as it should be
   *   	imaged from actual object
   *  @param expectedObject
   *
   *  Now we reflect fully the expected object chain, when we get to the depth
   *  to which it should be returned, we keep reference of object in 'actualReference'
   *  now we continue reflection and when we get to end (recursion) we put the value to
   *  'expectedReference' and compare those two references which should be same to satisfy
   *  isomorphism
   *
   */
  def checkIsomorphism(depth: Int, namePath: String, expectedObject: Any)(implicit report: FabutReport): Boolean = {

    var depthLevels = 0
    val actualReference = getReference(depth, namePath, expectedObject)

    println(s"actual ${actualReference}")
    for { path <- namePath.split('.').toList }
      depthLevels += path.size

    val expectedReference = getReference(depthLevels - depth, namePath.substring(2 * depth), actualReference)
    println(s"expected ${expectedReference}")
    if (actualReference != expectedReference) {
      report.addIsomorphicGraphExceptionMessage(depth)
      return false
    }

    true
  }

  /**
   *  Returns the depth of entering inside objects, used
   *  for checking if actual and expected objects are isomorphic
   *
   *  @param namePath
   *
   *  @return
   * 	numeric depth level
   */
  def getGraphDepth(namePath: String): Int = {
    if (namePath == "") {
      0
    } else {
      (namePath.split('.').size)
    }
  }

  def assertPrimitives(pair: AssertPair)(implicit report: FabutReport) = {
    try {
      fabutTest.customAssertEquals(pair.expected, pair.actual)
      ASSERTED
    } catch {
      case e: AssertionError =>
        report.addPropertiesExceptionMessage(pair.path, pair.actual, pair.expected)
        ASSERT_FAIL
    }
  }

  def assertSubfields(actualObject: Any, changedProperties: Map[String, IProperty], nodesList: NodesList)(implicit report: FabutReport) = {

    val actualObjectType = getValueType(actualObject)
    val actualProperties: Map[String, Property] = getObjectProperties(actualObject, getObjectType(actualObject, actualObjectType))

    var ret: Boolean = true

    actualProperties.values.foreach {
      case actualProperty: Property =>

        val expectedProperty: Option[IProperty] = try {
          Some(changedProperties(actualProperty.path))
        } catch {
          case e: NoSuchElementException => None
        }

        if (expectedProperty.isDefined) {
          ret &= assertProperty(actualProperty, expectedProperty.get, changedProperties, nodesList, true)
        } else if (hasInnerProperties(actualProperty.path, changedProperties)) {
          ret &= assertInnerProperty(actualProperty.path, actualProperty.value, changedProperties, nodesList, report)
        } else {
          report.addMissingExpectedPropertyMessage(actualProperty.path)
          ret &= false
        }
    }
    ret
  }

  def assertEntityById(entityObjectName: String, entityObject: Any, expectedObject: Any)(implicit report: FabutReport): Boolean = {
    val actualValue = getFieldValueFromGetter("id", entityObject, getObjectType(entityObject, ENTITY_TYPE))
    // val expectedEntityObject = reflectObject(entityObjectName, expectedObject, getObjectType(entityObject, ENTITY_TYPE))
    val expectedValue = getFieldValueFromGetter("id", expectedObject, getObjectType(expectedObject, ENTITY_TYPE))
    try {
      fabutTest.customAssertEquals(expectedValue, actualValue)
      ASSERTED

    } catch {
      case e: AssertionError => {
        report.addPropertiesExceptionMessage(entityObjectName + "id", actualValue, expectedValue)
        ASSERT_FAIL
      }
    }
  }

  /**
   *  Makes snapshot of specified
   */
  def takeSnapshot(report: FabutReport, parameters: Any*): Boolean = {

    _parameterSnapshot = List()

    var ret = ASSERTED
    parameters.foreach {
      objectInstance =>
        try {
          val snapshotPair = SnapshotPair(false, objectInstance, createCopy(objectInstance))
          _parameterSnapshot ::= snapshotPair
        } catch {
          case e: CopyException => // TODO add to report copy exception message
            ret &= ASSERT_FAIL;
            println(objectInstance);
            println("SRANJE");
        }

    }
    return ret
  }

  /**BLAAA old crap*/

  /**
   * Asserts 2 objects.
   *
   * @param actualObject
   *            actual object
   * @param expectedObject
   *            expected object
   * @param customProperties
   * 			List of expected regular, ignorable, null or not null properties
   *    for asserting instead of properties from expected object
   */
  def assert(report: FabutReport, expectedObject: Any, actualObject: Any, customProperties: Map[String, IProperty]) {

    /* 
     * Contains elements that are not yet expected and after algorithm wrong or unused properties
     * 
     * */
    var unusedExpectedProperties: Map[String, IProperty] = customProperties

    /**
     *  Asserts two collections, either scala map or scala list
     *
     *  @param pathcut
     *  	  the number of characters to be cut from full primitives name
     *      as we go deeper inside expected object to assert its primitive property
     *  @param namePath
     *  	  full path name where the property is positioned in graph
     *  @param actualCollection
     *  	  actual collection to be asserted
     *  @param expectedObject
     *  	  expected object to be reflected for getting the expected collection if depth is bigger then 1
     *  @param collectionType
     *  	type of collection from enumeration AssertableType -> SCALA_MAP_TYPE or SCALA_LIST_TYPE
     */

    //    TODO extract all methods to be at the same level, pass report as implict
    //    TODO no need for prefix error message here, it should be just passed to Fabut methods
    def assertCollection(pathcut: Int, namePath: String, actualCollection: Any, expectedObject: Any, collectionType: AssertableType) {

      val propertyDepth = namePath.substring(pathcut).split('.')

      var expectedCollection: Any = null

      //      TODO expectedCollection can be modified to be instantiated like this val  expectedCollection = try{...
      try {
        expectedCollection = customProperties(namePath).asInstanceOf[Property].value
        unusedExpectedProperties -= namePath
      } catch {
        case t: NoSuchElementException =>
          {

            if (propertyDepth.size > 1) {
              val newExpectedObjectName = propertyDepth.head

              val newExpectedObject = reflectObject(newExpectedObjectName, expectedObject, getObjectType(expectedObject, COMPLEX_TYPE)).get
              if (newExpectedObject != None) {
                assertCollection(pathcut + newExpectedObjectName.size + 1, namePath, actualCollection, newExpectedObject, collectionType)
              }
              return
            } else {
              if (propertyDepth.head == "") { // case "" is when assertObjects checks 2 List/Maps
                expectedCollection = expectedObject
              } else {
                expectedCollection = getFieldValueFromGetter(propertyDepth.head, expectedObject, getObjectType(expectedObject, COMPLEX_TYPE)).get
              }

            }

          }
      } // end catch

      var expectedCollectionSize = 0;
      var actualCollectionSize = 0;

      collectionType match {
        case SCALA_LIST_TYPE =>
          actualCollectionSize = actualCollection.asInstanceOf[List[Any]].size
          expectedCollectionSize = expectedCollection.asInstanceOf[List[Any]].size
        case SCALA_MAP_TYPE =>
          actualCollectionSize = actualCollection.asInstanceOf[Map[Any, Any]].size
          expectedCollectionSize = expectedCollection.asInstanceOf[Map[Any, Any]].size
        case _ =>
          throw new Error("For future implementation of more collections")
      }

      if (actualCollectionSize != expectedCollectionSize) {
        report.addCollectionSizeExceptionMessage(namePath, actualCollectionSize, expectedCollectionSize)
      } else {
        collectionType match {
          case SCALA_LIST_TYPE =>
            println("TODO - modify assert list a lil bit")
            assertListElements(0, namePath, actualCollection.asInstanceOf[List[Any]], expectedCollection.asInstanceOf[List[Any]], getObjectType(actualCollection.asInstanceOf[List[_]].head, COMPLEX_TYPE))
          case SCALA_MAP_TYPE =>
          //assertMapElements(namePath, actualCollection.asInstanceOf[Map[Any, Any]], expectedCollection.asInstanceOf[Map[Any, Any]])
          case _ => throw new Error("For future implementation of more collections")
        }
      }

    }

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
    def assertListElements(position: Int, namePath: String, actualList: List[_], expectedList: List[_], isComplexType: Option[Type]) {

      actualList match {
        case head :: tail => {

          if (isComplexType == None) {
            try {
              fabutTest.customAssertEquals(expectedList.head, actualList.head)
            } catch {
              case e: AssertionError =>
                report.addListPropertyException(position, actualList.head, expectedList.head)
            }
          } else {
            assertNode(expectedList.head, actualList.head, "", Map())
          }
          assertListElements(position + 1, namePath, actualList.tail, expectedList.tail, isComplexType)
        }
        case Nil => return
      }

    }

    /**
     *  Fetches properties from actual object, asserts each actual property
     *   with corresponding in expected object or with one from expected property map,
     *   and for each complex object as property recursively calls itself to assert it
     *
     * 	 @param expected
     *   	the expected object
     *   @param actual
     *   	the actual object
     *   @param namePath
     *   	symbolic path of actual object, used to check if properties
     *      and objects are on same position in expected
     *   @checkedObjects
     *   	Map that has key as object reference that we've already asserted and value as
     *    	numeric position in actual objects chain
     *   @prefixErrorMessage
     *   	Part of message used if algorithm gets inside collection for remembering the path
     *      because of recursive usage of assertGraph method
     */
    def assertNode(expected: Any, actual: Any, namePath: String, checkedObjectsMap: Map[Any, Int]) {

      /**
       * Asserts primitive properties from actual object with corresponding pairs from expected object
       *   via abstract method assertEqualsObjects, adds mismatch into report.
       *   Primitive property is any class not marked as complex, entity or ignored type
       *
       *  @param pathcut
       *  	the number of characters to be cut from full primitives name
       *      as we go deeper inside expected object to assert its primitive property
       *  @param properties
       *  	primitive types with name and value to be asserted
       *  @param expectedObject
       *  	the expected object
       *
       */
      def assertPrimitiveProperties(pathcut: Int, properties: Map[String, IProperty], expectedObject: Any) {

        /**
         * - Checks if all properties are specified in case of assertObject call of the algorithm,
         * 	    or in case of assertObjects call, it partitions the expected properties
         *      to assert from the predefined expected properties list
         *
         *  @param propertyName
         *  	property name to be checked in expected map
         *
         * 	@return
         *  	true if exists in expected properties list else false
         */
        def splitExpectedProperties(propertyName: String): Boolean = {
          try {
            customProperties(propertyName); true
          } catch {
            case e: NoSuchElementException =>
              if (expected == Nil) {
                report.addMissingExpectedPropertyMessage(propertyName)
              }; false
          }
        }

        if (properties.size == 0) return

        var regularExpectedProperties: Map[String, IProperty] = Map()

        val (expectedProperties, unexpectedProperties) = properties partition {
          case (name: String, property: IProperty) => splitExpectedProperties(name)
        }

        // transform all actual Property to custom Property (NullProperty, IgnoredProperty)..
        expectedProperties.keys.foreach {
          propertyName =>
            customProperties(propertyName) match {
              case property: NullProperty =>
                regularExpectedProperties ++= Map(property.path -> NullProperty(property.path))
              case property: Property =>
                val actualPropertyValue = expectedProperties(property.path).asInstanceOf[Property].value
                regularExpectedProperties ++= Map(property.path -> Property(property.path, actualPropertyValue))
              case property: NotNullProperty =>
                regularExpectedProperties ++= Map(property.path -> NotNullProperty(property.path))
            }
        }

        if (expected == Nil) { // called from asertObject

          regularExpectedProperties.values.foreach {
            property =>
              try {
                property match {
                  case property: Property =>
                    fabutTest.customAssertEquals(property.value, unusedExpectedProperties(property.path).asInstanceOf[Property].value)

                  case property: NullProperty =>
                    val propertyValue = expectedProperties(property.path).asInstanceOf[Property].value
                    if (propertyValue != null) {
                      report.addNullExpectedException(property.path, propertyValue)
                    }

                  case property: NotNullProperty =>
                    val propertyValue = expectedProperties(property.path).asInstanceOf[Property].value
                    if (propertyValue == null) {
                      report.addNotNullExpectedException(property.path, propertyValue)
                    }
                }
              } catch {
                case e: AssertionError => {
                  //report.addPropertiesExceptionMessage(property.getPath, property.asInstanceOf[Property].value, unusedExpectedProperties(property.getPath).asInstanceOf[Property].value)
                }
              }
            //unusedExpectedProperties -= property.getPath

          }
        } else { // called from assertObjects

          val propertyDepth = properties.head._1.substring(pathcut).split('.')
          val (reg, unu) = removeIgnoredAndNullProperties(properties, customProperties, unusedExpectedProperties, report)
          regularExpectedProperties = reg
          if (propertyDepth.size == 1) {
            //            unusedExpectedProperties = reflectPrimitiveProperties(pathcut, regularExpectedProperties, expectedObject, getObjectType(expectedObject, COMPLEX_TYPE), unusedExpectedProperties, report)
          } else {
            try {
              val newExpectedObjectName = propertyDepth.head
              val newExpectedObject = reflectObject(newExpectedObjectName, expectedObject, getObjectType(expectedObject, COMPLEX_TYPE)).get
              assertPrimitiveProperties(pathcut + newExpectedObjectName.size + 1, properties, newExpectedObject)
            } catch {
              case e: NoSuchElementException => println("Cannot get expected object, missing type?")
            }
          }
        }
      }

      //  method assertGraph entrance 

      val checkedObjects: Map[Any, Int] = checkedObjectsMap ++ Map(actual -> getGraphDepth(namePath))
      val objectProperties = getObjectProperties(actual, getObjectType(actual, getValueType(actual)))

      if (objectProperties nonEmpty) {

        val (primitiveProperties, nonPrimitiveProperties) = objectProperties partition {
          case (name: String, property: Property) => getValueType(property.value) == PRIMITIVE_TYPE
        }

        if (primitiveProperties nonEmpty) {
          assertPrimitiveProperties(0, primitiveProperties, expected)
        }

        val (filteredProperties, unusuedPropertiez) = removeIgnoredAndNullProperties(nonPrimitiveProperties, customProperties, unusedExpectedProperties, report)
        unusedExpectedProperties = unusuedPropertiez
        filteredProperties.values foreach {
          case (property: Property) =>
            getValueType(property.value) match {
              case SCALA_LIST_TYPE =>
                assertCollection(0, property.path, property.value, expected, SCALA_LIST_TYPE)
              case SCALA_MAP_TYPE =>
                assertCollection(0, property.path, property.value, expected, SCALA_MAP_TYPE)
              case ENTITY_TYPE =>
                assertEntityById(property.path, property.value, expected)(report)
              case IGNORED_TYPE =>
              // TO-DO what?
              case COMPLEX_TYPE => {
                if (checkedObjects.contains(property.value))
                  checkIsomorphism(checkedObjects(property.value), property.path, expected)(report)
                else
                  assertNode(expected, property.value, property.path + DOT, checkedObjects)
              }
            }
        }

      } else {

      }
    }

    getValueType(actualObject) match {
      case PRIMITIVE_TYPE => {
        try {
          fabutTest.customAssertEquals(expectedObject, actualObject)
        } catch {
          case e: AssertionError =>
            report.addPropertiesExceptionMessage("", actualObject, expectedObject)
        }
      }

      //      TODO what are ""??? 
      case SCALA_LIST_TYPE =>
        assertCollection(0, "", actualObject, expectedObject, SCALA_LIST_TYPE)
      case SCALA_MAP_TYPE =>
        assertCollection(0, "", actualObject, expectedObject, SCALA_MAP_TYPE)
      case _ =>
        assertNode(expectedObject, actualObject, "", Map())
    }

    if (unusedExpectedProperties.nonEmpty)
      unusedExpectedProperties foreach {
        case (propertyName: String, propertyValue: Any) =>
          report.addUnusedPropertyMessage(propertyName, propertyValue)
      }

  }

  def removeIgnoredAndNullProperties(properties: Map[String, IProperty], customProperties: Map[String, IProperty], unusedPropertiez: Map[String, IProperty], report: FabutReport): (Map[String, IProperty], Map[String, IProperty]) = {

    val ignoreAndNullProperties = customProperties.values.filter(property => !property.isInstanceOf[Property])

    var unusedProperties = unusedPropertiez
    var filteredProperties = properties

    //    ignoreAndNullProperties.foreach {
    //      property =>
    //        try {
    //          property match {
    //            case _: NullProperty =>
    //              val propertyValue = properties(property.getPath).asInstanceOf[Property].value
    //              if (propertyValue != null) {
    //                report.addNullExpectedException(property.getPath, propertyValue)
    //              }
    //            case _: NotNull =>
    //              val propertyValue = properties(property.getPath).asInstanceOf[Property].value
    //              if (propertyValue == null) {IProperty
    //                report.addNullExpectedException(property.getPath, propertyValue)
    //              }
    //            case _: IgnoredProperty =>
    //          }
    //          filteredProperties -= property.getPath
    //          unusedProperties -= property.getPath
    //        } catch {
    //          case e: NoSuchElementException =>
    //        }
    (filteredProperties, unusedProperties)
  }

  /**
   * Get the types
   *
   *  @return the types
   */

  def getTypes: MutableMap[AssertableType, List[Type]] = types

  /**
   *  Sets the types map
   *
   *  @param types
   *  		the Fabut object types
   */
  def setTypes(types: MutableMap[AssertableType, List[Type]]) =
    this.types = types

  def getEntityTypes =
    this.types(ENTITY_TYPE)

}

