package eu.execom.fabut.property
import scala.collection.immutable.StringOps._

class AbstractProperty(path: String) extends ISingleProperty {

  override def getPath = path

  override def equals(property: Any): Boolean = {
    path.equalsIgnoreCase(property.asInstanceOf[ISingleProperty].getPath)
  }
}