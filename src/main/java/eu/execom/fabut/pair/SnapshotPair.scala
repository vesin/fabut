package eu.execom.fabut.pair

case class SnapshotPair(var asserted: Boolean, override val expected: Any, override val actual: Any) extends Pair {

}