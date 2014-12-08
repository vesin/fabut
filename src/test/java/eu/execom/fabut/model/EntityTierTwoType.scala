package eu.execom.fabut.model

class EntityTierTwoType(prop: String, var _subProperty: EntityTierOneType, var _id: Int) extends TierOneType(prop) {

  def id: Int = _id

  def id_=(id: Int) =
    _id = id

  def subProperty: EntityTierOneType = _subProperty

  def subProperty_=(subProperty: EntityTierOneType) =
    _subProperty = subProperty

  def property: String = _property

  def property_=(property: String) =
    _property = property

  def this() = this("", null, 0)

}