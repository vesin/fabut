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
}

object FabusReport