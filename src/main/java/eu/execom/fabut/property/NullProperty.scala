package eu.execom.fabut.property

case class NullProperty(path: String) extends AbstractProperty(path) {
  override def getNamePath = path
}