package eu.execom.fabut.enums

/**
 * Assert type.
 */
class AssertType

object AssertType extends Enumeration {

  type AssertType = Value

  /**
   * Using repository functionality.
   */
  val REPOSITORY_ASSERT = Value

  /**
   * Using regular object assert.
   */
  val OBJECT_ASSERT = Value

  /**
   * When test does not meet necessary prerequisites.
   */
  val UNSUPPORTED_ASSERT = Value
}