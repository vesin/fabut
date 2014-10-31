package eu.execom.fabut

import scala.collection.immutable.Map
import scala.collection.mutable.{ Map => MutableMap }
import scala.collection
import scala.reflect.runtime.universe.{ Type, TypeTag, typeOf, Symbol }

import eu.execom.fabut.enums.AssertType._
import eu.execom.fabut.enums.AssertableType._
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.model.ObjectWithComplexProperty
import eu.execom.fabut.model.ObjectWithSimpleProperties
import eu.execom.fabut.model.TrivialClasses._
import eu.execom.fabut.util.ReflectionUtil._

/**
 * TODO
 * enter tasks here ->
 *
 */

class FabutObjectAssert

object FabutObjectAssert {

  lazy val DOT: String = "."

  var types: MutableMap[AssertableType, List[Type]] = MutableMap()
  types(COMPLEX_TYPE) = List()
  types(ENTITY_TYPE) = List()
  types(IGNORED_TYPE) = List()

  /**
   *  Asserts actual object with all expected properties (case +-+)
   */
  def assertObjects(actualObject: Any, expectedProperties: Property*) {
    assert(actualObject, Nil, createExpectedPropertiesMap(expectedProperties))
  }

  /**
   *  Asserts actual object with expected object (case ++-)
   */
  def assertObjects(actualObject: Any, expectedObject: Any) {
    assert(actualObject, expectedObject, Map())
  }

  /**
   *  Asserts actual object with expected object,
   *  by taking the expected properties from list and for those that are not in it, takes corresponding ones from expected object
   *
   *  case (+++)
   */
  def assertObjects(actualObject: Any, expectedObject: Any, expectedProperties: Property*) {
    assert(actualObject, expectedObject, createExpectedPropertiesMap(expectedProperties))
  }

  /**
   *  Turns Seq of expected properties to Map
   */
  def createExpectedPropertiesMap(properties: Seq[Property]): Map[String, Any] =
    properties.map { property => (property.namePath, property.expectedValue) } toMap

