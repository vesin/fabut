package eu.execom.fabut
import scala.reflect.runtime.universe.{ Type, typeOf }

trait IFabutTest {

  def fabutBeforeTest
  def fabutAfterTest

  def complexTypes: List[Type]
  def ignoredTypes: List[Type]

  def customAssertEquals(expectedObject: Any, actualObject: Any)
}