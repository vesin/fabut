package eu.execom.fabut

import eu.execom.fabut.enums.AssertType._
import eu.execom.fabut.enums.AssertableType
import eu.execom.fabut.property.{AbstractProperty, IgnoredProperty, NotNullProperty, NullProperty, Property}
import eu.execom.fabut.report.FabutReportBuilder
import eu.execom.fabut.util.ConversionUtil._
import eu.execom.fabut.util.ReflectionUtil._
import junit.framework.AssertionFailedError

import scala.collection.mutable.{Map => MutableMap}

/**
 * Set of method for advanced asserting.
 *
 */
object Fabut {

  val EMPTY_STRING = ""
  val ASSERT_SUCCESS = true
  var assertType = UNSUPPORTED_ASSERT
  var fabutAssert: FabutRepositoryAssert = null

  /**
   * This method needs to be called in @Before method of a test in order for {@link Fabut} to work.
   *
   * @param testInstance
   * the test instance
   */
  def beforeTest(testInstance: Any) {
    assertType = getAssertType(testInstance)

    fabutAssert = assertType match {
      case OBJECT_ASSERT => new FabutRepositoryAssert(testInstance.asInstanceOf[IFabutTest], assertType)
      case REPOSITORY_ASSERT => new FabutRepositoryAssert(testInstance.asInstanceOf[IFabutRepositoryTest], assertType)
      case UNSUPPORTED_ASSERT => throw new IllegalStateException("This test must implement IFabutAssert or IRepositoryFabutAssert")
      case _ => throw new IllegalStateException("Unsupported assert type: " + assertType)
    }
  }

  /**
   * This method needs to be called in @After method of a test in order for {@link Fabut} to work.
   */
  def afterTest {
    var ok = true
    val sb = new StringBuilder

    val parameterReport = new FabutReportBuilder("Parameter snapshot assert")
    if (!fabutAssert.assertParameterSnapshot(parameterReport)) {
      sb.append(parameterReport.message)
      ok = false
    }

    val snapshotReport = new FabutReportBuilder("Repository snapshot assert")
    if (assertType == REPOSITORY_ASSERT) {
      if (!fabutAssert.assertDbSnapshot(snapshotReport)) {
        sb.append(snapshotReport.message)
        ok = false
      }
    }

    if (!ok) {
      throw new AssertionFailedError(sb.toString())
    }
  }

  /**
   * Creates repository snapshot so it can be asserted with after state after the test execution.
   */
  def takeSnapshot(parameters: Any*) = {
    checkValidInit

    if (assertType == UNSUPPORTED_ASSERT) {
      throw new IllegalArgumentException("Test must implement IRepositoryFabutAssert")
    }

    val report = new FabutReportBuilder
    if (!fabutAssert.takeSnapshot(parameters: _*)(report)) {
      throw new AssertionFailedError(report.message)
    }

  }

  /**
   * Asserts object with expected properties.
   *
   * @param message
   * custom message to be added on top of the report
   * @param objectInstance
   * the object that needs to be asserted
   * @param properties
   * expected properties for asserting object
   */
  def assertObject(message: String, objectInstance: Any, properties: AbstractProperty*) {
    checkValidInit

    val changedProperties = createExpectedPropertiesMap(properties: _*)
    val report = new FabutReportBuilder
    if (!fabutAssert.assertObjectWithProperties(objectInstance, changedProperties)(report)) {
      throw new AssertionError(report.message)
    }
  }

  /**
   * Asserts object with expected properties
   *
   * @param objectInstance
   * the object that needs to be asserted
   * @param properties
   * expected properties for asserting object
   *
   */
  def assertObject(objectInstance: Any, properties: AbstractProperty*) {
    assertObject(EMPTY_STRING, objectInstance, properties: _*)
  }

  /**
   * Asserts two objects
   *
   * @param expectedObject
   * the expected object
   * @param actualObject
   * the actual object
   * @param propertiesList
   * property difference between expected and actual
   *
   */
  def assertObjects(expectedObject: Any, actualObject: Any, expectedChanges: AbstractProperty*) {
    assertObjects(EMPTY_STRING, expectedObject, actualObject, expectedChanges: _*)
  }

  /**
   * Asserts two objects
   *
   * @param message
   * custom message to be added on top of the report
   * @param expectedObject
   * the expected object
   * @param actualObject
   * the actual object
   * @param propertiesList
   * property difference between expected and actual
   *
   */
  def assertObjects(message: String, expectedObject: Any, actualObject: Any, expectedChanges: AbstractProperty*) {

    val report = new FabutReportBuilder

    if (expectedObject == null)
      report.nullReference

    if (actualObject == null)
      report.nullReference

    if (actualObject != null && expectedObject != null) {

      val properties: Map[String, AbstractProperty] = if (expectedChanges.nonEmpty) {
        createExpectedPropertiesMap(expectedChanges: _*)
      } else {
        Map()
      }

      if (!fabutAssert.assertObjects(expectedObject, actualObject, properties)(report)) {
        throw new AssertionFailedError(report.message)
      }

    }
  }

