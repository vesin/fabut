package eu.execom.fabut

import eu.execom.fabut.AssertType._
import eu.execom.fabut.property._
import eu.execom.fabut.report.FabutReportBuilder
import eu.execom.fabut.util.ConversionUtil._
import eu.execom.fabut.util.ReflectionUtil._
import junit.framework.AssertionFailedError

import scala.reflect.runtime.universe.Type

/**
 * Set of method for advanced asserting.
 */
trait Fabut {

  var assertType: AssertType = null
  var fabutAssert: FabutRepositoryAssert = null

  /**
   * Method used for initialization of db and Fabut
   * */
  def before():Unit

  /**
   * Method for after test stream close ups, rollbacks etc.
   * */
  def after():Unit

  /**
   * List of class types that will be treated as complex
   *
   * @return list of complex class typesS
   **/
  def complexTypes(): List[Type]

  /**
   * List of class types that Fabut will ignore while asserting
   *
   * @return list of ignored class types
   **/
  def ignoredTypes(): List[Type]

  /**
   * Custom implementation of assert function for certain class types
   **/
  def customAssertEquals(expectedObject: Any, actualObject: Any)

  /**
   * This method needs to be called in @Before method of a test in order for [@link Fabut] to work.
   *
   * @param testInstance
   * - the test instance
   */
  def beforeTest(testInstance: AnyRef): Unit = {
    assertType = getAssertType(testInstance)
    fabutAssert = assertType match {
      case OBJECT_ASSERT => new FabutRepositoryAssert(testInstance.asInstanceOf[Fabut], assertType)
      case REPOSITORY_ASSERT => new FabutRepositoryAssert(testInstance.asInstanceOf[FabutRepository], assertType)
      case UNSUPPORTED_ASSERT => throw new IllegalStateException("This test must implement IFabutAssert or IRepositoryFabutAssert")
      case _ => throw new IllegalStateException("Unsupported assert type: " + assertType)
    }
  }

