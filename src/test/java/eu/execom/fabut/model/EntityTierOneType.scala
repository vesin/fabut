package eu.execom.fabut.model

class EntityTierOneType(_property: String, private var _id: Int) extends TierOneType(_property) {

  def id: Int = _id

  def id_=(id: Int) =
    _id = id
  def this() = this("", 0)
}

object EntityTierOneType {

  val PROPERTY = "property"

  val ID = "id"

}