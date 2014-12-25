package eu.execom.fabut.property

/**
 * Intermediate object that stores entity and info is the entity asserted or not.
 */
case class CopyAssert(var entity: Any, var asserted:Boolean = false)