package eu.execom.fabut

import scala.collection.mutable.{ Map => MutableMap }
import scala.reflect.runtime.universe.Type
import eu.execom.fabut.enums.AssertType
import eu.execom.fabut.enums.AssertType.OBJECT_ASSERT
import eu.execom.fabut.enums.AssertType.REPOSITORY_ASSERT
import eu.execom.fabut.enums.AssertableType.ENTITY_TYPE
import eu.execom.fabut.exception.CopyException
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.pair.AssertPair
import eu.execom.fabut.property.CopyAssert
import eu.execom.fabut.property.AbstractProperty
import eu.execom.fabut.util.ReflectionUtil.createCopy
import eu.execom.fabut.util.ReflectionUtil.getFieldValueFromGetter
import eu.execom.fabut.util.ReflectionUtil.getIdValue
import eu.execom.fabut.util.ReflectionUtil.getObjectType
import eu.execom.fabut.report.FabutReportBuilder

/**
 * Extension of {@link FabutObjectAssert} with functionality to assert db snapshot with its after state.
 */
class FabutRepositoryAssert(fabutTest: IFabutTest, assertType: AssertType.Value) extends FabutObjectAssert(fabutTest) {

  /** The db snapshot. */
  private val dbSnapshot: MutableMap[Type, MutableMap[Any, CopyAssert]] = MutableMap()
  private var isRepositoryValid = false

  val repositoryFabutTest: IFabutRepositoryTest = assertType match {
    case REPOSITORY_ASSERT =>
      fabutTest.asInstanceOf[IFabutRepositoryTest]
    case _ =>
      null
  }

  types(ENTITY_TYPE) = repositoryFabutTest.entityTypes

  override def assertEntityPair(propertyName: String, pair: AssertPair, properties: Map[String, AbstractProperty], nodesList: NodesList)(implicit report: FabutReportBuilder): Boolean = {

    assertType match {
      case OBJECT_ASSERT =>
        super.assertEntityPair(propertyName, pair, properties, nodesList)
      case REPOSITORY_ASSERT if (pair.isProperty) =>
        assertEntityById(propertyName, pair)
      case REPOSITORY_ASSERT =>
        assertSubfields(propertyName, pair, properties, nodesList)
    }
  }

  /**
   * Asserts entity with one saved in snapshot.
   *
   * @param entity
   *            the entity
   * @param expectedChanges
   *            properties changed after the snapshot has been taken
   */
  def assertEntityWithSnapshot(report: FabutReportBuilder, entity: Any, properties: Map[String, AbstractProperty]): Boolean = {

    val id = getIdValue(entity)
    val entityType = getObjectType(entity, ENTITY_TYPE).get

    val expected = try {
      dbSnapshot(entityType)(id).entity
    } catch {
      case e: NoSuchElementException =>
        return ASSERT_FAIL
    }

    assertObjects(expected, entity, properties)(report)
  }

  /**
   * Asserts that entity has been deleted in after db state.
   *
   * @param report
   * @param entity
   * @return <code>true</code> if entity is really deleted, <code>false</code> otherwise.
   */
  def assertEntityAsDeleted(implicit report: FabutReportBuilder, entity: Any): Boolean = {

    val ignoredEntity = ignoreEntity(entity)

    val foundByIdObject = findById(getObjectType(entity, ENTITY_TYPE).get, getIdValue(entity))
    val isDeletedInRepository = foundByIdObject == None;

    if (!isDeletedInRepository) {
      report.notDeletedInRepositoy(entity)
    }
    ignoredEntity && isDeletedInRepository
  }

  /**
   * Ignores the entity.
   *
   * @param entity
   *            the entity
   */
  def ignoreEntity(entity: Any)(implicit report: FabutReportBuilder): Boolean = {
    markAsAsserted(report, entity, getObjectType(entity, ENTITY_TYPE))
  }

  /**
   * Takes current database snapshot and saves it.
   *
   * @param report
   *            the report
   * @param parameters
   *            the parameters
   * @return true, if successful
   */
  override def takeSnapshot(parameters: Any*)(implicit report: FabutReportBuilder): Boolean = {

    initDbSnapshot
    var ok = ASSERTED

    val isParameterSnapshotOk = if (parameters.nonEmpty) {
      super.takeSnapshot(parameters)
    } else {
      ASSERTED
    }

    dbSnapshot.foreach {
      case (entryKey, entryValue: MutableMap[Any, CopyAssert]) => {

        val foundAll: List[_] = findAll(entryKey)

        foundAll.foreach {
          entity =>
            try {
              val copy = createCopy(entity)
              entryValue += getFieldValueFromGetter("id", entity, getObjectType(entity, ENTITY_TYPE)).get -> CopyAssert(copy)
            } catch {
              case e: CopyException =>
                report.noCopy(entity)
                ok = ASSERT_FAIL
            }
        }
      }
    }

    ok && isParameterSnapshotOk
  }

