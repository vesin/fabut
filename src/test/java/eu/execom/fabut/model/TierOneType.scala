package eu.execom.fabut.model

/**
 * Tier one complex type with only one {@link String} property.
 */
case class TierOneType(var _property: String) extends Type {

  def this() = this("")

}