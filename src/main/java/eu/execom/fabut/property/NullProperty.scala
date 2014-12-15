package eu.execom.fabut.property

/**
 *  {@link AbstractProperty} extension with limited checking is property only different then null.
 */
case class NullProperty(override val path: String) extends AbstractProperty