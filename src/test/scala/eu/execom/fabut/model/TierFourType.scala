package eu.execom.fabut.model

/**
 * Class representing tier four objects with one complex object property.
 */
case class TierFourType(var _property: TierThreeType) extends Type {
  def this() = this(null)
}