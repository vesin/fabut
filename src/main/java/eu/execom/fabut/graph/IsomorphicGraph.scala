package eu.execom.fabut.graph

import eu.execom.fabut.pair.AssertPair
import eu.execom.fabut.enums.NodeCheckType

/**
 * Trait representing isomorphic object graphs structure.
 */
trait IsomorphicGraph {

  /**
   * Adds specified object nodes pair if it doesn't exist in object graph.
   *
   * @param actual
   *            - actual object
   * @param expected
   *            - expected object
   */
  def addPair(expected: AnyRef, actual: AnyRef)

  /**
   * Checks if object graph has specified object node pair. Ordering is important.
   *
   * @param actual
   *            - actual object
   * @param expected
   *            - expected object
   * @return - <code>true</code> if {@link IsomorphicGraph} contains specified pair
   */
  def containsPair(expected: AnyRef, actual: AnyRef): Boolean

  /**
   * Checks if object graph has specified expected object node pair. Ordering is important.
   *
   * @param expected
   *            - expected object
   * @return - <code>true</code> if {@link IsomorphicGraph} contains expected object, <code>false</code> otherwise
   */
  def containsExpected(expected: AnyRef): Boolean

  /**
   * Get expected object pair node for specified actual object node.Ordering is important.
   *
   * @param actual
   *            - object
   * @return - expected pair object
   */
  def expected(actual: AnyRef): Option[AnyRef]

  /**
   * For two specified objects check if any or both are contained in node list graph.
   *
   * @param expected
   *            the expected
   * @param actual
   *            the actual
   * @return - if graph contains object pair return {@link NodeCheckType}.CONTAINS_PAIR, if and only if one of the
   *         nodes is contained in nodes list return {@link NodeCheckType}.SINGLE_NODE, otherwise it is new pair and
   *         return {@link NodeCheckType}.NEW_PAIR.
   */
  def nodeCheck(pair: AssertPair): NodeCheckType.Value

  //  def getActual(expected: Any): Option[Any]
  //  def containsActual(actual: Any): Boolean

}