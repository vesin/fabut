package eu.execom.fabut.exception

/**
 * Exception thrown when object copy fails.
 */
case class CopyException(message: String) extends Exception(message)
