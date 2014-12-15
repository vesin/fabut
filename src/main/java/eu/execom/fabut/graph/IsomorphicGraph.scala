package eu.execom.fabut.graph

import eu.execom.fabut.enums.NodeCheckType
import eu.execom.fabut.enums.NodeCheckType._
import eu.execom.fabut.pair.{AssertPair, Pair}

import scala.collection.mutable.ListBuffer

/**
 * Trait representing isomorphic object graphs structure.
 */
trait IsomorphicGraph {

  /**
   * Adds specified object nodes pair if it doesn't exist in object graph.
   *
   * @param actual
   * - actual object
   * @param expected
   * - expected object
   */
  def addPair(expected: AnyRef, actual: AnyRef)

  /**
   * Checks if object graph has specified object node pair. Ordering is important.
   *
   * @param actual
   * - actual object
   * @param expected
   * - expected object
   * @return - <code>true</code> if { @link IsomorphicGraph} contains specified pair
   */
  def containsPair(expected: AnyRef, actual: AnyRef): Boolean

  /**
   * Checks if object graph has specified expected object node pair. Ordering is important.
   *
   * @param expected
   * - expected object
   * @return - <code>true</code> if { @link IsomorphicGraph} contains expected object, <code>false</code> otherwise
   */
  def containsExpected(expected: AnyRef): Boolean

  /**
   * Get expected object pair node for specified actual object node.Ordering is important.
   *
   * @param actual
   * - object
   * @return - expected pair object
   */
  def expected(actual: AnyRef): Option[AnyRef]

  //TODO update documentation
  /**
   * For two specified objects check if any or both are contained in node list graph.
   *
   * @param expected
   * the expected
   * @param actual
   * the actual
   * @return - if graph contains object pair return { @link NodeCheckType}.CONTAINS_PAIR, if and only if one of the
   *                                                        nodes is contained in nodes list return { @link NodeCheckType}.SINGLE_NODE, otherwise it is new pair and
   *                                                                                                        return { @link NodeCheckType}.NEW_PAIR.
   */
  def nodeCheck(pair: AssertPair): NodeCheckType.Value

}

case class IsomorphicNodePair(expected: AnyRef, actual: AnyRef) extends Pair

class NodesList extends IsomorphicGraph {

  val isomorphicNodes: ListBuffer[IsomorphicNodePair] = new ListBuffer[IsomorphicNodePair]

  override def addPair(expected: AnyRef, actual: AnyRef) =
    isomorphicNodes += IsomorphicNodePair(expected, actual)


  override def containsPair(expected: AnyRef, actual: AnyRef) =
    isomorphicNodes.contains(IsomorphicNodePair(expected, actual))


  override def containsExpected(expected: AnyRef) =
    isomorphicNodes.exists(pair => pair.expected.eq(expected))


  override def expected(actual: AnyRef): Option[AnyRef] =
    isomorphicNodes.find(pair => pair.actual.eq(actual)).map(_.expected)


  override def nodeCheck(pair: AssertPair): NodeCheckType =
    if (containsExpected(pair.expected.asInstanceOf[AnyRef])) {
      CONTAINS_PAIR
    } else {
      NEW_PAIR
    }

}