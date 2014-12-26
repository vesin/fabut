package eu.execom.fabut.property

/**
 * `AbstractProperty` extension with limited checking is property only different then null.
 */
case class NullProperty(path: String) extends IProperty