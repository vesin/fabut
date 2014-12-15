package eu.execom.fabut.property

/**
 * The class IgnoreProperty. {@link AbstractProperty} extension with limited
 * functionality only to mark property as ignored for testing.
 */
case class IgnoredProperty(path: String) extends AbstractProperty
