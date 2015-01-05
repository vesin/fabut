package eu.execom.fabut.property

/**
 * `AbstractProperty` extension with focus on new value of the property
 */
case class Property(override val path: String, value: Any) extends IProperty