  def getAfterEntities(entityClassType: Type): Map[Any, Any] = {

    val afterEntities: MutableMap[Any, Any] = MutableMap()
    val entities = findAll(entityClassType)

    for (entity <- entities) {
      val id = getFieldValueFromGetter("id", entity, getObjectType(entity, ENTITY_TYPE))
      if (id.isDefined) {
        afterEntities += id.get -> entity
      }
    }
    afterEntities.toMap
  }

  /**
   * Asserts two entities by their id.
   *
   * @param report
   *            assert report builder
   * @param propertyName
   *            name of current entity
   * @return - <code>true</code> if and only if id's of two specified objects are equal, <code>false</code> otherwise
   */
  def assertEntityById(propertyName: String, pair: AssertPair)(implicit report: FabutReportBuilder): Boolean = {

    val actualValue = getFieldValueFromGetter("id", pair.actual, getObjectType(pair.actual, ENTITY_TYPE)).get
    val expectedValue = getFieldValueFromGetter("id", pair.expected, getObjectType(pair.expected, ENTITY_TYPE)).get

    try {
      fabutTest.customAssertEquals(expectedValue, actualValue)
      ASSERTED

    } catch {
      case e: AssertionError => {
        report.assertFail(pair, propertyName)
        ASSERT_FAIL
      }
    }
  }

  /**
   * Marks the specified entity as asserted.
   *
   * @param report
   * @param entity
   *            the entity
   * @param actualType
   *            the actual entity type
   * @return <code>true</code> if entity is successfully asserted else return <code>false</code>.
   */
  def markAsAsserted(report: FabutReportBuilder, entity: Any, entityType: Option[Type]): Boolean = {

    val id = getIdValue(entity)

    if (id == null) {
      report.idNull(entity)
      return ASSERT_FAIL
    }
    val copy = try {
      createCopy(entity)
    } catch {
      case e: CopyException =>
        report.noCopy(entity)
        return ASSERT_FAIL
    }
    markAsserted(id, copy, entityType.get)(report)
  }

  /**
   * Mark entity bean as asserted in db snapshot map. Go trough all its supper classes and if its possible assert it.
   *
   * @param id
   *       the id
   * @param copy
   *       the entity
   * @param actualType
   *        the actual type
   * @return true, if successful
   */
  def markAsserted(id: Any, copy: Any, entityType: Type)(implicit report: FabutReportBuilder): Boolean = {

    val isTypeSupported = try {
      dbSnapshot.contains(entityType)
    } catch {
      case e: NoSuchElementException => false
    }

    if (isTypeSupported) {
      val copyAssert = try {
        val map = dbSnapshot(entityType)
        val x = map(id)
        x
      } catch {
        case e: NoSuchElementException =>
          val copyAssert = CopyAssert(copy)

          dbSnapshot(entityType) += id -> copyAssert
          copyAssert
      }
      copyAssert.asserted_=(true)

    }

    val superClassType = if (entityType.baseClasses.contains(2)) entityType.baseClasses(2) else null
    val isSuperSuperTypeSupported = (superClassType != null) && {
      if (entityType.baseClasses.contains(3)) markAsserted(id, copy, entityType.baseClasses(3).typeSignature) else true
    }

    val marked = isTypeSupported || isSuperSuperTypeSupported

    marked
  }

  /**
   * This method needs to be called after every entity assert so it marks that entity has been asserted in snapshot.
   *
   * @param report
   * @param entity
   * @param isProperty
   * @return <code>true</code> if entity can be marked that is asserted, <code>false</code> otherwise.
   */
  def afterAssertEntity(report: FabutReportBuilder, entity: Any, isProperty: Boolean): Boolean = {
    if (!isProperty) {
      markAsAsserted(report, entity, getObjectType(entity, ENTITY_TYPE))
    } else {
      ASSERTED
    }
  }

