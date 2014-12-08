package eu.execom.fabut

import eu.execom.fabut.enums.AssertType._
import eu.execom.fabut.property.Property
import eu.execom.fabut.property.IProperty
import scala.collection.mutable.{ Map => MutableMap }
import eu.execom.fabut.property.IProperty
import eu.execom.fabut.property.IgnoredProperty
import eu.execom.fabut.property.NullProperty
import eu.execom.fabut.property.NotNullProperty
import eu.execom.fabut.property.NotNullProperty
import eu.execom.fabut.util.ReflectionUtil._
import eu.execom.fabut.property.IgnoredProperty
import eu.execom.fabut.property.NullProperty
import eu.execom.fabut.property.NotNullProperty
import eu.execom.fabut.util.ConversionUtil._
import eu.execom.fabut.enums.AssertType._
import junit.framework.AssertionFailedError
import eu.execom.fabut.enums.AssertableType
import eu.execom.fabut.property.NullProperty
import eu.execom.fabut.property.IgnoredProperty
import eu.execom.fabut.property.NotNullProperty

object Fabut {

  val ASSERT_SUCCESS = true
  var assertType = UNSUPPORTED_ASSERT

  var fabutAssert: FabutRepositoryAssert = null

  def beforeTest(testInstance: Any) {

    assertType = getAssertType(testInstance)

    assertType match {
      case OBJECT_ASSERT =>
        fabutAssert = new FabutRepositoryAssert(testInstance.asInstanceOf[IFabutTest], assertType)
      case REPOSITORY_ASSERT =>
        fabutAssert = new FabutRepositoryAssert(testInstance.asInstanceOf[IFabutRepositoryTest], assertType)
      case UNSUPPORTED_ASSERT =>
        throw new IllegalStateException("This test must implement IFabutAssert or IRepositoryFabutAssert")
      case _ =>
        throw new IllegalStateException("Unsupported assert type: " + assertType)
    }
  }

  def afterTest {

    var ok = true

    val sb = new StringBuilder

    val parameterReport = new FabutReportBuilder("Parameter snapshot assert")
    if (!fabutAssert.assertParameterSnapshot(parameterReport)) {
      ok = false
      sb.append(parameterReport.message)
    }

    val snapshotReport = new FabutReportBuilder("Repository snapshot assert")
    if (assertType == REPOSITORY_ASSERT) {
      if (!fabutAssert.assertDbSnapshot(snapshotReport)) {
        ok = false
        sb.append(snapshotReport.message)
      }
    }

    if (!ok) {
      throw new AssertionFailedError(sb.toString)
    }
  }

  def takeSnapshot(parameters: Any*) = {
    checkValidInit

    if (assertType == UNSUPPORTED_ASSERT) {
      throw new IllegalArgumentException("Test must implement IRepositoryFabutAssert")
    }

    val report = new FabutReportBuilder
    if (!fabutAssert.takeSnapshot(parameters)(report)) {
      throw new AssertionFailedError(report.message)
    }

  }

  /**
   *  Asserts object with expected properties
   *
   *  @param objectInstance
   *  		the object that needs to be asserted
   *  @param properties
   *  		expected properties for asserting object
   *
   */
  def assertObject(objectInstance: Any, properties: IProperty*) {

    checkValidInit

    val changedProperties = createExpectedPropertiesMap(properties)
    val report = new FabutReportBuilder
    if (!fabutAssert.assertObjectWithProperties(objectInstance, changedProperties)(report)) {
      throw new AssertionError(report.message)
    }
  }

  def checkValidInit = {
    if (fabutAssert == null)
      throw new IllegalArgumentException("Fabut.beforeTest must be called before the test")
  }

