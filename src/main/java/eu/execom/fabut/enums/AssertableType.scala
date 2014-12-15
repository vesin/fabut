package eu.execom.fabut.enums

/**
 * Types assertable by FABUT
 */
class AssertableType

object AssertableType extends Enumeration {

  type AssertableType = Value

  /** Type that implements {@link List}, its asserted iterating over its elements and asserting those. */
  val SCALA_LIST_TYPE = Value

  /** Type that implements {@link Map} */
  val SCALA_MAP_TYPE = Value

  /** Type that can be split by sub fields and asserted by those fields. */
  val COMPLEX_TYPE = Value

  /**
   * Type used for storing in external repositories, main difference to {@link AssertableType#COMPLEX_TYPE} is that
   * this type requires id field.
   */
  val ENTITY_TYPE = Value

  /** Type that should be ignored during Fabut assert. */
  val IGNORED_TYPE = Value

  /** Type asserted using custom user assert. */
  val PRIMITIVE_TYPE = Value
}