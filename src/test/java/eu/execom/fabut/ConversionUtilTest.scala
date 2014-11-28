package eu.execom.fabut

import org.junit.Assert
import org.junit.Test
import org.junit.Assert._
import eu.execom.fabut.util.ConversionUtil._
import eu.execom.fabut.enums.AssertType

class ConversionUtilTest extends Assert {

  @Test
  def testAssertTypeUnsupportedAssert {
    //    setup
    val testInstance = new Object
    //    method
    val assertType = getAssertType(testInstance)
    //    assert
    assertEquals(AssertType.UNSUPPORTED_ASSERT, assertType)
  }

  @Test
  def testAssertTypeRepositoryAssert {
    //    setup
    val testInstance = new AbstractFabutRepositoryAssertTest
    //    method
    val assertType = getAssertType(testInstance)
    //    assert
    assertEquals(AssertType.REPOSITORY_ASSERT, assertType)
  }

  @Test
  def testAssertTypeObjectAssert {
    //    setup
    val testInstance = new AbstractFabutObjectAssertTest
    //    method
    val assertType = getAssertType(testInstance)
    //    assert
    assertEquals(AssertType.OBJECT_ASSERT, assertType)
  }
}