package eu.execom.fabut.util

import scala.reflect.runtime.universe._
import scala.collection.mutable.{ Map => MutableMap }
import scala.reflect.ClassTag
import eu.execom.fabut.model.ObjectWithSimpleProperties
import sun.reflect.generics.tree.TypeSignature
import eu.execom.fabut.FabutReport
import eu.execom.fabut.enums.AssertType._
import eu.execom.fabut.enums.AssertableType._
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.model.ObjectWithSimpleProperties
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.exception.TypeMissingException
import eu.execom.fabut.FabutRepositoryAssert
import eu.execom.fabut.FabutObjectAssert
import eu.execom.fabut.property.Property
import eu.execom.fabut.property.IProperty
import eu.execom.fabut.property.IgnoredProperty

object ReflectionUtil {

  lazy val classLoaderMirror = runtimeMirror(getClass.getClassLoader)
  lazy val SETTER_POSTFIX = "_$eq"

  var fabutAssert: FabutRepositoryAssert = null;

  def setFabutAssert(fabutAssert: FabutRepositoryAssert) {
    this.fabutAssert = fabutAssert
  }

  def getClassLoaderMirror() = classLoaderMirror

  /**
   * Returns a type of given value
   *
   * @param value
   * @return
   * 		one of assertable types
   */
  def getValueType(value: Any) = {

    if (value.isInstanceOf[List[_]])
      SCALA_LIST_TYPE
    else if (value.isInstanceOf[Map[_, _]])
      SCALA_MAP_TYPE
    else if (getObjectType(value, COMPLEX_TYPE) != None)
      COMPLEX_TYPE
    else if (getObjectType(value, ENTITY_TYPE) != None)
      ENTITY_TYPE
    else if (getObjectType(value, IGNORED_TYPE) != None)
      IGNORED_TYPE
    else
      PRIMITIVE_TYPE
  }

  /**
   * Returns object type from 'types' for given value
   *
   *  @param objectValue
   *  	object for which we check the type
   *  @param assertableType
   *  	the list of predefined objects from map of types where we should search for value
   *  @return
   *  	the specific type of object
   */

  def getObjectType(objectValue: Any, assertableType: AssertableType): Option[Type] = {

    if (objectValue == null) {
      return None
    }

    try {
      fabutAssert.getTypes(assertableType).find(
        typeName => (typeName.toString == objectValue.getClass.getCanonicalName)) match {
          case n: Some[Type] => Some(n.get)
          case _ => None
        }
    } catch {
      case e: NoSuchElementException =>
        println("TODO 1: Add message for forgeting to add to complex or any list types a type of class ")
        None
    }

  }

  /**
   * Gets all the properties of given object that need to be asserted.
   *
   * @param objectInstance
   * 		object instance of fields
   * @param pathName
   * @param objectTypeOption
   * 	optioned object instance type
   *
   * @return
   *    optioned map that has all the properties of object instance
   *
   * @throws ScalaReflectionException
   *
   */
  def getObjectProperties(objectInstance: Any, pathName: String, objectTypeOption: Option[Type]): Map[String, Property] = {

    var result: Map[String, Property] = Map()

    if (objectTypeOption == None) {
      return result
    }

    val objectType = objectTypeOption.get

    val classMirror = objectType.typeSymbol.asClass
    val im = classLoaderMirror.reflect(objectInstance)

    val isField = (sym: TermSymbol) => {
      sym.isGetter
    }
    try {

      result ++= objectType.members.collect {
        case sym:
          TermSymbol if isField(sym) => {
          val name = sym.toString.split(' ').last
          val value = reflectField(name, objectType, im)
          (pathName + name, Property(pathName + name, value.get))
        }
      }.toMap
    } catch {
      case t: ScalaReflectionException => println("//TODO")
    }
    result
  }

  /**
   * Used for @method getFieldsForAssertFromMethods
   *
   * returns value from a field name and instance mirror of class
   *
   * @param propertyName
   * 		property name that needs to be asserted
   * @param im
   * 		instance mirror of object instance
   * @param objectType
   * 	type of object instance
   *
   * @return
   * 		property value
   * @throws ScalaReflectionException
   */
  def reflectField(propertyName: String, objectType: Type, instanceMirror: InstanceMirror) = {

    try {
      val fieldSymbol = objectType.member(TermName(propertyName)).asMethod
      val field = instanceMirror.reflectMethod(fieldSymbol)
      Some(field())
    } catch {
      case t: ScalaReflectionException => None
    }
  }

