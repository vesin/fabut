package eu.execom.fabut

//TODO move to enum files
package object enums {
  class AssertableType
  object AssertableType extends Enumeration {

    type AssertableType = Value
    val SCALA_LIST_TYPE, SCALA_MAP_TYPE, COMPLEX_TYPE, ENTITY_TYPE, IGNORED_TYPE, PRIMITIVE_TYPE = Value
  }

  class AssertType
  object AssertType extends Enumeration {

    type AssertType = Value
    val REPOSITORY_ASSERT, OBJECT_ASSERT, UNSUPPORTED_ASSERT = Value
  }

  class NodeCheckType
  object NodeCheckType extends Enumeration {

    type NodeCheckType = Value
    val NEW_PAIR, CONTAINS_PAIR, SINGLE_NODE = Value
  }

}