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

object Fabut {

  var fabutAssert: FabutRepositoryAssert = new FabutRepositoryAssert((new AbstractFabutObjectAssertTest).asInstanceOf[IFabutTest])

  def beforeTest(testInstance: Any) {
    /**
     *  TODO1
     */
  }

  def afterTest {
    /**
     *  TODO2
     */
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

    val report = new FabutReport
    fabutAssert.assert(report, Nil, objectInstance, createExpectedPropertiesMap(properties))

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
        }

        fabutAssert.assert(report, expectedObject, actualObject, properties)
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
    properties.map { property => (property.getPath, property) } toMap
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
  def value(namePath: String, expectedValue: Any): IProperty = Property(namePath, expectedValue)

  def ignored(namePath: String): IgnoredProperty = IgnoredProperty(namePath)

  def isNull(namePath: String): NullProperty = NullProperty(namePath)

  def notNull(namePath: String): NotNullProperty = NotNullProperty(namePath)
}