  /**
   * This method needs to be called in @After method of a test in order for [@link Fabut] to work.
   */
  def afterTest(): Unit = {
    var ok = true
    val sb = new StringBuilder()

    val parameterReport = new FabutReportBuilder("Parameter snapshot assert")
    if (!fabutAssert.assertParameterSnapshot(parameterReport)) {
      sb.append(parameterReport.message())
      ok = false
    }

    val snapshotReport = new FabutReportBuilder("Repository snapshot assert")
    if (assertType == REPOSITORY_ASSERT) {
      if (!fabutAssert.assertDbSnapshot(snapshotReport)) {
        sb.append(snapshotReport.message())
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
  def takeSnapshot(parameters: Any*): Unit = {
    checkValidInit()

    if (assertType == UNSUPPORTED_ASSERT) {
      throw new IllegalArgumentException("Test must implement IRepositoryFabutAssert")
    }

    val report = new FabutReportBuilder
    if (!fabutAssert.takeSnapshot(parameters: _*)(report)) {
      throw new AssertionFailedError(report.message())
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
  def assertObject(objectInstance: Any, properties: IProperty*): Unit =
    assertObject(EMPTY_STRING, objectInstance, properties: _*)

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
  def assertObject(message: String, objectInstance: Any, properties: IProperty*): Unit = {
    checkValidInit()

    val changedProperties = createExpectedPropertiesMap(properties: _*)
    val report = new FabutReportBuilder
    if (!fabutAssert.assertObjectWithProperties(objectInstance, changedProperties)(report)) {
      throw new AssertionError(report.message())
    }
  }

  /**
   * Asserts two objects
   *
   * @param expectedObject
   * the expected object
   * @param actualObject
   * the actual object
   * @param expectedChanges
   * property difference between expected and actual
   *
   */
  def assertObjects(expectedObject: Any, actualObject: Any, expectedChanges: IProperty*): Unit =
    assertObjects(EMPTY_STRING, expectedObject, actualObject, expectedChanges: _*)

  /**
   * Asserts list of expected and array of actual objects.
   *
   * @param expected
   * the expected list
   * @param actuals
   * the actual array
   */
  def assertList(expected: List[_], actuals: Any*): Unit = {
    checkValidInit()
    assertObjects(EMPTY_STRING, expected, actuals.toList)
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
   * @param expectedChanges
   * property difference between expected and actual
   *
   */
  def assertObjects(message: String, expectedObject: Any, actualObject: Any, expectedChanges: IProperty*): Unit = {

    val report = new FabutReportBuilder()

    if (expectedObject == null) {
      report.nullReference()
    }

    if (actualObject == null) {
      report.nullReference()
    }

    if (actualObject != null && expectedObject != null) {

      val properties: Map[String, IProperty] = expectedChanges match {
        case changes if changes.nonEmpty => createExpectedPropertiesMap(changes: _*)
        case _ => Map()
      }

      if (!fabutAssert.assertObjects(expectedObject, actualObject, properties)(report)) {
        throw new AssertionFailedError(report.message())
      }
    }
  }

  /**
   * Turns Seq of properties to Map
   *
   * @param properties
   * Seq of properties
   *
   * @return properties map
   *
   */
  def createExpectedPropertiesMap(properties: IProperty*): Map[String, IProperty] = properties.map(property => (property.path(), property)).toMap

  /**
   * Asserts entity with one saved in snapshot.
   *
   * @param entity
   * the entity
   * @param expectedChanges
   * properties changed after the snapshot has been taken
   */
  def assertEntityWithSnapshot(entity: Any, expectedChanges: IProperty*) = {
    checkValidInit()
    checkIfEntity(entity)

    val report = new FabutReportBuilder
    val changedProperties = createExpectedPropertiesMap(expectedChanges: _*)
    if (!fabutAssert.assertEntityWithSnapshot(report, entity, changedProperties)) {
      throw new AssertionFailedError(report.message())
    }
  }

  /**
   * Marks object as asserted.
   *
   * @param entity
   * the entity
   */
  def markAsserted(entity: Any): Unit = {

    checkValidInit()
    checkIfEntity(entity)

    val report = new FabutReportBuilder
    val entityType = getClassType(entity, AssertableType.ENTITY_TYPE)
    if (!fabutAssert.markAsAsserted(report, entity, entityType)) {
      throw new AssertionFailedError(report.message())
    }
  }

  /**
   *
   * Assert entity as deleted. It will fail if entity can still be found in snapshot.
   *
   * @param entity
   * the entity
   */
  def assertEntityAsDeleted(entity: Any): Unit = {
    checkValidInit()
    checkIfEntity(entity)

    val report = new FabutReportBuilder
    if (!fabutAssert.assertEntityAsDeleted(report, entity)) {
      throw new AssertionFailedError(report.message())
    }

  }

  /**
   * Checks if Fabut repository or object assert is initialized
   */
  def checkValidInit(): Unit =
    if (fabutAssert == null)
      throw new IllegalArgumentException("Fabut.beforeTest must be called before the test")

  /**
   * Checks if specified object is entity.
   *
   * @param entity
   * the entity
   */
  def checkIfEntity(entity: Any): Unit = {
    checkIfRepositoryAssert()

    if (entity == null) {
      throw new NullPointerException("assertEntityWithSnapshot cannot take null entity!")
    }
    if (!getClassType(entity, AssertableType.ENTITY_TYPE).isDefined) {
      throw new IllegalStateException(entity.getClass.getSimpleName + " is not registered as entity type")
    }
  }

  /**
   * Checks if current test is repository test.
   */
  def checkIfRepositoryAssert(): Unit = {
    if (assertType != REPOSITORY_ASSERT) {
      throw new IllegalStateException("Test class must implement IRepositoryFabutAssert")
    }
  }

  /**
   * Ignores the entity.
   *
   * @param entity
   * the entity
   */
  def ignoreEntity(entity: Any): Unit = {
    checkValidInit()
    checkIfEntity(entity)

    val report = new FabutReportBuilder
    if (!fabutAssert.ignoreEntity(entity)(report)) {
      throw new AssertionFailedError(report.message())
    }
  }

  /**
   * Create [@link Property] with provided parameters.
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
   * Create [@link IgnoredProperty] with provided parameter.
   *
   * @param path
   * property path.
   * @return created object.
   */
  def ignored(path: String): IgnoredProperty = IgnoredProperty(path)

  /**
   * Create [@link IgnoredProperty] with provided parameters.
   *
   * @param paths
   * property path.
   * @return created objects.
   */
  def ignored(paths: Seq[String]): Seq[IgnoredProperty] = paths.map(path => IgnoredProperty(path)).toSeq


  /**
   * Create [@link NotNullProperty] with provided parameter.
   *
   * @param path
   * property path.
   * @return created object.
   */
  def notNull(path: String): NotNullProperty = NotNullProperty(path)

  /**
   * Create [@link NotNullProperty] with provided parameters.
   *
   * @param paths
   * property paths.
   * @return created objects.
   */
  def notNull(paths: Seq[String]): Seq[NotNullProperty] = paths.map(path => NotNullProperty(path)).toSeq


  /**
   * Create [@link NullProperty] with provided parameter.
   *
   * @param path
   * property path.
   * @return created object.
   */

  def isNull(path: String): NullProperty = NullProperty(path)

  /**
   * Create [@link NullProperty] with provided parameters.
   *
   * @param paths
   * property paths.
   * @return created objects.
   */
  def isNull(paths: Seq[String]): Seq[NullProperty] = paths.map(path => NullProperty(path)).toSeq
}