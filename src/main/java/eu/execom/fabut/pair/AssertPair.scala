package eu.execom.fabut.pair

import eu.execom.fabut.enums.AssertableType

/**
 * Represents expected & actual pair during asserting with
 * information of what type is the pair and if expected/actual
 * are fields of some already asserted object.
 */
case class AssertPair(path: String, expected: Any, actual: Any, assertableType: AssertableType.Value, property: Boolean) extends Pair {

  def isProperty: Boolean = property
}

