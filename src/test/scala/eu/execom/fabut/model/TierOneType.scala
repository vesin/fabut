package eu.execom.fabut.model

/**
 * Tier one complex type with only one {@link String} property.
 */
class TierOneType(private var _property: String = "") extends Type {

  def property: String = _property

  def property_=(property: String):Unit = { _property = property}

  val PROPERTY: String = "property"

  def this() = this("")

}