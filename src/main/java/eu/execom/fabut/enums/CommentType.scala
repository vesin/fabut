package eu.execom.fabut.enums

object CommentType extends Enumeration {
  type CommentType = Value

  //TODO consider using other signs.
  val FAIL = Value("x")
  val SUCCESS = Value("✓")
  val COLLECTION = Value("#")

}