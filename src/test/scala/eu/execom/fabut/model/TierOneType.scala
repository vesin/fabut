package eu.execom.fabut.model

/**
 * Tier one complex type with only one {@link String} property.
 */
class TierOneType(var _property: String = "") extends Type {

  val PROPERTY: String = "property"

  def property: String = _property

  def property_=(property: String): Unit = {
    _property = property
  }

  def this() = this("")

}