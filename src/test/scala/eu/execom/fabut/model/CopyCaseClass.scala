package eu.execom.fabut.model

case class CopyCaseClass(
                          var id: String,
                          var name: String,
                          var complexObject: ObjectInsideSimpleProperty,
                          var list: List[_],
                          var map: Map[_, _]) {

  def this() = this("", "", null, List(), Map())
}