  /**
   * Method reflects object inside object, used in checking if graphs
   * are isomorphic and for reflecting the expected object until the depth
   * where actual and expected value should be asserted
   *
   *  @param objectName
   *  @param expectedObject
   *  @param expectedObjectType
   *  		type of expected object
   *
   *  @return
   *  		reflected object
   *  @throws ScalaReflectionException
   */
  def reflectObject(objectName: String, expectedObject: Any, expectedObjectTypeOption: Option[Type]): Option[Any] = {

    if (expectedObjectTypeOption == None) return None

    val expectedObjectType = expectedObjectTypeOption.get

    try {
      val classMirror = expectedObjectType.typeSymbol.asClass
      val im = classLoaderMirror.reflect(expectedObject)
      val valueOption = reflectField(objectName, expectedObjectType, im)
      if (valueOption != None) {
        Some(valueOption.get)
      } else {
        None
      }
    } catch {
      case t: ScalaReflectionException => None
    }
  }

  /**
   * Gets field value from given object for given field name via getter.
   *
   * @param propertyName
   * @param objectInstance
   * @param expectedObjectType
   *
   * @return
   * 	property value or None if it throws exception
   *
   * @throws ScalaReflectionException
   */
  def getFieldValueFromGetter(fieldName: String, objectInstance: Any, expectedObjectTypeOption: Option[Type]): Option[Any] = {

    if (expectedObjectTypeOption == None) return None

    val expectedObjectType = expectedObjectTypeOption.get
    val classMirror = expectedObjectType.typeSymbol.asClass
    try {
      val instanceMirror = classLoaderMirror.reflect(objectInstance)

      val fieldSymbol = expectedObjectType.decl(TermName(fieldName)).asMethod
      val field = instanceMirror.reflectMethod(fieldSymbol)
      Some(field())
    } catch {
      case t: ScalaReflectionException => None
    }
  }

  /**
   *
   * Sets the field value from given object for given field name via setter.
   *
   * @param fieldName
   * @param newFieldValue
   * @param objectInstance
   * @param objectType
   *
   * @return
   * 	field option value or none if it throws exception
   *
   * @throws ScalaReflectionException
   * @throws IllegalArgumentException
   */
  def setField(fieldName: String, newFieldValue: Any, objectInstance: Any, objectType: Type) {

    var methodName = fieldName + SETTER_POSTFIX

    val im = classLoaderMirror.reflect(objectInstance)
    val mSymbol = objectType.decl(TermName(methodName)).asMethod
    val methodMirror = im.reflectMethod(mSymbol)

    try {
      methodMirror(newFieldValue)
    } catch {
      case e: IllegalArgumentException => println(e.getMessage())
      case e: ScalaReflectionException => println(e.getMessage())
    }

  }

  /**
   * Method is used to assert each primitive element from map on given depth
   * with its corresponding element from expected object, returns report that is
   * fulfilled with messages if any of the actual primitive value is not as
   * expected value
   *
   * @param prefixMessage
   * 	prefix message for report that is used if the depth entered a collection
   * @param pathcut
   * 	number of chars we cut from pathname so we could have a name of object
   * @param primitiveProperties
   * 	map that contains name and value of each primitive on given depth
   * @param expectedObject
   * @param expectedObjectType
   * @param expectedObjectPropertiesList
   * @param report
   * 	fabut report
   *
   * @return
   * 	report with added fail messages if assert fail occurs
   * @throws ScalaReflectionException
   */
  def reflectPrimitiveProperties(pathcut: Int, primitiveProperties: Map[String, IProperty], expectedObject: Any, expectedObjectTypeOption: Option[Type], expectedObjectPropertiesList: Map[String, IProperty], report: FabutReport): Map[String, IProperty] = {

    var uncheckedExpectedObjectProperties = expectedObjectPropertiesList

    if (expectedObjectTypeOption == None) {
      report.addObjectNullExceptionMessage("E", "")
      return uncheckedExpectedObjectProperties
    }

    val expectedObjectType = expectedObjectTypeOption.get
    val classMirror = expectedObjectType.typeSymbol.asClass
    val instanceMirror = classLoaderMirror.reflect(expectedObject)

    var fieldValue: Any = null
    var property: IProperty = null

    primitiveProperties.values.foreach {
      property =>
        try {
          fieldValue = expectedObjectPropertiesList(property.getNamePath).asInstanceOf[Property].value
          uncheckedExpectedObjectProperties -= property.getNamePath
        } catch {
          case t: NoSuchElementException =>
            try {
              val fieldSymbol = expectedObjectType.decl(TermName(property.getNamePath.substring(pathcut))).asMethod
              val field = instanceMirror.reflectMethod(fieldSymbol)
              fieldValue = field()
            } catch {
              case t: ScalaReflectionException => None
            }
        }

        try {
          fabutAssert.fabutTest.customAssertEquals(fieldValue, property.asInstanceOf[Property].value)
        } catch {
          case e: AssertionError => {
            report.addPropertiesExceptionMessage(property.getNamePath, property.asInstanceOf[Property].value, fieldValue)
          }
        }
    }
    uncheckedExpectedObjectProperties

  }

