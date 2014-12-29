package eu.execom.fabut.model

class EntityTierThreeType(property: String, subProperty: EntityTierOneType,  id: Int) extends EntityTierTwoType(property, subProperty, id) {
  def this() = this("", null, 0)
}