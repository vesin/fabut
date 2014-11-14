package eu.execom.fabut.model

/**
 * Class representing tier two objects with one complex object property.
 */
class TierTwoType extends Type {

  var _property: TierOneType = null

  def property: TierOneType = _property

  def property_=(property: TierOneType) =
    _property = property

}