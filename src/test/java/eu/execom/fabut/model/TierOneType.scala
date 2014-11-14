package eu.execom.fabut.model

/**
 * Tier one complex type with only one {@link String} property.
 */
case class TierOneType(var _property: String) extends Type {

  val PROPERTY = "property"
  //  var _property: String = null
  //
  //  def property: String = _property
  //
  //  def property_=(property: String) =
  //    _property = property

  def this() = this("")

}