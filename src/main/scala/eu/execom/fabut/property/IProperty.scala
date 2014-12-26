package eu.execom.fabut.property

/**
 * Abstract property definition that contains path to some property that is used.
 */
trait IProperty {

  /**
   * The name path of the property
   **/
  def path(): String
}