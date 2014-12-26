package eu.execom.fabut.report

import eu.execom.fabut.CommentType._
import eu.execom.fabut.pair.AssertPair

/**
 * Report builder used for creating clean and readable reports. Its point is to emphasize failed asserts so developer
 * can track them easy.
 */
case class FabutReportBuilder(newMessage: String = "") {

  val ARROW = "⇝"
  val NEW_LINE = "\n"
  val TAB = "\t\t"
  val COLON = ":"

  val builder: StringBuilder = new StringBuilder
  var assertDepth = 0

  def message(): String = builder.toString()

  if (!newMessage.isEmpty) {
    builder.append(NEW_LINE)
    builder.append(newMessage)
  }

  def increaseDepth(parent: String): Unit = {
    if (parent.isEmpty) builder.append(addIndentation + parent + COLON)
    assertDepth += 1
  }

  def decreaseDepth(): Unit = assertDepth -= 1

  def listDifferentSizeComment(propertyName: String, expectedSize: Int, actualSize: Int): Unit = {
    val comment = s"Expected size for list $propertyName is: $expectedSize, but was: $actualSize"
    addComment(comment, FAIL)
  }

  def mapDifferentSizeComment(propertyName: String, expectedSize: Int, actualSize: Int): Unit = {
    val comment = s"Expected size for map $propertyName is: $expectedSize, but was: $actualSize"
    addComment(comment, FAIL)
  }

  def mapMissingKeyInExpected(propertyName: Any): Unit = {
    val comment = s"There was no key in expected named: $propertyName"
    addComment(comment, FAIL)
  }

  def mapMissingKeyInActual(propertyName: Any): Unit = {
    val comment = s"There was no key in actual named: $propertyName"
    addComment(comment, FAIL)
  }

  def noPropertyForField(fieldName: String, field: Any): Unit = {
    val comment = s"There was no property for field:  $fieldName of class:  ${field.getClass.getSimpleName}, with value: $field"
    addComment(comment, FAIL)
  }

  def notNullProperty(fieldName: String, assertResult: Boolean): Unit = {
    if (assertResult) {
      val comment = s"$fieldName: expected not null property and field was not null"
      addComment(comment, SUCCESS)
    } else {
      val comment = s"$fieldName: expected not null property, but field was null"
      addComment(comment, SUCCESS)
    }
  }

  def nullProperty(fieldName: String, assertResult: Boolean) {
    if (assertResult) {
      val comment = s"$fieldName: expected null property and field was null"
      addComment(comment, SUCCESS)
    } else {
      val comment = s"$fieldName: expected null property, but field was not null"
      addComment(comment, FAIL)
    }
  }

  def reportIgnoreProperty(fieldName: String): Unit = {
    val comment = s"$fieldName: is ignored field"
    addComment(comment, SUCCESS)
  }

  def checkByReference(fieldName: String, objectInstance: Any, asserted: Boolean): Unit = {
    if (asserted) {
      val comment = s"Property:  ${objectInstance.getClass.getSimpleName} of class:  $fieldName has good reference."
      addComment(comment, SUCCESS)
    } else {
      val comment = s"Property:  ${objectInstance.getClass.getSimpleName} of class:  $fieldName has wrong reference."
      addComment(comment, FAIL)
    }
  }

  def ignoredType(assertPair: AssertPair): Unit = {
    val comment = s"Type  ${assertPair.expected.getClass.getSimpleName} is ignored type."
    addComment(comment, SUCCESS)
  }

  def assertingListElements(listName: String, index: Int): Unit = {
    val comment = s"Asserting object at index [$index] of list $listName."
    addComment(comment, COLLECTION)
  }

  def noEntityInSnapshot(entity: Any): Unit = {
    val comment = s"Entity $entity doesn't exist in DB any more but is not asserted in test."
    addComment(comment, FAIL)
  }

  def entityNotAssertedInAfterState(entity: Any): Unit = {
    val comment = s"Entity $entity is created in system after last snapshot but hasnt been asserted in test."
    addComment(comment, FAIL)
  }

  def nullReference(): Unit = {
    val comment = s"Object that was passed to assertObject was null, it must not be null!"
    addComment(comment, FAIL)
  }

  def asserted(pair: AssertPair, propertyName: String): Unit = {
    val comment = s"$propertyName: expected: ${pair.expected} and was: ${pair.actual}"
    addComment(comment, SUCCESS)
  }

  def assertFail(pair: AssertPair, propertyName: String): Unit = {
    val comment = s"$propertyName: expected: ${pair.expected}, but was: ${pair.actual}"
    addComment(comment, FAIL)
  }

  def idNull(clazz: Any): Unit = {
    val comment = s"Id of ${clazz.getClass.getSimpleName} cannot be null"
    addComment(comment, FAIL)
  }

  def notExistingInSnapshot(entity: Any): Unit = {
    val comment = s"Entity: $entity cannot be found in snapshot"
    addComment(comment, FAIL)
  }

  def notDeletedInRepositoy(entity: Any): Unit = {
    val comment = s"Entity: $entity was not deleted in repository"
    addComment(comment, FAIL)
  }

  def noCopy(entity: Any): Unit = {
    val comment = s"Entity: $entity cannot be copied into snapshot"
    addComment(comment, FAIL)
  }

  def excessExpectedMap(key: Any): Unit = {
    val comment = s"No match for expected key: $key"
    addComment(comment, FAIL)
  }

  def missingPropertyInClass(propertyName: String, className: String): Unit = {
    val comment = s"Missing property $propertyName for class $className"
    addComment(comment, FAIL)
  }

  def excessActualMap(key: Any): Unit = {
    val comment = s"No match for actual key: $key"
    addComment(comment, FAIL)
  }

  def assertingMapKey(key: Any): Unit = {
    val comment = s"Map key: $key"
    addComment(comment, COLLECTION)
  }

  def undefinedClassType(value: Any): Unit = {
    val comment = s"Undefined class type ${value.getClass.getSimpleName}, you must add it to predefined fabut types}"
    addComment(comment, FAIL)
  }

  def addComment(comment: String, commentType: CommentType): Unit = {
    val part = new StringBuilder(addIndentation())

    part.append(commentType)
    part.append(ARROW)
    part.append(comment)

    builder.append(part.toString())
  }

  def addIndentation(): String = {
    val part = new StringBuilder(builder.toString()).append(NEW_LINE)
    for (i <- 0 to assertDepth) part.append(TAB)
    builder.setLength(0)
    part.toString()
  }

  //TODO make successComment and fail comment
}

