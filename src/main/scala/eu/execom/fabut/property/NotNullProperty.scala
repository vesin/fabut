package eu.execom.fabut.property

/**
 * `AbstractProperty` extension with limited checking is property equal with <code>null</code>.
 */
case class NotNullProperty(path: String) extends IProperty
