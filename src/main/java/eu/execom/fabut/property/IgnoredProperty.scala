package eu.execom.fabut.property

case class IgnoredProperty(path: String) extends AbstractProperty(path) {
  override def getPath = path
}