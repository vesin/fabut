package eu.execom.fabut.property

/**
 * Intermediate object that stores entity and info is the entity asserted or not.
 */
case class CopyAssert(var entity: Any) {

  var _asserted: Boolean = false

  /**
   * Get is entity asserted.
   *
   * @return <code>true</code> if is else return <code>false</code> .
   */
  def asserted: Boolean = _asserted

  /**
   * Set is entity asserted.
   *
   * @param asserted
   *            is entity asserted.
   */
  def asserted_=(asserted: Boolean) =
    _asserted = asserted
}