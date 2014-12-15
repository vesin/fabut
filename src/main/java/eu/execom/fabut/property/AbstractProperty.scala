package eu.execom.fabut.property

/**
 *  Abstract property definition that contains path to some property that is used.
 */
trait AbstractProperty {
  def path: String

  //TODO do we need this equal?
  override def equals(property: Any): Boolean = {
    path.equalsIgnoreCase(property.asInstanceOf[AbstractProperty].path)
  }
}