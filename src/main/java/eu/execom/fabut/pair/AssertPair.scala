package eu.execom.fabut.pair

import eu.execom.fabut.enums.AssertableType

case class AssertPair(path: String, actual: Any, expected: Any, objectType: AssertableType.Value)

