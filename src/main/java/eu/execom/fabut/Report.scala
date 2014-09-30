package eu.execom.fabut

import eu.execom.fabut.enums.AssertType._

class FabutReport() {

  var _result = ASSERT_SUCCESS
  var _message: String = "Report:"

  def message: String = _message

  def message_=(newMessage: String) =
    _message = newMessage

  def result = _result

  def addResult(result: Value) {
    if (_result != result)
      _result = result
  }

  def addPropertiesExceptionMessage(path: String, actualValue: String, expectedValue: String) {
    message_=(message + "\n"
      + s"Value of property ${path} ::  is '${actualValue}', expected: '${expectedValue}'")
  }

  def addIsomorphicGraphExceptionMessage(actualDepth: Int) {
    message_=(message + "\n"
      + s"Recursive graphs are not isomorphic: actual object returns to node ${actualDepth}, " +
      s"which is not expected")
  }

  def addObjectNullExceptionMessage(o: String, path: String) {
    message_=(message + "\n"
      + { if (o == "A") "Actual " else "Expected " }
      + "object" + { if (path != "") s" with path ${path}" } + " is null")
  }

  def addListSizeExceptionMessage(path: String, actualListSize: Int, expectedListSize: Int) {
    message_=(message + "\n"
      + s"List sizes are different path: ${path}:: actual list size ${actualListSize}"
      + s", expected list size ${expectedListSize}")
  }

  def addListPropertyException(position: Int, actualPropertyValue: Any, expectedPropertyValue: Any) {
    message_=(message + "\n"
      + s"List element value on position $position is $actualPropertyValue, expected $expectedPropertyValue ")
  }

}

object FabusReport