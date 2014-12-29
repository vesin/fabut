package eu.execom.fabut

import eu.execom.fabut.AssertType._
import eu.execom.fabut.AssertableType._
import eu.execom.fabut.exception.CopyException
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.pair.AssertPair
import eu.execom.fabut.property.{CopyAssert, IProperty}
import eu.execom.fabut.report.FabutReportBuilder
import eu.execom.fabut.util.ReflectionUtil.{createCopy, getClassType, getIdValue}

import scala.collection.mutable.{Map => MutableMap}
import scala.reflect.runtime.universe.Type

//TODO inline comment wtf dusko?
/**
 * Extension of {@link FabutObjectAssert} with functionality to assert db snapshot with its after state.
 */
class FabutRepositoryAssert(fabutTest: IFabutTest, assertType: AssertType.Value) extends FabutObjectAssert(fabutTest) {

  /** The db snapshot. */
  private[this] val _dbSnapshot: MutableMap[Type, MutableMap[Any, CopyAssert]] = MutableMap()
  private[this] val _repositoryFabutTest = fabutTest.asInstanceOf[IFabutRepositoryTest]
  private[this] var isRepositoryValid = false
  types(ENTITY_TYPE) = _repositoryFabutTest.entityTypes()

  override def assertEntityPair(propertyName: String, pair: AssertPair, properties: Map[String, IProperty], nodesList: NodesList)(implicit report: FabutReportBuilder): Boolean =
    assertType match {
      case OBJECT_ASSERT => super.assertEntityPair(propertyName, pair, properties, nodesList)
      case REPOSITORY_ASSERT if pair.property => assertEntityById(propertyName, pair)
      case REPOSITORY_ASSERT => assertSubfields(propertyName, pair, properties, nodesList)
    }

  /**
   * Asserts two entities by their id.
   *
   * @param report
   * - assert report builder
   * @param propertyName
   * - name of current entity
   * @return - <code> true </code> if and only if id's of two specified objects are equal, <code> false </code> otherwise
   */
  def assertEntityById(propertyName: String, pair: AssertPair)(implicit report: FabutReportBuilder): Boolean = try {
    val actualValue = getIdValue(pair.expected).get
    val expectedValue = getIdValue(pair.actual).get
    fabutTest.customAssertEquals(expectedValue, actualValue)
    ASSERTED
  } catch {
    case e: AssertionError =>
      report.assertFail(pair, propertyName)
      ASSERT_FAIL
    case e: NoSuchElementException =>
      report.missingPropertyInClass("id", pair.expected.getClass.getSimpleName)
      ASSERT_FAIL
  }

  /**
   * Asserts entity with one saved in snapshot.
   *
   * @param entity
   * the entity
   * @param properties
   * properties changed after the snapshot has been taken
   */
  def assertEntityWithSnapshot(report: FabutReportBuilder, entity: Any, properties: Map[String, IProperty]): Boolean =
    try {
      val id = getIdValue(entity).get
      val entityType = getClassType(entity, ENTITY_TYPE).get

      if (_dbSnapshot(entityType).contains(id)) {
        val expected = _dbSnapshot(entityType)(id).entity
        assertObjects(expected, entity, properties)(report)
      } else {
        ASSERT_FAIL
      }
    } catch {
      case e: NoSuchElementException => ASSERT_FAIL
    }

  /**
   * Asserts that entity has been deleted in after db state.
   *
   * @param report
   * the report
   * @param entity
   * the entity
   * @return <code>true</code> if entity is really deleted, <code>false</code> otherwise.
   */
  def assertEntityAsDeleted(implicit report: FabutReportBuilder, entity: Any): Boolean = {
    val ignoredEntity = ignoreEntity(entity)
    val foundByIdObject = findById(getClassType(entity, ENTITY_TYPE).get, getIdValue(entity).get)
    val isDeletedInRepository = foundByIdObject == null

    if (!isDeletedInRepository) {
      report.notDeletedInRepositoy(entity)
    }
    ignoredEntity && isDeletedInRepository
  }

  /**
   * Ignores the entity.
   *
   * @param entity
   * the entity
   */
  def ignoreEntity(entity: Any)(implicit report: FabutReportBuilder): Boolean = markAsAsserted(report, entity, getClassType(entity, ENTITY_TYPE))

