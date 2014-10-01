package eu.execom.fabut

import scala.reflect.runtime.universe.{ Type, typeOf }
import eu.execom.fabut.FabutObjectAssert._
import eu.execom.fabut.util.ReflectionUtil
import javax.naming.directory.InvalidAttributesException
import eu.execom.fabut.model.ObjectWithComplexProperty
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.model.ObjectWithSimpleProperties
import org.junit.Test
import eu.execom.fabut.model.TrivialClasses._
import eu.execom.fabut.enums.AssertableType._

class FabutObjectAssertTest {

  var complexTypes: List[Type] = List()
  complexTypes ::= typeOf[ObjectWithSimpleProperties]
  complexTypes ::= typeOf[ObjectWithComplexProperty]
  complexTypes ::= typeOf[ObjectInsideSimpleProperty]
  complexTypes ::= typeOf[A]
  complexTypes ::= typeOf[B]
  complexTypes ::= typeOf[C]
  complexTypes ::= typeOf[D]
  complexTypes ::= typeOf[E]

  types(COMPLEX_TYPE) = complexTypes

  @Test
  def testAssertObjectWithSimpleProperties() = {

    //    setup
    val actualInsideSimple = ObjectInsideSimpleProperty("3301")
    val actual = ObjectWithSimpleProperties("pera", 43, actualInsideSimple)
    val expected = ObjectWithSimpleProperties("pera", 43, actualInsideSimple)

    //	  assert
    assertObjects(actual, expected)
  }

  @Test(expected = classOf[AssertionError])
  def testAssertObjectWithComplexPropertiesAndSimpleList() = {

    //    setup
    val actualInsideSimple = ObjectInsideSimpleProperty("3301")
    val expectedInsideSimple = ObjectInsideSimpleProperty("226")
    val actualSimpleObject = ObjectWithSimpleProperties("mika", 22, actualInsideSimple)
    val expectedSimpleObject = ObjectWithSimpleProperties("mika1", 22, expectedInsideSimple)
    val actual = ObjectWithComplexProperty(900, true, actualSimpleObject, List(1))
    val expected = ObjectWithComplexProperty(900, false, expectedSimpleObject, List(1))

    //    assert
    assertObjects(actual, expected)
  }

  @Test(expected = classOf[AssertionError])
  def testAssertObjectWithComplexPropertiesAndComplexList() = {

    //    setup
    val actualInsideSimple = ObjectInsideSimpleProperty("3301")
    val expectedInsideSimple = ObjectInsideSimpleProperty("3301")
    val a1 = ObjectInsideSimpleProperty("3301")
    val e1 = ObjectInsideSimpleProperty("3333")
    val a2 = ObjectInsideSimpleProperty("5000")
    val e2 = ObjectInsideSimpleProperty("5001")
    val actualSimpleObject = ObjectWithSimpleProperties("mika", 22, actualInsideSimple)
    val expectedSimpleObject = ObjectWithSimpleProperties("mika", 221, expectedInsideSimple)
    val actual = ObjectWithComplexProperty(900, true, actualSimpleObject, List(a1, a2, a1, actualInsideSimple))
    val expected = ObjectWithComplexProperty(900, true, expectedSimpleObject, List(e1, e2, e1, expectedInsideSimple))

    //    assert
    assertObjects(actual, expected)
  }

  @Test(expected = classOf[AssertionError])
  def testAssertNotIsomorphicGraphs() = {

    //setup 

    //actual
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

    //expected
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
    val e_c4 = new C(e_a3, "c2")

    e_b1.c = e_c1
    e_b2.c = e_c2
    e_b3.c = e_c3
    e_b4.c = e_c4

    //	assert
    assertObjects(a_a1, e_a1)
  }

  @Test
  def testWithRecursiveGraphWithTrivialObjects() = {

    //	setup
    val a_b = new B(null, "pera")
    val a_a = new A(a_b, "mika")
    val a_d = new D(null)
    val a_c = new C(a_d, "zelja")
    val a_e = new E(a_a)
    a_b.c = a_c
    a_d.e = a_e

    val e_b = new B(null, "pera")
    val e_a = new A(e_b, "mika")
    val e_d = new D(null)
    val e_c = new C(e_d, "zelja")
    val e_e = new E(e_a)
    e_b.c = e_c
    e_d.e = e_e

    //	assert
    assertObjects(a_a, e_a)
  }

  @Test
  def testAssertObjectsWithNullObjects() = {

    val a_b = new B(null, "pera")
    val a_a = new A(a_b, "mika")
    val a_c = new C(null, "zelja")
    a_b.c = a_c

    val e_b = new B(null, "pera")
    val e_a = new A(e_b, "mika")
    val e_c = new C(null, "zelja")
    e_b.c = e_c

    assertObjects(a_a, e_a)
  }

  @Test(expected = classOf[AssertionError])
  def testAssertObjectsWithNullProperties() = {

    val a_b = new B(null, null)
    val a_a = new A(a_b, "mika")
    val a_c = new C(a_a, "zelja")
    a_b.c = a_c

    val e_b = new B(null, "pera")
    val e_a = new A(e_b, "mika")
    val e_c = new C(e_a, null)
    e_b.c = e_c

    assertObjects(a_a, e_a)
  }

}