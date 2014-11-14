package eu.execom.fabut

import scala.collection.immutable.Map
import scala.collection
import scala.reflect.runtime.universe.{ Type, TypeTag, typeOf, Symbol, InstanceMirror }
import eu.execom.fabut.enums.AssertType._
import eu.execom.fabut.enums.AssertableType._
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.model.ObjectWithComplexProperty
import eu.execom.fabut.model.ObjectWithSimpleProperties
import eu.execom.fabut.model.TrivialClasses._
import eu.execom.fabut.util.ReflectionUtil._
import scala.collection.mutable.{ Map => MutableMap }
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.exception.TypeMissingException
import org.junit.Assert
import eu.execom.fabut.model.ObjectWithComplexProperty
import eu.execom.fabut.model.NormalClass
import eu.execom.fabut.model.TierTwoType
import eu.execom.fabut.model.EntityTierOneType
import eu.execom.fabut.model.EntityTierThreeType
import eu.execom.fabut.property.Property
import eu.execom.fabut.property.IProperty
import eu.execom.fabut.property.IgnoredProperty
import eu.execom.fabut.property.IgnoredProperty
import eu.execom.fabut.property.NullProperty
import eu.execom.fabut.property.NullProperty
import eu.execom.fabut.property.NullProperty
import eu.execom.fabut.property.IgnoredProperty
import eu.execom.fabut.property.Property
import eu.execom.fabut.property.NullProperty
import eu.execom.fabut.model.EmptyClass
import eu.execom.fabut.property.NotNullProperty
import eu.execom.fabut.property.NotNullProperty
import eu.execom.fabut.property.NotNullProperty
import eu.execom.fabut.property.IgnoredProperty

