package eu.execom.fabut.model

case class ObjectWithComplexProperty(
  id: Int,
  var state: Boolean,
  complexObject: ObjectWithSimpleProperties,
  list: List[Any]) {

}