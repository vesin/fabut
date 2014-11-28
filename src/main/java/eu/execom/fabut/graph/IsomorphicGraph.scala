package eu.execom.fabut.graph

import eu.execom.fabut.pair.AssertPair
import eu.execom.fabut.enums.NodeCheckType

trait IsomorphicGraph {

  def addPair(expected: Any, actual: Any)

  def containsPair(expected: Any, actual: Any): Boolean

  def containsExpected(expected: Any): Boolean

  //  def containsActual(actual: Any): Boolean
  //
  def getExpected(actual: Any): Option[Any]
  //
  //  def getActual(expected: Any): Option[Any]

  def nodeCheck(pair: AssertPair): NodeCheckType.Value

}