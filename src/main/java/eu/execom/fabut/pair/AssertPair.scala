package eu.execom.fabut.pair

import eu.execom.fabut.enums.AssertableType

case class AssertPair(path: String, override val expected: Any, override val actual: Any, assertableType: AssertableType.Value, property: Boolean) extends Pair {

  def isProperty: Boolean = property
}