  /**
   * Asserts 2 objects.
   *
   * @param actualObject
   *            actual object
   * @param expectedObject
   *            expected object
   * @param expectedObjectProperties
   * 			expected object properties
   */
  def assert(actualObject: Any, expectedObject: Any, expectedObjectProperties: Map[String, Any]) {

    /* Contains elements that are not yet expected or sufficient value properties after assert*/
    var uncheckedExpectedObjectProperties = expectedObjectProperties

    /**
     *  Fetches properties from actual object, asserts each actual property
     *   with corresponding in expected object or with one from expected property map, and for each complexobject as property recursively calls
     *   itself to assert it
     *
     *   @param actual
     *   	the actual object
     *   @param expected
     *   	the expected object
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
    def assertGraph(actual: Any, expected: Any, namePath: String, checkedObjects: Map[Any, Int], prefixErrorMessage: String) {

      /**
       * Returns a type of given value
       *
       * @param value
       * @return
       * 		one of assertable types
       */
      def getValueType(value: Any) = {

        if (value.isInstanceOf[List[_]])
          SCALA_LIST_TYPE
        else if (value.isInstanceOf[Map[_, _]])
          SCALA_MAP_TYPE
        else if (getType(value, COMPLEX_TYPE) != None)
          COMPLEX_TYPE
        else if (getType(value, ENTITY_TYPE) != None)
          ENTITY_TYPE
        else if (getType(value, IGNORED_TYPE) != None)
          IGNORED_TYPE
        else
          PRIMITIVE_TYPE
      }

      /**
       * Returns object type from 'types' for given value
       *
       *  @param objectValue
       *  	object for which we check the type
       *  @param assertableType
       *  	the list of predefined objects from map of types where we should search for value
       *  @return
       *  	the specific type of object
       */
      def getType(objectValue: Any, assertableType: AssertableType): Option[Type] = {

        // suspicious, repair?!
        if (objectValue == null) {
          return None
        }

        types(assertableType).find(typeName =>
          (typeName.toString == objectValue.getClass.getCanonicalName)) match {
          case n: Some[Type] => Some(n.get)
          case _ => None
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
      def getGraphDepth(namePath: String): Int =
        if (namePath == "") {
          0
        } else {
          (namePath.split('.').size)
        }

      /**
       *  Asserts primitive types of given object with expected corresponding
       *  values inside expected object
       *
       *  @param pathcut
       *  	the number of characters to be cut from full primitives name
       *      as we go deeper inside expected object to assert its primitive property
       *  @param primitiveProperties
       *  	primitive types with name and value to be asserted
       *  @param expectedObject
       *  	the expected object
       *
       */
      def assertPrimitiveProperties(pathcut: Int, primitiveProperties: Map[String, Any], expectedObject: Any) {

        /**
         *  Checks if all properties are specified in case of +-+ and reports missing ones,
         *  and this helper class is used to partition the expected properties from unexpected ones
         *
         *  @param propertyName
         *  	property name to be checked in expected map
         *
         */
        def isInExpectedPropertiesList(propertyName: String): Boolean = {
          try {
            expectedObjectProperties(propertyName); true
          } catch {
            case e: NoSuchElementException =>
              if (expected == Nil) {
                report.addMissingExpectedPropertyMessage(prefixErrorMessage, propertyName)
              }; false
          }
        }

        val (expectedProperties, unexpectedProperties) = primitiveProperties partition {
          p: (String, Any) => isInExpectedPropertiesList(p._1)
        }

        if (expected == Nil) {
          //	CASE +-+
          if (expectedProperties.nonEmpty) {
            expectedProperties.foreach { property =>
              {
                if (property._2 != uncheckedExpectedObjectProperties(property._1))
                  report.addPropertiesExceptionMessage(prefixErrorMessage, property._1, property._2, uncheckedExpectedObjectProperties(property._1))
                uncheckedExpectedObjectProperties -= property._1
              }
            }
          }
        } else {
          //	CASE  ++- & +++

          val (propertyKey, propertyValue) = primitiveProperties.head
          val propertyDepth = propertyKey.substring(pathcut).split('.')

          if (propertyDepth.size == 1) {
            uncheckedExpectedObjectProperties = reflectPrimitives(prefixErrorMessage, pathcut, primitiveProperties, expectedObject, getType(expectedObject, COMPLEX_TYPE), uncheckedExpectedObjectProperties, report)
          } else {
            try {
              val newExpectedObjectName = propertyDepth.head
              val newExpectedObject = reflectObject(newExpectedObjectName, expectedObject, getType(expectedObject, COMPLEX_TYPE)).get
              assertPrimitiveProperties(pathcut + newExpectedObjectName.size + 1, primitiveProperties, newExpectedObject)
            } catch {
              case e: NoSuchElementException => println("Cannot get expected object, missing type?")
            }
          }
        }
      }

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
      def assertCollection(pathcut: Int, namePath: String, actualCollection: Any, expectedObject: Any, collectionType: AssertableType) {

        val propertyDepth = namePath.substring(pathcut).split('.')
        var expectedCollection: Any = null

        try {
          expectedCollection = expectedObjectProperties(namePath)
          uncheckedExpectedObjectProperties -= namePath
        } catch {
          case t: NoSuchElementException => {
            if (propertyDepth.size > 1) {
              val newExpectedObjectName = propertyDepth.head
              val newExpectedObjectOption = reflectObject(newExpectedObjectName, expectedObject, getType(expectedObject, COMPLEX_TYPE))
              val newExpectedObject = newExpectedObjectOption.get
              if (newExpectedObject != None)
                assertCollection(pathcut + newExpectedObjectName.size + 1, namePath, actualCollection, newExpectedObject, collectionType)
              return
            } else {
              expectedCollection = reflectProperty(propertyDepth.head, expectedObject, getType(expectedObject, COMPLEX_TYPE)).get
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
              assertListElements(0, namePath, actualCollection.asInstanceOf[List[Any]], expectedCollection.asInstanceOf[List[Any]], getType(actualCollection.asInstanceOf[List[_]].head, COMPLEX_TYPE))
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
              assertPrimitiveListProperty(position, actualList.head, expectedList.head)
            } else {
              assertGraph(actualList.head, expectedList.head, "", Map(), prefixErrorMessage)
            }

            assertListElements(position + 1, namePath, actualList.tail, expectedList.tail, isComplexType)
          }
          case Nil => ()
        }

        def assertPrimitiveListProperty(position: Int, actualProperty: Any, expectedProperty: Any) {
          if (actualProperty != expectedProperty) {
            report.addListPropertyException(prefixErrorMessage, position, actualProperty, expectedProperty)
          }
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

        def loop(actualMapKeys: List[_]) {

          actualMapKeys match {
            case head :: tail =>
              try {
                val actualMapElementType = getType(actualMap(head), COMPLEX_TYPE)
                val expectedMapElementType = getType(actualMap(head), COMPLEX_TYPE)
                val actualMapElementValue = actualMap(head)
                val expectedMapElementValue = expectedMap(head)
                if (actualMapElementType != expectedMapElementType) {
                  report.addNonMatchingTypesMessage(prefixErrorMessage, actualMapElementType.toString, expectedMapElementType.toString)
                } else if (actualMapElementType == None && (actualMapElementValue != expectedMapElementValue)) {
                  report.addPropertiesExceptionMessage(prefixErrorMessage, namePath + s" for key ${head} ", actualMapElementValue, expectedMapElementValue)
                } else {
                  assertGraph(actualMap(head), expectedMap(head), "", Map(), prefixErrorMessage + s" for key ${head}")
                }
              } catch {
                case e: NoSuchElementException =>
                  report.addKeyNotFoundInExpectedMapMessage(prefixErrorMessage, head.toString)
              }

              loop(tail)

            case Nil => ()
          }
        }

        // method assertMapElements entrance
        loop(actualMap.keySet.toList)
      }

      /**
       *  Method checks if two objects have isomorphic property graphs
       *
       *  How: We pass the position of the node to which recursion returns,
       *  in actual object 'depth'
       *
       *  @param depth
       *  	if chain of objects is ABCD and returns to B => 1234 returns to node 2,
       *   	depth is the returning to node number
       *  @param namePath
       *  	used to guide the expected object through reflection, as it should be
       *   	imaged from actual object
       *  @expectedObject
       *
       *  Now we reflect fully the expected object chain, when we get to the depth
       *  to which it should be returned, we keep reference of object in 'expectedToReturnToRef'
       *  now we continue reflection and when we get to end (recursion) we put the value to
       *  'expectedReturnsToRef' and compares those two references which should be same to
       *  isomorphism
       *
       */
      def checkIsomorphism(depth: Int, namePath: String, expectedObject: Any) {

        def loop(depth: Int, path: String, expectedObject: Any): Any = {
          if (depth > 0) {
            val objectNameList = path.split('.').toList
            val objectName = objectNameList.head
            val newExpectedObjectOption = reflectObject(objectName, expectedObject, getType(expectedObject, COMPLEX_TYPE)) //getObjectFromExpected(objectName, expectedObject)
            if (newExpectedObjectOption != None) {
              val newExpectedObject = newExpectedObjectOption.get
              loop(depth - 1, path.stripPrefix(objectName + DOT), newExpectedObject)
            }
          } else {
            expectedObject
          }
        }

        //	def checkIsomorphism entry point
        val expectedToReturnToRef = loop(depth, namePath, expectedObject)
        var depthLevels = 0

        for { path <- namePath.split('.').toList }
          depthLevels += path.size

        val expectedReturnsToRef = loop(depthLevels - depth, namePath.substring(2 * depth), expectedToReturnToRef)

        if (expectedToReturnToRef != expectedReturnsToRef) {
          report.addIsomorphicGraphExceptionMessage(prefixErrorMessage, depth)
        }
      }

      //  method assertGraph entrance 

      val checkedObjectsWithActualReference: Map[Any, Int] = checkedObjects ++ Map(actual -> getGraphDepth(namePath))
      val objectPropertiesOption = getFieldsForAssertFromObject(actual, namePath, getType(actual, COMPLEX_TYPE))

      if (objectPropertiesOption != None) {

        val objectProperties = objectPropertiesOption.get

        val (primitives, non_primitives) = objectProperties partition {
          p: (String, Any) => getValueType(p._2) == PRIMITIVE_TYPE
        }

        if (primitives nonEmpty) {
          assertPrimitiveProperties(0, primitives, expected)
        }

        non_primitives foreach {
          p =>
            getValueType(p._2) match {
              case SCALA_LIST_TYPE =>
                assertCollection(0, p._1, p._2, expected, SCALA_LIST_TYPE)
              case SCALA_MAP_TYPE =>
                assertCollection(0, p._1, p._2, expected, SCALA_MAP_TYPE)
              case ENTITY_TYPE =>
                report.addPropertiesExceptionMessage(prefixErrorMessage, p._1, p._2, "ENTITY_TYPE not implemented yet")
              case IGNORED_TYPE =>
              // TO-DO what?
              case COMPLEX_TYPE => {
                if (checkedObjectsWithActualReference.contains(p._2))
                  checkIsomorphism(checkedObjectsWithActualReference(p._2), p._1, expected)
                else
                  assertGraph(p._2, expected, p._1 + DOT, checkedObjectsWithActualReference, "")
              }
            }
        }

      }

    }

    lazy val report = new FabutReport()

    //def assertObjects entry point
    assertGraph(actualObject, expectedObject, "", Map(), "")

    if (uncheckedExpectedObjectProperties.nonEmpty)
      uncheckedExpectedObjectProperties foreach { p: (String, Any) =>
        report.addUnusedPropertyMessage(p._1, p._2)
      }

    report.result match {
      case ASSERT_SUCCESS => ()
      case _ => throw new AssertionError(report.message)
    }
  }

  def value(namePath: String, expectedValue: Any) = Property(namePath, expectedValue)

}

object Main extends App {

  val c = "Report:"
  println(c.size)
}

