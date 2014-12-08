package eu.execom.fabut
import scala.reflect.runtime.universe.{ Type }

trait IFabutRepositoryTest extends IFabutTest {

  def findAll(clazz: Type): List[Any]
  def findById(entityClass: Type, id: Any): Any

  def entityTypes: List[Type]
}