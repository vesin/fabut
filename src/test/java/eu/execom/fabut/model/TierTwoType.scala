package eu.execom.fabut.model

/**
 * Class representing tier two objects with one complex object property.
 */
class TierTwoType(var _property: TierOneType) extends Type {

  def property: TierOneType = _property

  def property_=(property: TierOneType) =
    _property = property

}