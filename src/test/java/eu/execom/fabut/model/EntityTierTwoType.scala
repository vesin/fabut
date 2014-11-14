package eu.execom.fabut.model

class EntityTierTwoType extends TierOneType {

  var a_id: Int = 0
  var a_subProperty: EntityTierOneType = null

  def id: Int = a_id

  def id_=(id: Int) =
    a_id = id

  def subProperty: EntityTierOneType = a_subProperty

  def subProperty_=(subProperty: EntityTierOneType) =
    a_subProperty = subProperty

}