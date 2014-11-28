package eu.execom.fabut.graph

import eu.execom.fabut.pair.Pair

case class IsomorphicNodePair(override val expected: Any, override val actual: Any) extends Pair {

  override def equals(objectInstance: Any) = {
    val that = objectInstance.asInstanceOf[IsomorphicNodePair]
    if (this.expected == that.expected && this.actual == that.actual) true else false
  }
}