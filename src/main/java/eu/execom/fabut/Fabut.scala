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

object Fabut {

  val ASSERT_SUCCESS = true
  var assertType = UNSUPPORTED_ASSERT

  var fabutAssert: FabutRepositoryAssert = null //new FabutRepositoryAssert((new AbstractFabutRepositoryAssertTest).asInstanceOf[IFabutRepositoryTest])

  def beforeTest(testInstance: Any) {
    assertType = getAssertType(testInstance)
    assertType match {
      case OBJECT_ASSERT =>
        fabutAssert = new FabutRepositoryAssert(testInstance.asInstanceOf[IFabutTest], assertType)
      case REPOSITORY_ASSERT =>
        fabutAssert = new FabutRepositoryAssert(testInstance.asInstanceOf[IFabutRepositoryTest], assertType)
      case _ =>
        throw new IllegalArgumentException("This test must implement IFabutAssert or IRepositoryFabutAssert")
    }
  }

  def afterTest {

    var ok = true

    if (!fabutAssert.assertParameterSnapshot(new FabutReport)) {
      ok = false
    }

    if (assertType == REPOSITORY_ASSERT) {
      //      if()
    }
  }

  def takeSnapshot(parameters: Any*) = {
    checkValidInit
    if (assertType == UNSUPPORTED_ASSERT) {
      throw new IllegalArgumentException("Test must implement IRepositoryFabutAssert")
    }

    if (!fabutAssert.takeSnapshot(new FabutReport, parameters)) {
      throw new AssertionFailedError("TODO report.getMessage")
    }

  }

  def checkValidInit = {
    if (fabutAssert == null)
      throw new IllegalArgumentException("Fabut.beforeTest must be called before the test")
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

    // val changedProperties = createExpectedPropertiesMap(properties)
    val report = new FabutReport
    //    fabutAssert.assert(report, Nil, objectInstance, createExpectedPropertiesMap(properties))

    report.result match {
      case ASSERT_SUCCESS => ()
      case _ => throw new AssertionError(report.message)
    }
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

    val report = new FabutReport

    if (expectedObject == null)
      report.addObjectNullExceptionMessage("E", "")

    if (actualObject == null)
      report.addObjectNullExceptionMessage("A", "")

    if (actualObject != null && expectedObject != null) {

      if (actualObject.getClass.equals(expectedObject.getClass)) {

        var properties: Map[String, IProperty] = Map()

        if (propertiesList.nonEmpty) {
          properties = createExpectedPropertiesMap(propertiesList)
        } else {
          properties = Map()
        }

        //        fabutAssert.assertObjects(report, expectedObject, actualObject, properties)

      } else {
        report.addTypeMissmatchException(expectedObject, actualObject)
      }
    }

    report.result match {
      case ASSERT_SUCCESS => ()
      case _ => throw new AssertionError(report.message)
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
    Map()
    //    properties.map { property => (property.getPath, property) } toMap
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

