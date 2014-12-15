package eu.execom.fabut.enums

object AssertableType extends Enumeration {
  type AssertableType = Value

  val SCALA_LIST_TYPE, SCALA_MAP_TYPE, COMPLEX_TYPE, ENTITY_TYPE, IGNORED_TYPE, PRIMITIVE_TYPE = Value
}