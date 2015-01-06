package eu.execom.fabut

import eu.execom.fabut.AssertableType._

import scala.reflect.runtime.universe.Type

trait FabutRepository extends Fabut with InitFabut {

  /**
   * Find all objects of specified type.
   *
   * @param clazz
   * class type of objects that are requested.
   * @return { @link List } of objects of clazz type
   */
  def findAll(clazz: Type): List[Any]

  /**
   * Find object of requested class by id.
   *
   * @param entityClass
   * of the object
   * @param id
   * of the object
   * @return matching object if it exist, else return <code>null</code>
   */
  def findById(entityClass: Type, id: Any): Any

  /**
   * Get { @link List } of entity types that that can be persisted.
   *
   * @return { @link List } with existing entity types.
   */
  def entityTypes(): List[Type]

  override def beforeTest(): Unit = {
    super.beforeTest()
    assertType = AssertType.REPOSITORY_ASSERT
    fabutAssert = new FabutRepositoryAssert(this, assertType)
    fabutAssert.types(ENTITY_TYPE) = entityTypes()
  }

  override def afterTest(): Unit = {
    super.afterTest()
  }
}