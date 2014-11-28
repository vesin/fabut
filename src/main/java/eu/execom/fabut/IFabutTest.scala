package eu.execom.fabut
import scala.reflect.runtime.universe.{ Type, typeOf }

trait IFabutTest {

  def fabutBeforeTest
  def fabutAfterTest

  def getComplexTypes(): List[Type]
  def getIgnoredTypes(): List[Type]

  def customAssertEquals(expectedObject: Any, actualObject: Any)
}