  /**
   * Takes current database snapshot and saves it.
   *
   * @param report
   * the report
   * @param parameters
   * the parameters
   * @return true, if successful
   */
  override def takeSnapshot(parameters: Any*)(implicit report: FabutReportBuilder): Boolean = {
    initDbSnapshot()
    isRepositoryValid = true

    var ok = ASSERTED

    val isParameterSnapshotOk = if (parameters.nonEmpty) {
      super.takeSnapshot(parameters: _*)
    } else {
      ASSERTED
    }

    _dbSnapshot.foreach {
      case (tableName, table) =>
        val entities = findAll(tableName)
        entities.foreach(entity =>
          try {
            val copy = createCopy(entity)
            table += getIdValue(entity).get -> CopyAssert(copy)
          } catch {
            case e: CopyException =>
              report.noCopy(entity)
              ok = ASSERT_FAIL
            case e: NoSuchElementException =>
              report.missingPropertyInClass("id", entity.getClass.getSimpleName)
              ok = ASSERT_FAIL
          }
        )
    }
    ok && isParameterSnapshotOk
  }

  /**
   * Initialize database snapshot.
   */
  def initDbSnapshot(): Unit = {
    _dbSnapshot.clear()
    getEntityTypes.foreach(entity => _dbSnapshot += entity -> MutableMap())
  }

  /**
   * Find all entities of type entity class in DB.
   *
   * @param entityClassType
   * the entity class
   * @return the list
   */
  def findAll(entityClassType: Type): List[_] = _repositoryFabutTest.findAll(entityClassType)

  /**
   * Marks the specified entity as asserted.
   *
   * @param report
   * the report
   * @param entity
   * the entity
   * @param entityType
   * the actual entity type
   * @return <code>true</code> if entity is successfully asserted else return <code>false</code>.
   */
  def markAsAsserted(report: FabutReportBuilder, entity: Any, entityType: Option[Type]): Boolean = try {
    val id = getIdValue(entity) //.getOrElse(throw new NoSuchElementException(s"Property with name 'id' doesn't exist in ${entity.getClass.getSimpleName}")*/)
    if (id.isDefined && id.get != null) {
      val copy = createCopy(entity)
      val marked = markAsserted(id.get, copy, entityType.get)(report)
      if (!marked) {
        report.notExistingInSnapshot(entity)
        ASSERT_FAIL
      } else {
        ASSERTED
      }
    } else {
      report.idNull(entity)
      ASSERT_FAIL
    }
  } catch {
    case e: CopyException => report.noCopy(entity)
      ASSERT_FAIL
  }

  /**
   * Mark entity bean as asserted in db snapshot map. Go trough all its supper classes and if its possible assert it.
   *
   * @param id
   * the id
   * @param copy
   * the entity
   * @param entityType
   * the actual type
   * @return true, if successful
   */
  def markAsserted(id: Any, copy: Any, entityType: Type)(implicit report: FabutReportBuilder): Boolean = {
    val isTypeSupported = _dbSnapshot.contains(entityType)

    if (isTypeSupported) {
      val copyAssert = _dbSnapshot(entityType) match {
        case entities if entities.contains(id) => entities(id)
        case entities =>
          val copyAssert = CopyAssert(copy)
          entities += id -> copyAssert
          copyAssert
      }
      copyAssert.asserted = true
    }

    val superClassType = entityType.baseClasses.isDefinedAt(2)
    val isSuperSuperTypeSupported = superClassType && markAsserted(id, copy, entityType.baseClasses(2).typeSignature)
    isTypeSupported || isSuperSuperTypeSupported
  }

  /**
   * This method needs to be called after every entity assert so it marks that entity has been asserted in snapshot.
   *
   * @param report
   * the report
   * @param entity
   * the entity
   * @param isProperty
   * if entity is a property of object
   * @return <code>true</code> if entity can be marked that is asserted, <code>false</code> otherwise.
   */
  def afterAssertEntity(report: FabutReportBuilder, entity: Any, isProperty: Boolean): Boolean = isProperty match {
    case PROPERTY => ASSERTED
    case _ => markAsAsserted(report, entity, getClassType(entity, ENTITY_TYPE))
  }

