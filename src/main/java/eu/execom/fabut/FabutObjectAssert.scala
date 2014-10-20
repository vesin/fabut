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
 * not implemented => assert info when List[ComplexTyped] fails assert
 */

class FabutObjectAssert

object FabutObjectAssert {

  lazy val DOT: String = "."

  var types: MutableMap[AssertableType, List[Type]] = MutableMap()
  types(COMPLEX_TYPE) = List()
  types(ENTITY_TYPE) = List()
  types(IGNORED_TYPE) = List()

  /**
   * Asserts 2 objects.
   *
   * @param actual
   *            actual object
   * @param object
   *            expected object
   */
  def assertObjects(actual: Any, expected: Any) {

    /**
     *  Gets properties from actual object, asserts each actual property
     *   with corresponding in expected object, and for each object as property recursively calls
     *   itself to assert it
     *
     *   @param actual
     *   	the actual object
     *   @param expected
     *   	the expected object
     *   @param namePath
     *   	symbolic path of actual object, used to check if properties
     *      and objects are on same position in expected
     *   @checkObjects
     *   	Map that has key as object reference that we've already asserted and value as
     *    	numeric position in actual objects chain
     */
    def assertGraph(actual: Any, expected: Any, namePath: String, checkedObjects: Map[Any, Int]) {

      /**
       * Returns a type of given value
       *
       * @param value
       * @return
       * 		one of assertable type
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
       *  @param value
       *  @param t
       *  	the list from map of types where we should search for value
       *  @return
       *  	the specific object type
       */
      def getType(value: Any, t: AssertableType): Option[Type] = {

        // suspicious, repair?!
        if (value == null)
          return None

        types(t).find(v => (v.toString == value.getClass.getCanonicalName)) match {
          case n: Some[Type] => Some(n.get)
          case _ => None
        }
      }

      /**
       *  Returns the depth of entering inside objects, used for
       *  checking if actual and expected objects are isomorphic
       */
      def getDepth(path: String): Int =
        if (path == "") {
          0
        } else {
          (path.split('.').size)
        }

      /**
       *  Asserts primitive types of given object with expected corresponding
       *  values inside expected object
       *
       *  @param pathcut
       *  	the number of characters to be cut from full primitives name
       *      as we go deeper inside expected object to assert its primitive property
       *  @param primitives
       *  	primitive types with name and value to be asserted
       *  @param expectedObject
       *
       *
       */
      def assertPrimitives(pathcut: Int, primitives: Map[String, Any], expectedObject: Any) {

        val (key, value) = primitives.head
        val depth = key.substring(pathcut).split('.')

        if (depth.size == 1) {
          reflectPrimitives(pathcut, primitives, expectedObject, getType(expectedObject, COMPLEX_TYPE), report)
        } else {
          val objectName = depth.head
          val newExpectedObjectOption = reflectObject(objectName, expectedObject, getType(expectedObject, COMPLEX_TYPE))
          if (newExpectedObjectOption != None) {
            assertPrimitives(pathcut + objectName.size + 1, primitives, newExpectedObjectOption.get)
          }

        }

      }

      /**
       * Asserts lists of 2 objects
       *
       * @param pathcut
       * @param path
       * @param actualObject
       * @param expectedObject
       */
      def assertLists(pathcut: Int, path: String, actualObject: Any, expectedObject: Any) {

        val depth = path.substring(pathcut).split('.')

        if (depth.size == 1) {
          val actualList = actualObject.asInstanceOf[List[_]]

          val expectedListOption = reflectObject(path, expectedObject, getType(expectedObject, COMPLEX_TYPE))
          if (expectedListOption != None) {
            val expectedList = expectedListOption.get.asInstanceOf[List[_]]
            if (actualList.size != expectedList.size) {
              report.addResult(ASSERT_FAILED)
              report.addListSizeExceptionMessage(path, actualList.size, expectedList.size)
            } else if (actualList.size > 0) {
              val propertyType = getType(actualList.head, COMPLEX_TYPE)
              assertListProperties(0, path, actualList, expectedList, propertyType)
            } else {
              // TO-DO lists are empty
            }
          }

        } else {
          val objectName = depth.head
          val newExpectedObjectOption = reflectObject(path, expectedObject, getType(expectedObject, COMPLEX_TYPE))
          if (newExpectedObjectOption != None) {
            val newExpectedObject = newExpectedObjectOption.get
            assertLists(pathcut + objectName.size + 1, path, actualObject, newExpectedObject)
          }

        }
      }

      /**
       *  Helper class for asserting lists, goes through each object
       *   in actual list and asserts with corresponding in expected list
       *  @param position
       *  	actual position of objects we are asserting in list
       *  @param path
       *   	full name in object chain
       *  @param actualList
       *  	list in actual object
       *  @param expectedList
       *   	list in expected object
       *  @param isComplexType
       *  	if type exists in types complex list, if not null
       */
      def assertListProperties(position: Int, path: String, actualList: List[_], expectedList: List[_], isComplexType: Option[Type]) {
        actualList match {
          case head :: tail => {
            if (isComplexType == null)
              assertPrimitiveListProperty(position, actualList.head, expectedList.head)
            else
              assertGraph(actualList.head, expectedList.head, "", Map())

            assertListProperties(position + 1, path, actualList.tail, expectedList.tail, isComplexType)
          }
          case Nil => ()
        }

        def assertPrimitiveListProperty(position: Int, actualProperty: Any, expectedProperty: Any) {
          if (actualProperty != expectedProperty) {
            report.addResult(ASSERT_FAILED)
            report.addListPropertyException(position, actualProperty, expectedProperty)
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
       *  	if chain of objects is ABCD and returns to B => 1234 returns to node 2,
       *   	depth is the returning to node number
       *  @param path
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
      def checkIsomorphism(depth: Int, path: String, expectedObject: Any) {

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

        //def checkIsomorphism entry point
        val expectedToReturnToRef = loop(depth, path, expectedObject)

        var depthLevels = 0

        for { path <- path.split('.').toList }
          depthLevels += path.size

        val expectedReturnsToRef = loop(depthLevels - depth, path.substring(2 * depth), expectedToReturnToRef)

        if (expectedToReturnToRef != expectedReturnsToRef) {
          report.addResult(ASSERT_FAILED)
          report.addIsomorphicGraphExceptionMessage(depth)
        }
      }

      //  def assertGraph intro 

      val checkedObjectsWithActual: Map[Any, Int] = checkedObjects ++ Map(actual -> getDepth(namePath))
      val objectPropertiesOption = getFieldsForAssertFromObject(actual, namePath, getType(actual, COMPLEX_TYPE))
      if (objectPropertiesOption != None) {

        val objectProperties = objectPropertiesOption.get

        val (primitives, non_primitives) = objectProperties partition {
          p: (String, Any) => getValueType(p._2) == PRIMITIVE_TYPE
        }

        if (primitives nonEmpty)
          assertPrimitives(0, primitives, expected)

        non_primitives foreach {
          p =>
            getValueType(p._2) match {
              case SCALA_LIST_TYPE =>
                assertLists(0, namePath + p._1, p._2, expected)
              case SCALA_MAP_TYPE => // TO-DO implement map assertation
                report.addPropertiesExceptionMessage(p._1, p._2.toString, "SCALA_MAP_TYPE not implemented yet")
              case ENTITY_TYPE =>
                report.addPropertiesExceptionMessage(p._1, p._2.toString, "ENTITY_TYPE not implemented yet")
              case IGNORED_TYPE =>
              // TO-DO what?
              case COMPLEX_TYPE => {
                if (checkedObjectsWithActual.contains(p._2))
                  checkIsomorphism(checkedObjectsWithActual(p._2), p._1, expected)
                else
                  assertGraph(p._2, expected, p._1 + DOT, checkedObjectsWithActual)
              }
            }
        }

      }

    }

    lazy val report = new FabutReport()

    //def assertObjects entry point
    assertGraph(actual, expected, "", Map())

    report.result match {
      case ASSERT_SUCCESS => ()
      case _ => throw new AssertionError(report.message)
    }
  }
}

object Main extends App {
}

