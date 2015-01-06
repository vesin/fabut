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
  val TAB = "\t"
  val COLON = ":"

  val builder: StringBuilder = new StringBuilder
  var assertDepth = 0

  def message(): String = builder.toString()

  if (!newMessage.isEmpty) {
    builder.append(NEW_LINE)
    builder.append(newMessage)
  }

  def increaseDepth(parent: String): Unit = {
    if (!parent.isEmpty) builder.append(addIndentation + parent.trim + COLON)
    assertDepth += 1
  }

  def decreaseDepth(): Unit = assertDepth -= 1

  def listDifferentSizeComment(propertyName: String, expectedSize: Int, actualSize: Int): Unit = {
    val comment = s"Expected size for list $propertyName is: $expectedSize, but was: $actualSize"
    failComment(comment)
  }

  def mapDifferentSizeComment(propertyName: String, expectedSize: Int, actualSize: Int): Unit = {
    val comment = s"Expected size for map $propertyName is: $expectedSize, but was: $actualSize"
    failComment(comment)
  }

  def mapMissingKeyInExpected(propertyName: Any): Unit = {
    val comment = s"There was no key in expected named: $propertyName"
    failComment(comment)
  }

  def mapMissingKeyInActual(propertyName: Any): Unit = {
    val comment = s"There was no key in actual named: $propertyName"
    failComment(comment)
  }

  def noPropertyForField(fieldName: String, field: Any): Unit = {
    val comment = s"There was no property for field:  $fieldName of class:  ${field.getClass.getSimpleName}, with value: $field"
    failComment(comment)
  }

  def notNullProperty(fieldName: String, assertResult: Boolean): Unit = {
    if (assertResult) {
      val comment = s"$fieldName: expected not null property and field was not null"
      successComment(comment)
    } else {
      val comment = s"$fieldName: expected not null property, but field was null"
      successComment(comment)
    }
  }

  def nullProperty(fieldName: String, assertResult: Boolean) {
    if (assertResult) {
      val comment = s"$fieldName: expected null property and field was null"
      successComment(comment)
    } else {
      val comment = s"$fieldName: expected null property, but field was not null"
      failComment(comment)
    }
  }

  def reportIgnoreProperty(fieldName: String): Unit = {
    val comment = s"$fieldName: is ignored field"
    successComment(comment)
  }

  def checkByReference(fieldName: String, objectInstance: Any, asserted: Boolean): Unit = {
    if (asserted) {
      val comment = s"Property:  ${objectInstance.getClass.getSimpleName} of class:  $fieldName has good reference."
      successComment(comment)
    } else {
      val comment = s"Property:  ${objectInstance.getClass.getSimpleName} of class:  $fieldName has wrong reference."
      failComment(comment)
    }
  }

  def ignoredType(assertPair: AssertPair): Unit = {
    val comment = s"Type  ${assertPair.expected.getClass.getSimpleName} is ignored type."
    successComment(comment)
  }

  def assertingListElements(listName: String, index: Int): Unit = {
    val comment = s"Asserting object at index [$index] of list $listName."
    successComment(comment, COLLECTION)
  }

  def successComment(comment: String, commentType: CommentType*): Unit = {
    val part = new StringBuilder(addIndentation())

    if (commentType.nonEmpty) {
      part.append(commentType.head)
    } else {
      part.append(SUCCESS)
    }

    part.append(ARROW)
    part.append(comment)

    builder.append(part.toString())
  }

  def noEntityInSnapshot(entity: Any): Unit = {
    val comment = s"Entity $entity doesn't exist in DB any more but is not asserted in test."
    failComment(comment)
  }

  def entityNotAssertedInAfterState(entity: Any): Unit = {
    val comment = s"Entity $entity is created in system after last snapshot but hasnt been asserted in test."
    failComment(comment)
  }

  def nullReference(): Unit = {
    val comment = s"Object that was passed to assertObject was null, it must not be null!"
    failComment(comment)
  }

  def failComment(comment: String): Unit = {
    val part = new StringBuilder(addIndentation())

    part.append(FAIL)
    part.append(ARROW)
    part.append(comment)

    builder.append(part.toString())
  }

  def addIndentation(): String = {
    val part = new StringBuilder(builder.toString()).append(NEW_LINE)
    for (i <- 0 to assertDepth - 1) part.append(TAB)
    builder.setLength(0)
    part.toString()
  }

  def asserted(pair: AssertPair, propertyName: String): Unit = {
    val comment = s"$propertyName: expected: ${pair.expected} and was: ${pair.actual}"
    successComment(comment)
  }

  def assertFail(pair: AssertPair, propertyName: String): Unit = {
    val comment = s"$propertyName: expected: ${pair.expected}, but was: ${pair.actual}"
    failComment(comment)
  }

  def idNull(clazz: Any): Unit = {
    val comment = s"Id of ${clazz.getClass.getSimpleName} cannot be null"
    failComment(comment)
  }

  def notExistingInSnapshot(entity: Any): Unit = {
    val comment = s"Entity: $entity cannot be found in snapshot"
    failComment(comment)
  }

  def notDeletedInRepositoy(entity: Any): Unit = {
    val comment = s"Entity: $entity was not deleted in repository"
    failComment(comment)
  }

  def noCopy(entity: Any): Unit = {
    val comment = s"Entity: $entity cannot be copied into snapshot"
    failComment(comment)
  }

  def excessExpectedMap(key: Any): Unit = {
    val comment = s"No match for expected key: $key"
    failComment(comment)
  }

  def missingPropertyInClass(propertyName: String, className: String): Unit = {
    val comment = s"Missing property $propertyName for class $className"
    failComment(comment)
  }

  def excessActualMap(key: Any): Unit = {
    val comment = s"No match for actual key: $key"
    failComment(comment)
  }

  def assertingMapKey(key: Any): Unit = {
    val comment = s"Map key: $key"
    successComment(comment, COLLECTION)
  }

  def undefinedClassType(value: Any): Unit = {
    val comment = s"Undefined class type ${value.getClass.getSimpleName}, you must add it to predefined fabut types}"
    failComment(comment)
  }
}