  override def afterAssertObject(theObject: Any, isSubproperty: Boolean): Boolean = {
    afterAssertEntity(new FabutReportBuilder, theObject, isSubproperty)
  }

  /**
   * Initialize database snapshot.
   */
  def initDbSnapshot = {
    dbSnapshot.clear
    getEntityTypes.foreach {
      entity =>
        dbSnapshot += entity -> MutableMap()
    }
  }

  /**
   * Find all entities of type entity class in DB.
   *
   * @param entityClass
   *            the entity class
   * @return the list
   */
  def findAll(entityClassType: Type): List[_] = {
    repositoryFabutTest.findAll(entityClassType)
  }

  /**
   * Find specific entity of type entity class and with specific id in DB.
   *
   * @param entityClass
   *            the entity class
   * @param id
   *            the id
   * @return the entity type
   */
  def findById(entityClassType: Type, id: Any): Any = {
    repositoryFabutTest.findById(entityClassType, id)
  }

  /**
   * Performs assert check on entities that are contained in db snapshot but do not exist in after db state.
   *
   * @param beforeIds
   *            the before ids
   * @param afterIds
   *            the after ids
   * @param beforeEntities
   *            the before entities
   * @param report
   *            the report
   * @return <code>true</code> if all entities contained only in db snapshot are asserted, <code>false</code>
   *         otherwise.
   */
  def checkNotExistingInAfterDbState(beforeIds: Set[Any], afterIds: Set[Any], beforeEntities: Map[Any, CopyAssert])(implicit report: FabutReportBuilder): Boolean = {

    val beforeIdsCopy = beforeIds.diff(afterIds)
    var ok = ASSERTED
    beforeIdsCopy.foreach {
      id =>
        val copyAssert = beforeEntities(id)
        if (!copyAssert.asserted) {
          report.noEntityInSnapshot(copyAssert.entity)
          ok = ASSERT_FAIL
        }
    }
    ok
  }

  /**
   * Performs check if there is any entity in after db state that has not been asserted and reports them.
   *
   * @param beforeIds
   *        the before ids
   * @param afterIds
   *        the after ids
   * @param afterEntities
   *        the after entities
   * @param report
   *        the report
   * @return <code>true</code> if all entities in after db state are asserted.
   */
  def checkNewToAfterDbState(beforeIds: Set[Any], afterIds: Set[Any], afterEntities: Map[Any, Any])(implicit report: FabutReportBuilder): Boolean = {

    var ok = ASSERTED

    val afterIdsCopy = afterIds.diff(beforeIds)
    afterIdsCopy.foreach {
      id =>
        val entity = afterEntities(id)
        report.entityNotAssertedInAfterState(entity)
        ok = ASSERT_FAIL
    }
    ok
  }

  /**
   * Assert db snapshot with after state.
   *
   * @param beforeIds
   *            the before ids
   * @param afterIds
   *            the after ids
   * @param beforeEntities
   *            the before entities
   * @param afterEntities
   *            the after entities
   * @param report
   *            the report
   * @return true, if successful
   */
  def assertDbSnapshotWithAfterState(beforeIds: Set[Any], afterIds: Set[Any], beforeEntities: Map[Any, CopyAssert], afterEntities: Map[Any, Any])(implicit report: FabutReportBuilder): Boolean = {

    var ok = ASSERTED

    val beforeIdsCopy = beforeIds.intersect(afterIds)
    beforeIdsCopy.foreach {
      id =>
        if (!beforeEntities(id).asserted) {
          ok &= assertObjects(beforeEntities(id).entity, afterEntities(id), Map())
        }
    }
    ok
  }

  /**
   * Asserts db snapshot with after db state.
   *
   * @param report
   *            the report
   * @return true, if successful
   */
  def assertDbSnapshot(implicit report: FabutReportBuilder): Boolean = {

    var ok = ASSERTED
    dbSnapshot.foreach {
      snapshotEntry =>
        {
          val afterEntities = getAfterEntities(snapshotEntry._1)
          val beforeIds = snapshotEntry._2.keySet.toSet
          val afterIds = afterEntities.keySet

          ok &= checkNotExistingInAfterDbState(beforeIds, afterIds, snapshotEntry._2.toMap)
          ok &= checkNewToAfterDbState(beforeIds, afterIds, afterEntities)
          ok &= assertDbSnapshotWithAfterState(beforeIds, afterIds, snapshotEntry._2.toMap, afterEntities)
        }
    }
    ok
  }

}