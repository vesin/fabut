package eu.execom.fabut.pair

/**
 * Represents the pair expected/actual during snapshot assert with information if pair has been already asserted.
 */
case class SnapshotPair(var asserted: Boolean, override val expected: Any, override val actual: Any) extends Pair {

}