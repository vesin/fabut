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

class FabutRepositoryAssert(fabutTest: IFabutTest, assertType: AssertType.Value) extends FabutObjectAssert(fabutTest) {

  val repositoryFabutTest: IFabutRepositoryTest = assertType match {
    case REPOSITORY_ASSERT =>
      fabutTest.asInstanceOf[IFabutRepositoryTest]
    case _ =>
      null
  }

  types(ENTITY_TYPE) = repositoryFabutTest.getEntityTypes

  private var isRepositoryValid = false
  private var dbSnapshot: MutableMap[Type, MutableMap[Any, CopyAssert]] = MutableMap()

  setFabutAssert(this)

  def assertEntityPair(report: FabutReport, propertyName: String, pair: AssertPair, properties: Map[String, IProperty], nodesList: NodesList): Boolean = {

    assertType match {
      case OBJECT_ASSERT =>
        true
      // TODO call super.assertEntity... that is not implemented
      case _ => {
        if (pair.isProperty) {
          return assertEntityById(propertyName, pair.actual, pair.expected)(report)
          true
        } else {
          val expectedProperties = getObjectProperties(pair.expected, getObjectType(pair.expected, getValueType(pair.expected)))
          return assertSubfields(pair.actual, expectedProperties, nodesList)(report)
        }
      }
    }
  }
  override def takeSnapshot(report: FabutReport, parameters: Any*): Boolean = {

    initDbSnapshot

    val isParameterSnapshotOk = if (parameters.nonEmpty) {
      super.takeSnapshot(report, parameters)
    } else ASSERTED

    var ok = ASSERTED;

    dbSnapshot.foreach {
      case (entryKey, entryValue: MutableMap[Any, CopyAssert]) => {
        val foundAll: List[_] = findAll(entryKey)

        foundAll.foreach {
          entity =>
            try {
              val copy = createCopy(entity)
              entryValue ++= Map(getFieldValueFromGetter("id", entity, getObjectType(entity, ENTITY_TYPE)).get -> CopyAssert(copy))
            } catch {
              case e: CopyException => // TODO Add report msg
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
        map ++= Map(id.get -> entity)
      }
    }
    return map
  }

  def afterAssertEntity(report: FabutReport, entity: Any, isProperty: Boolean): Boolean = {
    if (!isProperty) {
      return markAsAsserted(entity, getObjectType(entity, ENTITY_TYPE))(report)
    } else {
      return ASSERTED
    }
  }

  def assertEntityAsDeleted(report: FabutReport, entity: Any): Boolean = {
    //TODO NEXT TIME
    val ignoredEntity = ignoreEntity(entity)(report)

    val foundByIdObject = findById(getObjectType(entity, ENTITY_TYPE).get, getIdValue(entity))
    val isDeletedInRepository = foundByIdObject == null;

    if (!isDeletedInRepository) {
      println("REPORT NOT DELETE IN REPOSITORY")
    }
    return ignoredEntity && isDeletedInRepository
  }

  def ignoreEntity(entity: Any)(implicit report: FabutReport): Boolean = {
    return markAsAsserted(entity, getObjectType(entity, ENTITY_TYPE))
  }

  def markAsAsserted(entity: Any, entityType: Option[Type])(implicit report: FabutReport): Boolean = {
    val id = getIdValue(entity)
    if (id == null) {
      println("report is null")
      ASSERT_FAIL
    }
    val copy = try {
      createCopy(entity)
    } catch {
      case e: CopyException =>
        return ASSERT_FAIL
    }

    return markAsserted(id, copy, entityType)
  }

  def markAsserted(id: Any, copy: Any, entityType: Option[Type])(implicit report: FabutReport): Boolean = {
    if (!entityType.isDefined) {
      return false
    } else {
      return true
    }
  }

  def initDbSnapshot = {
    dbSnapshot = MutableMap()
    getEntityTypes.foreach { entity => dbSnapshot ++= Map(entity -> MutableMap()) }
  }

  def findAll(entityClassType: Type): List[_] = {
    return repositoryFabutTest.findAll(entityClassType)
  }

  def findById(entityClassType: Type, id: Any): Any = {
    return repositoryFabutTest.findById(entityClassType, id)
  }

  def checkNotExistingInAfterDbState(beforeIds: Set[Any], afterIds: Set[Any], beforeEntities: Map[Any, CopyAssert])(implicit report: FabutReport): Boolean = {

    val beforeIdsCopy = beforeIds.diff(afterIds)
    var ok = ASSERTED
    beforeIdsCopy.foreach {
      id =>
        val copyAssert = beforeEntities(id)
        if (!copyAssert.asserted) {
          ok = ASSERT_FAIL
        }
    }
    return ok
  }

  def checkNewToAfterDbState(beforeIds: Set[Any], afterIds: Set[Any], afterEntities: Map[Any, Any])(implicit report: FabutReport): Boolean = {

    var ok = ASSERTED

    val afterIdsCopy = afterIds.diff(beforeIds)
    afterIdsCopy.foreach {
      id =>
        {
          val entity = afterEntities(id)
          ok = ASSERT_FAIL
        }
    }
    return ok
  }

  def assertDbSnapshotWithAfterState(beforeIds: Set[Any], afterIds: Set[Any], beforeEntities: Map[Any, CopyAssert], afterEntities: Map[Any, Any])(implicit report: FabutReport): Boolean = {

    var ok = ASSERTED
    val beforeIdsCopy = beforeIds.diff(afterIds)
    beforeIdsCopy.foreach {
      id =>
        {
          if (!beforeEntities(id).asserted) {
            ok &= assertObjects(report, beforeEntities(id), afterEntities(id), Map())
          }
        }
    }
    return ok
  }

  def assertDbSnapshot(report: FabutReport): Boolean = {
    var ok = ASSERTED
    dbSnapshot.foreach {
      snapshotEntry =>
        {
          val afterEntities = getAfterEntities(snapshotEntry._1)
          val beforeIds = snapshotEntry._2.keySet.toSet
          val afterIds = afterEntities.keySet

          ok &= checkNotExistingInAfterDbState(beforeIds, afterIds, snapshotEntry._2.toMap)(report)
          ok &= checkNewToAfterDbState(beforeIds, afterIds, afterEntities)(report)
          ok &= assertDbSnapshotWithAfterState(beforeIds, afterIds, snapshotEntry._2.toMap, afterEntities)(report)
        }
    }
    return ok
  }

}