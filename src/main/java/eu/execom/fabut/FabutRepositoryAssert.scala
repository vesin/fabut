package eu.execom.fabut
import eu.execom.fabut.util.ReflectionUtil._
import eu.execom.fabut.property.CopyAssert
import scala.reflect.runtime.universe.{ Type }
import scala.collection.mutable.{ Map => MutableMap }
import eu.execom.fabut.property.CopyAssert
import eu.execom.fabut.enums.AssertableType._
import eu.execom.fabut.exception.CopyException
import eu.execom.fabut.enums.AssertType._
import eu.execom.fabut.property.IProperty
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.pair.AssertPair
import eu.execom.fabut.enums.AssertType
import eu.execom.fabut.property.CopyAssert
import eu.execom.fabut.property.CopyAssert

class FabutRepositoryAssert(fabutTest: IFabutTest, assertType: AssertType.Value) extends FabutObjectAssert(fabutTest) {

  val repositoryFabutTest: IFabutRepositoryTest = assertType match {
    case REPOSITORY_ASSERT =>
      fabutTest.asInstanceOf[IFabutRepositoryTest]
    case _ =>
      null // throw sta? kasnije
  }

  types(ENTITY_TYPE) = repositoryFabutTest.entityTypes
  private var isRepositoryValid = false
  private var dbSnapshot: MutableMap[Type, MutableMap[Any, CopyAssert]] = MutableMap()

  setFabutAssert(this)

  override def assertEntityPair(propertyName: String, pair: AssertPair, properties: Map[String, IProperty], nodesList: NodesList)(implicit report: FabutReportBuilder): Boolean = {

    assertType match {
      case OBJECT_ASSERT =>
        super.assertEntityPair(propertyName, pair, properties, nodesList)(report)
      case REPOSITORY_ASSERT if (pair.isProperty) =>
        assertEntityById(propertyName, pair)(report)
      case REPOSITORY_ASSERT =>
        assertSubfields(propertyName, pair, properties, nodesList)(report)
    }
  }

  override def takeSnapshot(parameters: Seq[Any])(implicit report: FabutReportBuilder): Boolean = {

    initDbSnapshot

    val isParameterSnapshotOk = if (parameters.nonEmpty) {
      super.takeSnapshot(parameters)
    } else {
      ASSERTED
    }

    var ok = ASSERTED;

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

    var map: Map[Any, Any] = Map()
    val entities = findAll(entityClassType)

    for (entity <- entities) {
      val id = getFieldValueFromGetter("id", entity, getObjectType(entity, ENTITY_TYPE))
      if (id.isDefined) {
        map += id.get -> entity
      }
    }
    map
  }

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

  def assertEntityById(propertyName: String, pair: AssertPair)(implicit report: FabutReportBuilder): Boolean = {

    val actualValue = getFieldValueFromGetter("id", pair.actual, getObjectType(pair.actual, ENTITY_TYPE)).get
    val expectedValue = getFieldValueFromGetter("id", pair.expected, getObjectType(pair.expected, ENTITY_TYPE)).get
    try {
      fabutTest.customAssertEquals(expectedValue, actualValue)
      ASSERTED

    } catch {
      case e: AssertionError => {
        report.assertFail(pair, propertyName)
        //        report.addPropertiesExceptionMessage(entityObjectName + "id", actualValue, expectedValue)
        ASSERT_FAIL
      }
    }
  }

  def assertEntityWithSnapshot(report: FabutReportBuilder, entity: Any, properties: Map[String, IProperty]): Boolean = {

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

  def assertEntityAsDeleted(report: FabutReportBuilder, entity: Any): Boolean = {

    val ignoredEntity = ignoreEntity(entity)(report)

    val foundByIdObject = findById(getObjectType(entity, ENTITY_TYPE).get, getIdValue(entity))
    val isDeletedInRepository = foundByIdObject == None;

    if (!isDeletedInRepository) {
      report.notDeletedInRepositoy(entity)
    }
    ignoredEntity && isDeletedInRepository
  }

  def ignoreEntity(entity: Any)(implicit report: FabutReportBuilder): Boolean = {
    markAsAsserted(report, entity, getObjectType(entity, ENTITY_TYPE))
  }

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

  def initDbSnapshot = {
    dbSnapshot.clear
    getEntityTypes.foreach {
      entity =>
        dbSnapshot += entity -> MutableMap()
    }
  }

  def findAll(entityClassType: Type): List[_] = {
    repositoryFabutTest.findAll(entityClassType)
  }

  def findById(entityClassType: Type, id: Any): Any = {
    repositoryFabutTest.findById(entityClassType, id)
  }

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