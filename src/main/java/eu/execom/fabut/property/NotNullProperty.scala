package eu.execom.fabut.property

/**
 * {@link AbstractProperty} extension with limited checking is property equal with <code>null</code>.
 */
case class NotNullProperty(override val path: String) extends AbstractProperty
