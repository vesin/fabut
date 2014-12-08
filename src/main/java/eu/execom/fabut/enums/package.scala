package eu.execom.fabut

//TODO move to enum files

package object enums {

  trait Enum {
    def name: String
  }

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

  //  case class CommentType(name: String) extends Enum
  //
  //  object CommentType {
  //
  //    val FAIL = CommentType("■")
  //    val SUCCESS = CommentType("∞")
  //    val COLLECTION = CommentType("#")
  //    val values: List[CommentType] = FAIL :: SUCCESS :: COLLECTION :: Nil
  //
  //    def withName(name: String): CommentType = values.find(_.name == name).get
  //  }

  case class CommentType(name: String)

  object CommentType extends Enumeration {

    type CommentType = Value
    val FAIL = Value("■")
    val SUCCESS = Value("∞")
    val COLLECTION = Value("#")
    // val values: List[CommentType] = FAIL :: SUCCESS :: COLLECTION :: Nil

  }

}