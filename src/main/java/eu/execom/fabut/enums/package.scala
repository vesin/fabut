package eu.execom.fabut

//TODO move to enum files

package object enums {

  trait Enum {
    def name: String
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

  //  case class CommentType(name: String)
  //
  //  object CommentType extends Enumeration {
  //
  //    type CommentType = Value
  //    val FAIL = Value("■")
  //    val SUCCESS = Value("∞")
  //    val COLLECTION = Value("#")
  //    // val values: List[CommentType] = FAIL :: SUCCESS :: COLLECTION :: Nil
  //
  //  }

}