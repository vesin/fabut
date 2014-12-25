package eu.execom.fabut.model

case class NotInTypes(private var _property: String) extends Type {
  def this() = this("")
}