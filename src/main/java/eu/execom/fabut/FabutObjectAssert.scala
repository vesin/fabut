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

  def assertObjects(actual: Any, expected: Any, report: FabutReport): Unit = {

    def assertGraph(
      actual: Any,
      expected: Any,
      pathName: String,
      cNodes: Map[Any, Int]): Unit =
      {

        val checkedNodes: Map[Any, Int] = Map(actual -> getDepth(pathName)) ++ cNodes
        val nodeValues = getNameValueProperties(actual, pathName, getComplexType(actual))

        val (primitives, non_primitives) = nodeValues partition {
          p: (String, Any) => getValueType(p._2) == PRIMITIVE_TYPE
        }

        if (primitives nonEmpty)
          assertPrimitives(0, primitives, expected)

        non_primitives foreach {
          p =>
            getValueType(p._2) match {
              case SCALA_LIST_TYPE => report.addPropertiesExceptionMessage(p._1, p._2.toString, "SCALA_MAP_TYPE not implemented yet")
              case SCALA_MAP_TYPE => report.addPropertiesExceptionMessage(p._1, p._2.toString, "SCALA_MAP_TYPE not implemented yet")
              case COMPLEX_TYPE => {
                if (checkedNodes.contains(p._2))
                  checkIsomorphism(checkedNodes(p._2), p._1, expected)
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
    def getNameValueProperties(actual: Any, path: String, t: Type) =
      ReflectionUtil.getFieldsForAssertFromObject(actual, path, t)

    /* ATM not in use
     * 
     def reflectFieldOfExpected(fieldName: String, actualValue: Any, expectedObject: Any): Any = {

      val expectedValue =
        ReflectionUtil.getFieldValueFromGetter(fieldName, expectedObject, getComplexType(expectedObject))
      
      if (actualValue == expectedValue)
        null
      else
        expectedValue
    }
    */

    /**
     * Reflects expected object and returns a object that we are looking for inside it
     */
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
    def getDepth(path: String): Int =
      if (path == "") 0 else (path.split('.').size)

    /**
     *  Asserts primitive types of given object with expected corresponding values
     *  inside expected object
     */
    def assertPrimitives(pathcut: Int, primitives: Map[String, Any], expectedObject: Any) {

      val (key, value) = primitives.head
      val depth = key.substring(pathcut).split('.')

      if (depth.size == 1) {
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
        assertPrimitives(pathcut + objectName.size + 1, primitives, newExpectedObject)
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
    def checkIsomorphism(depth: Int, path: String, expectedObject: Any) {

      def loop(depth: Int, path: String, expectedObject: Any): Any = {
        if (depth > 0) {
          val objectNameList = path.split('.').toList
          val objectName = objectNameList.head
          val newExpectedObject = reflectObjectOfExpected(objectName, expectedObject)
          loop(depth - 1, path.stripPrefix(objectName + DOT), newExpectedObject)
        } else {
          expectedObject
        }
      }

      //def checkIsomorphism entry point
      val recRef = loop(depth, path, expectedObject)

      var depthLevels = 0

      path.split('.').toList.foreach { path => depthLevels += path.size }

      val lastRef = loop(depthLevels - depth, path.substring(2 * depth), recRef)

      if (recRef != lastRef) {
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

  val r = new FabutReport

  r.addPropertiesExceptionMessage("student.faculty.departman.name", "pera", "mica")
  r.addPropertiesExceptionMessage("student.faculty.id", "3311", "315")
  println(r.message)

  println("b.".split('.').size)
  println("b.c.".split('.').size)
  println("b.c.d.".split('.').size)

  println("peracar".stripPrefix("pera"))

}

