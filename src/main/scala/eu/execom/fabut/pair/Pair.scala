package eu.execom.fabut.pair

/**
 * Represents a pair of expected and actual object that needs to be asserted
 **/
trait Pair {
  def expected: Any

  def actual: Any
}