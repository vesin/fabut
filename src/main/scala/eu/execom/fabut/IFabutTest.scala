package eu.execom.fabut

import scala.reflect.runtime.universe.Type

// TODO add comments
trait IFabutTest {

  def beforeTest(): Unit

  def afterTest(): Unit

  /**
   * List of class types that will be treated as complex
   *
   * @return list of complex class types
   **/
  def complexTypes(): List[Type]

  /**
   * List of class types that Fabut will ignore while asserting
   *
   * @return list of ignored class types
   **/
  def ignoredTypes(): List[Type]

  /**
   * Custom implementation of assert function for certain class types
   **/
  def customAssertEquals(expectedObject: Any, actualObject: Any)
}