  /**
   * Asserts list of expected and array of actual objects.
   *
   * @param expected
   * the expected list
   * @param actuals
   * the actual array
   */
  def assertList(expected: List[_], actuals: Any*) {
    checkValidInit

    val actualAsList = actuals.toList
    assertObjects(EMPTY_STRING, expected, actualAsList)
  }

  /**
   * Asserts entity with one saved in snapshot.
   *
   * @param entity
   * the entity
   * @param expectedChanges
   * properties changed after the snapshot has been taken
   */
  def assertEntityWithSnapshot(entity: Any, expectedChanges: AbstractProperty*) = {
    checkValidInit
    checkIfEntity(entity)

    val report = new FabutReportBuilder
    val changedProperties = createExpectedPropertiesMap(expectedChanges: _*)
    if (!fabutAssert.assertEntityWithSnapshot(report, entity, changedProperties)) {
      throw new AssertionFailedError(report.message)
    }
  }

  /**
   * Marks object as asserted.
   *
   * @param entity
   * the entity
   */
  def markAsserted(entity: Any) {

    checkValidInit
    checkIfEntity(entity)

    val report = new FabutReportBuilder
    val entityType = getObjectType(entity, AssertableType.ENTITY_TYPE)
    if (!fabutAssert.markAsAsserted(report, entity, entityType)) {
      throw new AssertionFailedError(report.message)
    }
  }

  /**
   *
   * Assert entity as deleted. It will fail if entity can still be found in snapshot.
   *
   * @param entity
   * the entity
   */
  def assertEntityAsDeleted(entity: Any) = {
    checkValidInit
    checkIfEntity(entity)

    val report = new FabutReportBuilder
    if (!fabutAssert.assertEntityAsDeleted(report, entity)) {
      throw new AssertionFailedError(report.message)
    }

  }

  /**
   * Ignores the entity.
   *
   * @param entity
   * the entity
   */
  def ignoreEntity(entity: Any) = {
    checkValidInit
    checkIfEntity(entity)

    val report = new FabutReportBuilder
    if (!fabutAssert.ignoreEntity(entity)(report)) {
      throw new AssertionFailedError(report.message)
    }
  }

  /**
   * Checks if specified object is entity.
   *
   * @param entity
   * the entity
   */
  def checkIfEntity(entity: Any) = {
    checkIfRepositoryAssert

    if (entity == null) {
      throw new NullPointerException("assertEntityWithSnapshot cannot take null entity!")
    }
    if (!getObjectType(entity, AssertableType.ENTITY_TYPE).isDefined) {
      throw new IllegalStateException(entity.getClass.getSimpleName + " is not registered as entity type")
    }
  }

  /**
   * Checks if current test is repository test.
   */
  def checkIfRepositoryAssert = {
    if (assertType != REPOSITORY_ASSERT) {
      throw new IllegalStateException("Test class must implement IRepositoryFabutAssert")
    }
  }

  /**
   * Checks if Fabut repository or object assert is initialized
   */
  def checkValidInit = {
    if (fabutAssert == null)
      throw new IllegalArgumentException("Fabut.beforeTest must be called before the test")
  }

  /**
   * Turns Seq of properties to Map
   *
   * @param properties
   * Seq of properties
   *
   * @preturn properties map
   *
   */
  def createExpectedPropertiesMap(properties: AbstractProperty*): Map[String, AbstractProperty] = {
    properties.map {
      case property: NullProperty => (property.path, property)
      case property: IgnoredProperty => (property.path, property)
      case property: NotNullProperty => (property.path, property)
      case property: Property => (property.path, property)
    } toMap
  }

  /**
   * Create {@link Property} with provided parameters.
   *
   * @param path
   * property path.
   * @param expectedValue
   * expected values
   * @return created object.
   *
   */
  def value(path: String, expectedValue: Any): Property = Property(path, expectedValue)

  /**
   * Create {@link IgnoredProperty} with provided parameter.
   *
   * @param path
   * property path.
   * @return created object.
   */
  def ignored(namePath: String): IgnoredProperty = IgnoredProperty(namePath)

  /**
   * Create {@link IgnoredProperty} with provided parameters.
   *
   * @param paths
   * property path.
   * @return created objects.
   */
  def ignored(paths: Seq[String]): Seq[IgnoredProperty] = {
    paths.map(path => IgnoredProperty(path)).toSeq
  }

  /**
   * Create {@link NotNullProperty} with provided parameter.
   *
   * @param path
   * property path.
   * @return created object.
   */
  def notNull(path: String): NotNullProperty = NotNullProperty(path)

  /**
   * Create {@link NotNullProperty} with provided parameters.
   *
   * @param paths
   * property paths.
   * @return created objects.
   */
  def notNull(paths: Seq[String]): Seq[NotNullProperty] = {
    paths.map(path => NotNullProperty(path)).toSeq
  }

  /**
   * Create {@link NullProperty} with provided parameter.
   *
   * @param path
   * property path.
   * @return created object.
   */

  def isNull(path: String): NullProperty = NullProperty(path)

  /**
   * Create {@link NullProperty} with provided parameters.
   *
   * @param paths
   * property paths.
   * @return created objects.
   */
  def isNull(paths: Seq[String]): Seq[NullProperty] = {
    paths.map(path => NullProperty(path)).toSeq
  }
}