  /**
   * Creates copy of given object with empty properties
   *
   * @param objectInstance
   * @param objectTypeOption
   *
   */
  def createEmptyCopy(objectInstance: Any, objectTypeOption: Option[Type]): Any = {

    if (objectTypeOption == None) return Nil

    try {

      val objectType = objectTypeOption.get
      val classSymbol = objectType.typeSymbol.asClass
      val classMirror = classLoaderMirror.reflectClass(classSymbol)

      val constructorsList = objectType.decl(termNames.CONSTRUCTOR).asTerm.alternatives.collect {
        case constructor: MethodSymbol if constructor.paramLists.head.size == 1 || constructor.paramLists.head.size == 0 =>
          constructor
      }.sortBy { constructor => constructor.paramLists.head.size }

      val constructor = constructorsList.head
      val paramSize = constructor.paramLists.head.size
      val reflectConstructor = classMirror.reflectConstructor(constructor)

      reflectConstructor()

    } catch {
      case e: NoSuchElementException => println(s"TODO - add to report that default constructor or copy constuctor is missing in ${objectInstance}")
    }
  }

  /**
   *  Copies property value from original object instance to new object instance by reflecting the field value
   *  from original
   *
   *  @param propertyName
   *  		name of the property
   *  @param instanceMirror
   *  		instance mirror of original object
   *  @param objectInstance
   *  		original object instance
   *  @param newObjectInstance
   *  		new object instance
   *  @param assertableType
   *  		assertable type used for lookup of object type from the given assertable map
   *
   */
  def copyProperty(propertyName: String, instanceMirror: InstanceMirror, objectInstance: Any, newObjectInstance: Any, assertableType: AssertableType) {

    try {
      val objectType = getObjectType(objectInstance, assertableType).get
      val propertyValue = reflectField(propertyName, objectType, instanceMirror)
      setField(propertyName, propertyValue.get, newObjectInstance, objectType)
    } catch {
      case e: NoSuchElementException => println("Object type is not found in the types map")
    }
  }

  /**
   *  Determines if a given object is instance of case class
   */
  def isCaseClass(instanceObject: Any): Boolean = {
    val typeMirror = runtimeMirror(instanceObject.getClass.getClassLoader)
    val instanceMirror = typeMirror.reflect(instanceObject)
    val symbol = instanceMirror.symbol

    symbol.isCaseClass
  }

  /**
   *  Creates a deep copy from original object
   *
   *  @param originalObject
   *  		object that needs to be copied\
   *
   *  @return copied object
   *
   */
  def createCopy(originalObject: Any): Any = {
    def loop(originalObject: Any, copiedObject: Any, checkedObjectsMap: Map[Any, Int]): Any = {

      var assertableType: AssertableType = null

      getValueType(originalObject) match {
        case ENTITY_TYPE =>
          assertableType = ENTITY_TYPE
        case IGNORED_TYPE =>
          assertableType = IGNORED_TYPE
        case COMPLEX_TYPE =>
          assertableType = COMPLEX_TYPE
      }

      val checkedObjects = checkedObjectsMap ++ Map(originalObject -> 0)
      val objectProperties = getObjectProperties(originalObject, "", getObjectType(originalObject, assertableType))

      if (objectProperties nonEmpty) {

        val (primitiveProperties, nonPrimitiveProperties) = objectProperties partition {
          p: (String, Any) => getValueType(p._2) == PRIMITIVE_TYPE
        }

        if (primitiveProperties nonEmpty) {
          val im = getClassLoaderMirror.reflect(originalObject)
          primitiveProperties foreach { property => copyProperty(property._1, im, originalObject, copiedObject, assertableType) }
        }

        nonPrimitiveProperties foreach {
          case (nodeName: String, nodeObject: Any) =>
            getValueType(nodeObject) match {
              case SCALA_LIST_TYPE | SCALA_MAP_TYPE =>
                setField(nodeName, nodeObject, copiedObject, getObjectType(copiedObject, assertableType).get)
              case ENTITY_TYPE =>
                println("TODO tralalal")
              case IGNORED_TYPE =>
                println("TODO tralalal")
              case COMPLEX_TYPE => {
                if (checkedObjects.contains(nodeObject))
                  return copiedObject
                else {
                  val newEmptyObjectInstance = createEmptyCopy(nodeObject, getObjectType(nodeObject, COMPLEX_TYPE))
                  setField(nodeName, newEmptyObjectInstance, copiedObject, getObjectType(copiedObject, COMPLEX_TYPE).get)
                  val newCopiedObject = reflectObject(nodeName, copiedObject, getObjectType(copiedObject, COMPLEX_TYPE)).get
                  loop(nodeObject, newCopiedObject, checkedObjects)
                }
              }
            }
        }
        return copiedObject
      }
    }

    val emptyCopy = createEmptyCopy(originalObject, getObjectType(originalObject, COMPLEX_TYPE))
    loop(originalObject, emptyCopy, Map())
  }
}
