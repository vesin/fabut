package eu.execom.fabut
import scala.reflect.runtime.universe.Type

// TODO add comments
trait IFabutTest {

  def fabutBeforeTest(): Unit
  def fabutAfterTest(): Unit

  def complexTypes(): List[Type]
  def ignoredTypes(): List[Type]

  def customAssertEquals(expectedObject: Any, actualObject: Any)
}