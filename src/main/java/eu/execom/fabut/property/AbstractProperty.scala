package eu.execom.fabut.property

/**
 *  Abstract property definition that contains path to some property that is used.
 */
abstract class AbstractProperty {
  def path: String

  override def equals(property: Any): Boolean = {
    path.equalsIgnoreCase(property.asInstanceOf[AbstractProperty].path)
  }
}