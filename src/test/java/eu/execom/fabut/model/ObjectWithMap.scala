package eu.execom.fabut.model

case class ObjectWithMap(
  id: Int,
  complexMapObject: ObjectWithSimpleMap,
  complexListObject: ObjectWithSimpleList,
  map: Map[Any, Any]) {
}