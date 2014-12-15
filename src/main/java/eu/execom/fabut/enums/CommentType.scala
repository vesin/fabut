package eu.execom.fabut.enums

class CommentType

object CommentType extends Enumeration {

  type CommentType = Value

  /**
   * Fail type.
   */
  val FAIL = Value("■")

  /**
   * Success type.
   */
  val SUCCESS = Value("∞")

  /**
   * List type.
   */
  val COLLECTION = Value("#")

}