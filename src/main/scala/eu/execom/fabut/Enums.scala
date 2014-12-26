package eu.execom.fabut


/**
 * Types assertable by Fabut.
 **/
object AssertableType extends Enumeration {
  type AssertableType = Value

  val PRIMITIVE_TYPE, IGNORED_TYPE, ENTITY_TYPE, COMPLEX_TYPE, SCALA_LIST_TYPE, SCALA_MAP_TYPE = Value
}

object AssertType extends Enumeration {
  type AssertType = Value

  val REPOSITORY_ASSERT, OBJECT_ASSERT, UNSUPPORTED_ASSERT = Value
}

object CommentType extends Enumeration {
  type CommentType = Value

  //TODO consider using other signs.
  val FAIL = Value("x")
  val SUCCESS = Value("✓")
  val COLLECTION = Value("#")
}

/**
 * Types used to differentiate if node is already in list
 **/
object NodeCheckType extends Enumeration {
  type NodeCheckType = Value

  val NEW_PAIR, CONTAINS_PAIR = Value
}
