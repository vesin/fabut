package eu.execom.fabut.model

case class EntityTierOneType(property: String, var _id: Any) extends TierOneType(property) {

  def id: Any = _id

  def id_=(id: Any) =
    _id = id

  def this() = this("", 0)
}

object EntityTierOneType {

  val PROPERTY = "_property"

  val ID = "_id"

}