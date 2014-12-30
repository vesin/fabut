package eu.execom.fabut.model

class TierTwoTypeWithIgnoredType(private var _property: IgnoredType) extends Type {

  def property: IgnoredType = _property

  def property_=(property: IgnoredType) =
    _property = property
}