package eu.execom.fabut.property

case class CopyAssert(val entity: Any) {

  var _asserted: Boolean = false

  def asserted: Boolean = _asserted

  def asserted_=(asserted: Boolean) =
    _asserted = asserted
}