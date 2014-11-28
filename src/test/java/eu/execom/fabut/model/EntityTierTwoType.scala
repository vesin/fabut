package eu.execom.fabut.model

class EntityTierTwoType(var property: String, var _subProperty: EntityTierOneType, var _id: Int) extends TierOneType(property) {

  def id: Int = _id

  def id_=(id: Int) =
    _id = id

  def subProperty: EntityTierOneType = _subProperty

  def subProperty_=(subProperty: EntityTierOneType) =
    _subProperty = subProperty

  def this() = this("", null, 0)

}