case class FabutObjectAssert(fabutTest: IFabutTest) extends Assert {

  val DOT: String = "."
  var types: MutableMap[AssertableType, List[Type]] = MutableMap()

  types(COMPLEX_TYPE) = fabutTest.getComplexTypes
  types(IGNORED_TYPE) = fabutTest.getIgnoredTypes
  types(ENTITY_TYPE) = fabutTest.getEntityTypes

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
    def assertCollection(prefixErrorMessage: String, pathcut: Int, namePath: String, actualCollection: Any, expectedObject: Any, collectionType: AssertableType) {

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

              val newExpectedObject = reflectObject(newExpectedObjectName, expectedObject, getTypeFromTypes(expectedObject, COMPLEX_TYPE)).get
              if (newExpectedObject != None) {
                assertCollection(prefixErrorMessage, pathcut + newExpectedObjectName.size + 1, namePath, actualCollection, newExpectedObject, collectionType)
              }
              return
            } else {
              if (propertyDepth.head == "") { // case "" is when assertObjects checks 2 List/Maps
                expectedCollection = expectedObject
              } else {
                expectedCollection = getFieldValueFromGetter(propertyDepth.head, expectedObject, getTypeFromTypes(expectedObject, COMPLEX_TYPE)).get
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
        report.addCollectionSizeExceptionMessage(prefixErrorMessage, namePath, actualCollectionSize, expectedCollectionSize)
      } else {
        collectionType match {
          case SCALA_LIST_TYPE =>
            println("TODO - modify assert list a lil bit")
            assertListElements(0, namePath, actualCollection.asInstanceOf[List[Any]], expectedCollection.asInstanceOf[List[Any]], getTypeFromTypes(actualCollection.asInstanceOf[List[_]].head, COMPLEX_TYPE))
          case SCALA_MAP_TYPE =>
            assertMapElements(namePath, actualCollection.asInstanceOf[Map[Any, Any]], expectedCollection.asInstanceOf[Map[Any, Any]])
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

      val prefixErrorMessage = s"In list '${namePath}' at position ${position}"

      actualList match {
        case head :: tail => {

          if (isComplexType == None) {
            try {
              fabutTest.customAssertEquals(expectedList.head, actualList.head)
            } catch {
              case e: AssertionError =>
                report.addListPropertyException(prefixErrorMessage, position, actualList.head, expectedList.head)
            }
          } else {
            assertNode(expectedList.head, actualList.head, "", Map(), prefixErrorMessage)
          }
          assertListElements(position + 1, namePath, actualList.tail, expectedList.tail, isComplexType)
        }
        case Nil => return
      }

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
    def assertMapElements(namePath: String, actualMap: Map[Any, Any], expectedMap: Map[Any, Any]) {

      val prefixErrorMessage = s"In map '${namePath}' "

      //      TOOD Remove recursiveLoop, this method should assert by using foreach and assertObjects calls
      def recursiveLoop(actualMapKeys: List[_]) {

        actualMapKeys match {
          case head :: tail =>
            try {
              val actualMapElementType = getTypeFromTypes(actualMap(head), COMPLEX_TYPE)
              val expectedMapElementType = getTypeFromTypes(actualMap(head), COMPLEX_TYPE)
              val actualMapElementValue = actualMap(head)
              val expectedMapElementValue = expectedMap(head)
              if (actualMapElementType != expectedMapElementType) {
                report.addNonMatchingTypesMessage(prefixErrorMessage, actualMapElementType.toString, expectedMapElementType.toString)
              } else if (actualMapElementType == None) {
                try {
                  fabutTest.customAssertEquals(expectedMapElementValue, actualMapElementValue)
                } catch {
                  case e: AssertionError =>
                    report.addPropertiesExceptionMessage(prefixErrorMessage, namePath + s" for key ${head} ", actualMapElementValue, expectedMapElementValue)
                }
              } else {
                assertNode(expectedMap(head), actualMap(head), "", Map(), prefixErrorMessage + s" for key ${head}")
              }
            } catch {
              case e: NoSuchElementException =>
                report.addKeyNotFoundInExpectedMapMessage(prefixErrorMessage, head.toString)
            }

            recursiveLoop(tail)

          case Nil => return
        }
      }

      recursiveLoop(actualMap.keySet.toList)
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
    def assertNode(expected: Any, actual: Any, namePath: String, checkedObjectsMap: Map[Any, Int], prefixErrorMessage: String) {

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
                report.addMissingExpectedPropertyMessage(prefixErrorMessage, propertyName)
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
                  report.addPropertiesExceptionMessage(prefixErrorMessage, property.getNamePath, property.asInstanceOf[Property].value, unusedExpectedProperties(property.getNamePath).asInstanceOf[Property].value)
                }
              }
              unusedExpectedProperties -= property.getNamePath

          }
        } else { // called from assertObjects

          val propertyDepth = properties.head._1.substring(pathcut).split('.')
          val (regularLALSD, unusdee) = removeIgnoredAndNullProperties(properties, customProperties, unusedExpectedProperties, report)
          if (propertyDepth.size == 1) {
            unusedExpectedProperties = reflectPrimitiveProperties(prefixErrorMessage, pathcut, regularExpectedProperties, expectedObject, getTypeFromTypes(expectedObject, COMPLEX_TYPE), unusedExpectedProperties, report)
          } else {
            try {
              val newExpectedObjectName = propertyDepth.head
              val newExpectedObject = reflectObject(newExpectedObjectName, expectedObject, getTypeFromTypes(expectedObject, COMPLEX_TYPE)).get
              assertPrimitiveProperties(pathcut + newExpectedObjectName.size + 1, properties, newExpectedObject)
            } catch {
              case e: NoSuchElementException => println("Cannot get expected object, missing type?")
            }
          }
        }
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

      def assertEntityById(entityObjectName: String, entityObject: Any, expectedObject: Any) {
        val actualValue = getFieldValueFromGetter("id", entityObject, getTypeFromTypes(entityObject, ENTITY_TYPE))
        val expectedEntityObject = reflectObject(entityObjectName, expectedObject, getTypeFromTypes(entityObject, ENTITY_TYPE))
        val expectedValue = getFieldValueFromGetter("id", expectedEntityObject, getTypeFromTypes(expectedEntityObject, ENTITY_TYPE))
        try {
          fabutTest.customAssertEquals(expectedValue, actualValue)
        } catch {
          case e: AssertionError => {
            report.addPropertiesExceptionMessage(prefixErrorMessage, entityObjectName + "id", actualValue, expectedValue)
          }
        }
      }

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
      def checkIsomorphism(depth: Int, namePath: String, expectedObject: Any, prefixErrorMessage: String) {

        def recursiveLoop(depth: Int, path: String, expectedObject: Any): Any = {
          if (depth > 0) {
            try {
              val newExpectedObjectName = path.split('.').toList.head
              val newExpectedObject = reflectObject(newExpectedObjectName, expectedObject, getTypeFromTypes(expectedObject, COMPLEX_TYPE)).get
              recursiveLoop(depth - 1, path.stripPrefix(newExpectedObjectName + DOT), newExpectedObject)
            } catch {
              case e: NoSuchElementException =>
            }
          } else {
            expectedObject
          }
        }

        var depthLevels = 0
        val actualReference = recursiveLoop(depth, namePath, expectedObject)

        for { path <- namePath.split('.').toList }
          depthLevels += path.size

        val expectedReference = recursiveLoop(depthLevels - depth, namePath.substring(2 * depth), actualReference)

        if (actualReference != expectedReference) {
          report.addIsomorphicGraphExceptionMessage(prefixErrorMessage, depth)
        }

      }

      //  method assertGraph entrance 

      val checkedObjects: Map[Any, Int] = checkedObjectsMap ++ Map(actual -> getGraphDepth(namePath))
      val objectProperties = getObjectProperties(actual, namePath, getTypeFromTypes(actual, getValueType(actual)))

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
                assertCollection(prefixErrorMessage, 0, property.path, property.value, expected, SCALA_LIST_TYPE)
              case SCALA_MAP_TYPE =>
                assertCollection(prefixErrorMessage, 0, property.path, property.value, expected, SCALA_MAP_TYPE)
              case ENTITY_TYPE =>
                assertEntityById(property.path, property.value, expected)
              case IGNORED_TYPE =>
              // TO-DO what?
              case COMPLEX_TYPE => {
                if (checkedObjects.contains(property.value))
                  checkIsomorphism(checkedObjects(property.value), property.path, expected, prefixErrorMessage)
                else
                  assertNode(expected, property.value, property.path + DOT, checkedObjects, "")
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
            report.addPropertiesExceptionMessage("", "", actualObject, expectedObject)
        }
      }

      //      TODO what are ""??? 
      case SCALA_LIST_TYPE =>
        assertCollection("", 0, "", actualObject, expectedObject, SCALA_LIST_TYPE)
      case SCALA_MAP_TYPE =>
        assertCollection("", 0, "", actualObject, expectedObject, SCALA_MAP_TYPE)
      case _ =>
        assertNode(expectedObject, actualObject, "", Map(), "")
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

    ignoreAndNullProperties.foreach {
      property =>
        try {
          property match {
            case _: NullProperty =>
              val propertyValue = properties(property.getNamePath).asInstanceOf[Property].value
              if (propertyValue != null) {
                report.addNullExpectedException(property.getNamePath, propertyValue)
              }
            case _: NotNull =>
              val propertyValue = properties(property.getNamePath).asInstanceOf[Property].value
              if (propertyValue == null) {
                report.addNullExpectedException(property.getNamePath, propertyValue)
              }
            case _: IgnoredProperty =>
          }
          filteredProperties -= property.getNamePath
          unusedProperties -= property.getNamePath
        } catch {
          case e: NoSuchElementException =>
        }

    }
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

}

//TODO Remove main
object Main extends App {

}

