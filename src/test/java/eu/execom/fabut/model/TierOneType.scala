package eu.execom.fabut.model

/**
 * Tier one complex type with only one {@link String} property.
 */
class TierOneType(var _property: String) extends Type {

  val PROPERTY: String = "_property"

  //    def property: String = _property
  //  
  //    def property_=(newProperty: String) = {
  //      _property = newProperty
  //    }

  def this() = this("")

}