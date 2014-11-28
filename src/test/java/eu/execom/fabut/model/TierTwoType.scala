package eu.execom.fabut.model

/**
 * Class representing tier two objects with one complex object property.
 */
case class TierTwoType(var _property: TierOneType) extends Type
