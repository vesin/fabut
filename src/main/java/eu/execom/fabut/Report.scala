package eu.execom.fabut

import eu.execom.fabut.enums.AssertType._

class FabutReport() {

  var _result = ASSERT_SUCCESS
  var _message: String = "\nReport:"

  def message: String = _message

  def message_=(newMessage: String) =
    _message = newMessage

  def result = if (message.size > 9) ASSERT_FAILED else ASSERT_SUCCESS

  def addPropertiesExceptionMessage(path: String, actualValue: Any, expectedValue: Any) {
    message_=(message + "\n"
      + s" value of property ${path} ::  is '${actualValue}', expected: '${expectedValue}'")
  }

  def addIsomorphicGraphExceptionMessage(actualDepth: Int) {
    message_=(message + "\n"
      + s"Recursive graphs are not isomorphic: actual object returns to node ${actualDepth}, " +
      s"which is not expected")
  }

  def addObjectNullExceptionMessage(o: String, namePath: String) {
    message_=(message + "\n"
      + { if (o == "A") "Actual " else "Expected " }
      + "object" + { if (namePath != "") s" with path ${namePath}" } + " is null")
  }

  def addCollectionSizeExceptionMessage(path: String, actualListSize: Int, expectedListSize: Int) {
    message_=(message + "\n"
      + s"Collection sizes are different path: ${path}:: actual collection size ${actualListSize}"
      + s", expected collection size ${expectedListSize}")
  }

  def addListPropertyException(position: Int, actualPropertyValue: Any, expectedPropertyValue: Any) {
    message_=(message + "\n"
      + s"List element value on position $position is $actualPropertyValue, expected $expectedPropertyValue ")
  }

  def addMissingExpectedPropertyMessage(propertyName: String) {
    message_=(message + "\n"
      + s"Missing expected property: ${propertyName}")
  }

  def addUnusedPropertyMessage(propertyName: String, propertyValue: Any) {
    message_=(message + "\n"
      + s"Unused property : ${propertyName} with value '${propertyValue}'")
  }

  def addKeyNotFoundInExpectedMapMessage(elementName: String) {
    message_=(message + "\n"
      + s"key not found in expected map for key '${elementName}' ")
  }

  def addNonMatchingTypesMessage(actualElementType: String, expectedElementType: String) {
    message_=(message + "\n"
      + s"types of values do not match :: actual ${actualElementType}, expected ${expectedElementType} ")
  }

  def addTypeMissmatchException(expectedObject: Any, actualObject: Any) {
    message_=(message + "\n"
      + s"Type mismatch. Actual object is ${actualObject.getClass} , expected ${expectedObject.getClass} ")
  }

  def addNullExpectedException(propertyName: String, propertyValue: Any) {
    message_=(message + "\n"
      + s"Null expected exception. Actual object ${propertyName} is ${propertyValue}  , expected null ")
  }

  def addNotNullExpectedException(propertyName: String, propertyValue: Any) {
    message_=(message + "\n"
      + s"Not null expected exception. Actual object ${propertyName} is ${propertyValue}  , expected not to be null")
  }
}

object FabusReport