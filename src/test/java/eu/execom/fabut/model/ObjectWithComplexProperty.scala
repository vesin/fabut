package eu.execom.fabut.model

case class ObjectWithComplexProperty(
  var id: Int,
  var state: Boolean,
  var complexObject: ObjectWithSimpleProperties,
  var list: List[Any]) {

  def this() = this(0, false, null, null)
}

