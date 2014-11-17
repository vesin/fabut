package eu.execom.fabut.property

case class NotNullProperty(path: String) extends AbstractProperty(path) {
  override def getPath = path
}