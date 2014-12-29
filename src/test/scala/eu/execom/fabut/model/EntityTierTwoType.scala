package eu.execom.fabut.model

case class EntityTierTwoType(prop: String, var _subProperty: EntityTierOneType, private var _id: Int) extends TierOneType(prop) {

  def id: Int = _id

  def id_=(id: Int) =
    _id = id

  def subProperty: EntityTierOneType = _subProperty

  def subProperty_=(subProperty: EntityTierOneType) =
    _subProperty = subProperty


  def this() = this("", null, 0)

}