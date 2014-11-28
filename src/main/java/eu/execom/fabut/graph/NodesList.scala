package eu.execom.fabut.graph

import eu.execom.fabut.pair.AssertPair
import eu.execom.fabut.enums.NodeCheckType._

class NodesList extends IsomorphicGraph {

  var isomorphicNodes: List[IsomorphicNodePair] = List()

  override def addPair(expected: Any, actual: Any) {
    isomorphicNodes ::= IsomorphicNodePair(expected, actual)
  }

  override def containsPair(expected: Any, actual: Any) = {
    isomorphicNodes contains (IsomorphicNodePair(expected, actual))
  }

  override def containsExpected(expected: Any) = {
    val pair = isomorphicNodes find (pair => pair.expected == expected)

    if (pair.isDefined) true else false
  }

  //  override def containsActual(actual: Any) = {
  //    val pair = isomorphicNodes find (pair => pair.actual == actual)
  //
  //    if (pair.isDefined) true else false
  //  }

  override def getExpected(actual: Any): Option[Any] = {
    val pair = isomorphicNodes find (pair => pair.actual == actual)

    if (pair.isDefined) Some(pair.get.expected) else None
  }

  //  override def getActual(expected: Any): Option[Any] = {
  //    val pair = isomorphicNodes find (pair => pair.expected == expected)
  //
  //    if (pair.isDefined) Some(pair.get.actual) else None
  //  }

  override def nodeCheck(pair: AssertPair): NodeCheckType = {

    if (containsExpected(pair.expected))
      CONTAINS_PAIR
    else
      NEW_PAIR
  }

}