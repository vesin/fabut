package eu.execom.fabut.util

import eu.execom.fabut.util.ConversionUtil._
import eu.execom.fabut.{AbstractFabutObjectAssertTest, AbstractFabutRepositoryAssertTest, AssertType}
import org.junit.Assert._
import org.junit.{Assert, Test}

class ConversionUtilTest extends Assert {

  @Test
  def testAssertTypeUnsupportedAssert(): Unit = {
    //    setup
    val testInstance = new Object
    //    method
    val assertType = getAssertType(testInstance)
    //    assert
    assertEquals(AssertType.UNSUPPORTED_ASSERT, assertType)
  }

  @Test
  def testAssertTypeRepositoryAssert(): Unit = {
    //    setup
    val testInstance = new AbstractFabutRepositoryAssertTest
    //    method
    val assertType = getAssertType(testInstance)
    //    assert
    assertEquals(AssertType.REPOSITORY_ASSERT, assertType)
  }

  @Test
  def testAssertTypeObjectAssert(): Unit = {
    //    setup
    val testInstance = new AbstractFabutObjectAssertTest
    //    method
    val assertType = getAssertType(testInstance)
    //    assert
    assertEquals(AssertType.OBJECT_ASSERT, assertType)
  }
}