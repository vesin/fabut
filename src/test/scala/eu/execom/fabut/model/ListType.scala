package eu.execom.fabut.model

class ListType(private var _property: List[Any]) extends Type {
  def property: List[Any] = _property

  def property_=(property: List[Any]) =
    _property = property
}