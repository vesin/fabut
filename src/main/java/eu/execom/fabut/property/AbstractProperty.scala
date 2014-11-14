package eu.execom.fabut.property
import scala.collection.immutable.StringOps._

class AbstractProperty(path: String) extends IProperty {

  override def getNamePath = path

  override def equals(property: Any): Boolean = {
    path.equalsIgnoreCase(property.asInstanceOf[IProperty].getNamePath)
  }
}