  /**
   * Checks if object of entity type and if it is mark it as asserted entity,
   * in other case do nothing.
   *
   * @param theObject
   * the object
   * @param isSubproperty
   * is object subproperty
   *
   * @return true, if successful
   */
  override def afterAssertObject(theObject: Any, isSubproperty: Boolean): Boolean = afterAssertEntity(new FabutReportBuilder, theObject, isSubproperty)

  /**
   * Find specific entity of type entity class and with specific id in DB.
   *
   * @param entityClassType
   * the entity class
   * @param id
   * the id
   * @return the entity type
   */
  def findById(entityClassType: Type, id: Any): Any = _repositoryFabutTest.findById(entityClassType, id)

  /**
   * Asserts db snapshot with after db state.
   *
   * @param report
   * the report
   * @return true, if successful
   */
  def assertDbSnapshot(implicit report: FabutReportBuilder): Boolean =
    _dbSnapshot.forall(snapshotEntry => {
      val afterEntities = getAfterEntities(snapshotEntry._1)
      val beforeIds = snapshotEntry._2.keySet.toSet
      val afterIds = afterEntities.keySet

      checkNotExistingInAfterDbState(beforeIds, afterIds, snapshotEntry._2.toMap) &&
        checkNewToAfterDbState(beforeIds, afterIds, afterEntities) &&
        assertDbSnapshotWithAfterState(beforeIds, afterIds, snapshotEntry._2.toMap, afterEntities)
    })

  def getAfterEntities(entityClassType: Type): Map[Any, Any] = {
    val afterEntities: MutableMap[Any, Any] = MutableMap()
    val entities = findAll(entityClassType)
    entities.foreach{ entity =>
      val id =  getIdValue(entity)
      if(id.isDefined){
        afterEntities += id.get -> entity
      }
    }
    afterEntities.toMap
  }

  /**
   * Performs assert check on entities that are contained in db snapshot but do not exist in after db state.
   *
   * @param beforeIds
   * the before ids
   * @param afterIds
   * the after ids
   * @param beforeEntities
   * the before entities
   * @param report
   * the report
   * @return <code>true</code> if all entities contained only in db snapshot are asserted, <code>false</code>
   *         otherwise.
   */
  def checkNotExistingInAfterDbState(beforeIds: Set[Any], afterIds: Set[Any], beforeEntities: Map[Any, CopyAssert])(implicit report: FabutReportBuilder): Boolean =
    beforeIds.diff(afterIds).forall(id => {
      val copyAssert = beforeEntities(id)
      if (!copyAssert.asserted) {
        report.noEntityInSnapshot(copyAssert.entity)
      }
      copyAssert.asserted
    })

  /**
   * Performs check if there is any entity in after db state that has not been asserted and reports them.
   *
   * @param beforeIds
   * the before ids
   * @param afterIds
   * the after ids
   * @param afterEntities
   * the after entities
   * @param report
   * the report
   * @return <code>true</code> if all entities in after db state are asserted.
   */
  def checkNewToAfterDbState(beforeIds: Set[Any], afterIds: Set[Any], afterEntities: Map[Any, Any])(implicit report: FabutReportBuilder): Boolean =
    afterIds.diff(beforeIds).forall(id => {
      val entity = afterEntities(id)
      report.entityNotAssertedInAfterState(entity)
      ASSERT_FAIL
    })

  /**
   * Assert db snapshot with after state.
   *
   * @param beforeIds
   * the before ids
   * @param afterIds
   * the after ids
   * @param beforeEntities
   * the before entities
   * @param afterEntities
   * the after entities
   * @param report
   * the report
   * @return true, if successful
   */
  def assertDbSnapshotWithAfterState(beforeIds: Set[Any], afterIds: Set[Any], beforeEntities: Map[Any, CopyAssert], afterEntities: Map[Any, Any])(implicit report: FabutReportBuilder): Boolean =
    beforeIds.intersect(afterIds).filter(id => !beforeEntities(id).asserted).forall(id => assertObjects(beforeEntities(id).entity, afterEntities(id), Map()))
}