  /**
   *  Asserts two objects
   *
   *  @param expectedObject
   *  		the expected object
   *  @param actualObject
   *  		the actual object
   *  @param propertiesList
   *  		elements of this list are properties from the expected object
   *        which should be asserted with actual corresponding pairs instead of the properties from expected object
   *
   */
  def assertObjects(expectedObject: Any, actualObject: Any, propertiesList: IProperty*) {

    val report = new FabutReportBuilder

    if (expectedObject == null)
      report.nullReference

    if (actualObject == null)
      report.nullReference

    if (actualObject != null && expectedObject != null) {

      var properties: Map[String, IProperty] = Map()

      if (propertiesList.nonEmpty) {
        properties = createExpectedPropertiesMap(propertiesList)
      } else {
        properties = Map()
      }

      if (!fabutAssert.assertObjects(expectedObject, actualObject, properties)(report)) {
        throw new AssertionFailedError(report.message)
      }

    }

  }

  def markAsserted(entity: Any) {

    checkValidInit
    checkIfEntity(entity)

    val report = new FabutReportBuilder
    if (!fabutAssert.markAsAsserted(report, entity, getObjectType(entity, AssertableType.ENTITY_TYPE))) {
      throw new AssertionFailedError(report.message)
    }
  }

  def checkIfRepositoryAssert = {
    if (assertType != REPOSITORY_ASSERT) {
      throw new IllegalStateException("Test class must implement IRepositoryFabutAssert")
    }
  }

  def checkIfEntity(entity: Any) = {
    checkIfRepositoryAssert
    if (entity == null) {
      throw new NullPointerException("assertEntityWithSnapshot cannot take null entity!")
    }
    if (!getObjectType(entity, AssertableType.ENTITY_TYPE).isDefined) {
      throw new IllegalStateException(entity.getClass.getSimpleName + " is not registered as entity type")
    }
  }

  def assertEntityAsDeleted(entity: Any) = {
    checkValidInit
    checkIfEntity(entity)

    val report = new FabutReportBuilder
    if (!fabutAssert.assertEntityAsDeleted(report, entity)) {
      throw new AssertionFailedError(report.message)
    }

  }

  def assertEntityWithSnapshot(entity: Any, propertiesList: IProperty*) = {
    checkValidInit
    checkIfEntity(entity)

    val report = new FabutReportBuilder
    val changedProperties = createExpectedPropertiesMap(propertiesList)
    if (!fabutAssert.assertEntityWithSnapshot(report, entity, changedProperties)) {
      throw new AssertionFailedError(report.message)
    }
  }

  def ignoreEntity(entity: Any) = {
    checkValidInit
    checkIfEntity(entity)

    val report = new FabutReportBuilder
    if (!fabutAssert.ignoreEntity(entity)(report)) {
      throw new AssertionFailedError(report.message)
    }
  }

  /**
   *  Turns Seq of properties to Map
   *
   *  @param properties
   *  		Seq of properties
   *
   *  @preturn properties map
   *
   */
  def createExpectedPropertiesMap(properties: Seq[IProperty]): Map[String, IProperty] = {
    properties.map {
      case property: NullProperty => (property.path, property)
      case property: IgnoredProperty => (property.path, property)
      case property: NotNullProperty => (property.path, property)
      case property: Property => (property.path, property)
    } toMap
  }

  /**
   *  Creates a property with provided parameters
   *
   *  @param namePath
   *  		property path in the object
   *  @param expectedValue
   *  		expected value
   *
   *  @return property object
   *
   */
  def value(namePath: String, expectedValue: Any): Property = Property(namePath, expectedValue)

  def ignored(namePath: String): IgnoredProperty = IgnoredProperty(namePath)

  def isNull(namePath: String): NullProperty = NullProperty(namePath)

  def notNull(namePath: String): NotNullProperty = NotNullProperty(namePath)

  def ignored(paths: Seq[String]): Seq[IgnoredProperty] = {
    paths.map(path => IgnoredProperty(path)).toSeq
  }

  def isNull(paths: Seq[String]): Seq[NullProperty] = {
    paths.map(path => NullProperty(path)).toSeq
  }

  def notNull(paths: Seq[String]): Seq[NotNullProperty] = {
    paths.map(path => NotNullProperty(path)).toSeq
  }

}

