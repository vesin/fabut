package eu.execom.fabut

import scala.collection.immutable.Map
import scala.reflect.runtime.universe.{ Type, TypeTag, typeOf, Symbol }

import eu.execom.fabut.enums.AssertType._
import eu.execom.fabut.enums.AssertableType._
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.model.ObjectWithComplexProperty
import eu.execom.fabut.model.ObjectWithSimpleProperties
import eu.execom.fabut.util.ReflectionUtil
import eu.execom.fabut.model.TrivialClasses._

class FabutObjectAssert
//TODO move all algorithm functions to top
object FabutObjectAssert {

	var complexTypes: List[Type] = List()
	complexTypes ::= typeOf[ObjectWithSimpleProperties]
	complexTypes ::= typeOf[ObjectWithComplexProperty]
	complexTypes ::= typeOf[ObjectInsideSimpleProperty]
	complexTypes ::= typeOf[A]
	complexTypes ::= typeOf[B]
	complexTypes ::= typeOf[C]
	complexTypes ::= typeOf[D]
	complexTypes ::= typeOf[E]

	lazy val DOT: String = "."

	def assertObjects(actual: Any, expected: Any) {

		val report = new FabutReport()

		//		TODO function declarations should be in one line
		def assertGraph(
			actual: Any,
			expected: Any,
			pathName: String,
			cNodes: Map[Any, Int]) {

			val checkedNodes: Map[Any, Int] = Map(actual -> getDepth(pathName)) ++ cNodes
			val nodeValues = getNameValueProperties(actual, pathName, getComplexType(actual))

			val (primitives, non_primitives) = nodeValues partition {
				p: (String, Any) => getValueType(p._2) == PRIMITIVE_TYPE
			}

			if (primitives nonEmpty)
				assertPrimitives(0, primitives, expected, report)

			non_primitives foreach {
				p =>
					getValueType(p._2) match {
						case SCALA_LIST_TYPE =>
							assertList(0, pathName + p._1, p._2, expected)
						case SCALA_MAP_TYPE => // TO-DO implement map assertation
							report.addPropertiesExceptionMessage(p._1, p._2.toString, "SCALA_MAP_TYPE not implemented yet")
						case COMPLEX_TYPE => {
							if (checkedNodes.contains(p._2))
								checkIsomorphism(checkedNodes(p._2), p._1, expected, report)
							else
								assertGraph(p._2, expected, p._1 + DOT, checkedNodes)
						}
					}
			}

		}

		/**
		 *  Method returns all properties of given object in a map which
		 *  has a key: value name,  and as value: property value
		 */
		//		TODO no need to route calls, use static import and call the method instead
		def getNameValueProperties(actual: Any, path: String, t: Type) =
			ReflectionUtil.getFieldsForAssertFromObject(actual, path, t)

		/**
		 * Reflects expected object and returns a object that we are looking for inside it
		 */
		//		TODO no need to route calls, use static import and call the method insteads
		def reflectObjectOfExpected(objectName: String, expectedObject: Any): Any =
			ReflectionUtil.reflectObject(objectName, expectedObject, getComplexType(expectedObject))

		/**
		 * Returns a type of given value
		 */
		def getValueType(value: Any) = {

			if (value.isInstanceOf[List[_]])
				SCALA_LIST_TYPE
			else if (value.isInstanceOf[Map[_, _]])
				SCALA_MAP_TYPE
			//				TODO remove nulls
			else if (getComplexType(value) != null)
				COMPLEX_TYPE
			else
				PRIMITIVE_TYPE
		}

		/**
		 * Returns a type of complex object used for reflection
		 */
		def getComplexType(value: Any): Type = {

			// suspicious, repair?!
			if (value == null)
				return null

			complexTypes.find(v => (v.toString == value.getClass.getCanonicalName)) match {
				case n: Some[Type] => n.get
				case _ => null
			}
		}

		/**
		 *  Returns the depth of entering inside objects, used for
		 *  checking if actual and expected objects are isomorphic
		 */
		//		TODO use good old if/else statement with brackets :)
		def getDepth(path: String): Int =
			if (path == "") 0 else (path.split('.').size)

		/**
		 *  Asserts primitive types of given object with expected corresponding values
		 *  inside expected object
		 */
		def assertPrimitives(pathcut: Int, primitives: Map[String, Any], expectedObject: Any, report: FabutReport) {

			val (key, value) = primitives.head
			//			TODO use string instead of char
			val depth = key.substring(pathcut).split('.')

			if (depth.size == 1) {
				//				TODO call in one line
				ReflectionUtil
					.reflectPrimitives(
						pathcut,
						primitives,
						expectedObject,
						getComplexType(expectedObject),
						report)

			} else {
				val objectName = depth.head
				val newExpectedObject = reflectObjectOfExpected(objectName, expectedObject)
				assertPrimitives(pathcut + objectName.size + 1, primitives, newExpectedObject, report)
			}

		}

		def assertList(pathcut: Int, path: String, actualObject: Any, expectedObject: Any) {

			val depth = path.substring(pathcut).split('.')

			if (depth.size == 1) {
				val actualList = actualObject.asInstanceOf[List[_]]
				val expectedList = reflectObjectOfExpected(path, expectedObject).asInstanceOf[List[_]]
				if (actualList.size != expectedList.size) {
					report.addResult(ASSERT_FAILED)
					report.addListSizeExceptionMessage(path, actualList.size, expectedList.size)
				} else if (actualList.size > 0) {
					val propertyType = getComplexType(actualList.head)
					assertListProperties(0, path, actualList, expectedList, propertyType)
				} else {
					// TO-DO lists are empty
				}
			} else {
				val objectName = depth.head
				val newExpectedObject = reflectObjectOfExpected(objectName, expectedObject)
				assertList(pathcut + objectName.size + 1, path, actualObject, newExpectedObject)
			}
		}

		def assertListProperties(position: Int, path: String, actualList: List[_], expectedList: List[_], isComplexType: Type) {
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
		 *  The way graph isomorphic algo works is that we pass the depth of actual object in
		 *  which recursion returns, e.g. if chain is ABCDB, chain is simplified as 1232 and
		 *  depth is 2, it returns to 2nd node(object), now we reflect expected object until that depth
		 *  we keep the reference of expected object in which it should return, we continue reflecting
		 *  until recursion and theh we compare those 2 references, if equal, graphs are isomorphic,
		 *  they both return to the same node, else, isomorphic error message is added
		 */
		def checkIsomorphism(depth: Int, path: String, expectedObject: Any, report: FabutReport) {

			def loop(depth: Int, path: String, expectedObject: Any, report: FabutReport): Any = {
				if (depth > 0) {
					val objectNameList = path.split('.').toList
					val objectName = objectNameList.head
					val newExpectedObject = reflectObjectOfExpected(objectName, expectedObject)
					loop(depth - 1, path.stripPrefix(objectName + DOT), newExpectedObject, report)
				} else {
					expectedObject
				}
			}

			//def checkIsomorphism entry point
			val expectedToReturnToRef = loop(depth, path, expectedObject, report)

			var depthLevels = 0

			for { path <- path.split('.').toList }
				depthLevels += path.size

			val expectedReturnsToRef = loop(depthLevels - depth, path.substring(2 * depth), expectedToReturnToRef, report)

			if (expectedToReturnToRef != expectedReturnsToRef) {
				report.addResult(ASSERT_FAILED)
				report.addIsomorphicGraphExceptionMessage(depth)
			}
		}

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

