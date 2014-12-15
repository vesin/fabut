package eu.execom.fabut.graph

import eu.execom.fabut.pair.Pair

case class IsomorphicNodePair(override val expected: AnyRef, override val actual: AnyRef) extends Pair {

  override def equals(objectInstance: Any) = {
    val that = objectInstance.asInstanceOf[IsomorphicNodePair]
    if ((this.expected eq that.expected) && (this.actual eq that.actual)) true else false
  }
}