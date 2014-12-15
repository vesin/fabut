package eu.execom.fabut.enums

/**
 * Return result for node check.
 *
 */
class NodeCheckType

object NodeCheckType extends Enumeration {

  type NodeCheckType = Value

  /** The new pair. */
  val NEW_PAIR = Value

  /** The contains pair. */
  val CONTAINS_PAIR = Value

  /** The single node. */
  val SINGLE_NODE = Value
}