package eu.execom.fabut.model

case class EntityTierOneType(property1: String, private var _id: Any) extends TierOneType(property1) {

  def id: Any = _id

  def id_=(id: Any) =
    _id = id

  def this() = this("", 0)
}

object EntityTierOneType {

  val PROPERTY = "property"

  val ID = "id"

}