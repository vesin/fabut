package eu.execom.fabut

import eu.execom.fabut.enums.AssertType._

class FabutReport() {

  var _result = ASSERT_SUCCESS
  var _message: String = "\nReport:"

  def message: String = _message

  def message_=(newMessage: String) =
    _message = newMessage

  def result = if (message.size > 9) ASSERT_FAILED else ASSERT_SUCCESS

  def addPropertiesExceptionMessage(prefixMessage: String, path: String, actualValue: Any, expectedValue: Any) {
    message_=(message + "\n"
      + s"${prefixMessage} value of property ${path} ::  is '${actualValue}', expected: '${expectedValue}'")
  }

  def addIsomorphicGraphExceptionMessage(prefixMessage: String, actualDepth: Int) {
    message_=(message + "\n"
      + s"Recursive graphs are not isomorphic: actual object returns to node ${actualDepth}, " +
      s"which is not expected")
  }

  def addObjectNullExceptionMessage(prefixMessage: String, o: String, namePath: String) {
    message_=(message + "\n"
      + { if (o == "A") "Actual " else "Expected " }
      + "object" + { if (namePath != "") s" with path ${namePath}" } + " is null")
  }

  def addCollectionSizeExceptionMessage(prefixMessage: String, path: String, actualListSize: Int, expectedListSize: Int) {
    message_=(message + "\n"
      + s"Collection sizes are different path: ${path}:: actual collection size ${actualListSize}"
      + s", expected collection size ${expectedListSize}")
  }

  def addListPropertyException(prefixMessage: String, position: Int, actualPropertyValue: Any, expectedPropertyValue: Any) {
    message_=(message + "\n"
      + s"List element value on position $position is $actualPropertyValue, expected $expectedPropertyValue ")
  }

  def addMissingExpectedPropertyMessage(prefixMessage: String, propertyName: String) {
    message_=(message + "\n"
      + s"Missing expected property: ${propertyName}")
  }

  def addUnusedPropertyMessage(propertyName: String, propertyValue: Any) {
    message_=(message + "\n"
      + s"Unused property : ${propertyName} with value '${propertyValue}'")
  }

  def addKeyNotFoundInExpectedMapMessage(prefixMessage: String, elementName: String) {
    message_=(message + "\n"
      + s"${prefixMessage} key not found in expected map for key '${elementName}' ")
  }

  def addNonMatchingTypesMessage(prefixMessage: String, actualElementType: String, expectedElementType: String) {
    message_=(message + "\n"
      + s"${prefixMessage} types of values do not match :: actual ${actualElementType}, expected ${expectedElementType} ")
  }

  def addTypeMissmatchException(expectedObject: Any, actualObject: Any) {
    message_=(message + "\n"
      + s"Type mismatch. Actual object is ${actualObject.getClass} , expected ${expectedObject.getClass} ")
  }

  def addNullExpectedException(propertyName: String, propertyValue: Any) {
    message_=(message + "\n"
      + s"Null expected exception. Actual object ${propertyName} is ${propertyValue}  , expected null ")
  }
}

object FabusReport