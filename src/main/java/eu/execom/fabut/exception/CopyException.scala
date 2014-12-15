package eu.execom.fabut.exception

/**
 * Exception thrown when object copy fails.
 */
case class CopyException(msg: String) extends Exception(msg) {
}