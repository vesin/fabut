package eu.execom.fabut.graph

import eu.execom.fabut.NodeCheckType._
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
  def addPair(expected: Any, actual: Any)

  /**
   * Checks if object graph has specified object node pair. Ordering is important.
   *
   * @param actual
   * - actual object
   * @param expected
   * - expected object
   * @return - <code>true</code> if { @link IsomorphicGraph} contains specified pair
   */
  def containsPair(expected: Any, actual: Any): Boolean

  /**
   * Checks if object graph has specified expected object node pair. Ordering is important.
   *
   * @param expected
   * - expected object
   * @return - <code> true </code> if `IsomorphicGraph` contains expected object, <code> false </code> otherwise
   */
  def containsExpected(expected: Any): Boolean

  /**
   * Get expected object pair node for specified actual object node.Ordering is important.
   *
   * @param actual
   * - the object
   * @return - expected pair object
   */
  def expected(actual: Any): Option[AnyRef]

  /**
   * For two specified objects pair check if any or both are contained in node list graph.
   *
   * @param pair
   * the pair of expected and actual objects
   * @return - if graph contains object pair return   `NodeCheckType`.CONTAINS_PAIR,
   *         if and only if one of the nodes is contained in nodes list return `NodeCheckType`.SINGLE_NODE,
   *         otherwise it is new pair and
   */
  def nodeCheck(pair: AssertPair): NodeCheckType

}

/**
 * Class representing object pair from `IsomorphicGraph`
 **/
case class IsomorphicNodePair(expected: AnyRef, actual: AnyRef) extends Pair

/**
 * Class implementation of `IsomorphicGraph`
 **/
class NodesList extends IsomorphicGraph {

  val isomorphicNodes: ListBuffer[IsomorphicNodePair] = new ListBuffer[IsomorphicNodePair]

  override def addPair(expected: Any, actual: Any) = isomorphicNodes += IsomorphicNodePair(expected.asInstanceOf[AnyRef], actual.asInstanceOf[AnyRef])

  override def containsPair(expected: Any, actual: Any) = isomorphicNodes.contains(IsomorphicNodePair(expected.asInstanceOf[AnyRef], actual.asInstanceOf[AnyRef]))

  override def expected(actual: Any): Option[AnyRef] = isomorphicNodes.find(pair => pair.actual.eq(actual.asInstanceOf[AnyRef])).map(_.expected)

  override def nodeCheck(pair: AssertPair): NodeCheckType =
    if (containsExpected(pair.expected)) {
      CONTAINS_PAIR
    } else {
      NEW_PAIR
    }

  override def containsExpected(expected: Any) = isomorphicNodes.exists(pair => pair.expected.eq(expected.asInstanceOf[AnyRef]))
}