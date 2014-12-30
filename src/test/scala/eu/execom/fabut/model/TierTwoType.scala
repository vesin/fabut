package eu.execom.fabut.model

/**
 * Class representing tier two objects with one complex object property.
 */
case class TierTwoType(private var _property: TierOneType) extends Type {

  def property: TierOneType = _property

  def property_=(property: TierOneType): Unit = {
    _property = property
  }

  def this() = this(null)
}
