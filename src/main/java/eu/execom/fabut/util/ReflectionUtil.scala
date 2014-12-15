package eu.execom.fabut.util

import eu.execom.fabut.FabutObjectAssert
import eu.execom.fabut.enums.AssertableType._
import eu.execom.fabut.exception.CopyException
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.property.Property

import scala.collection.mutable.{ListBuffer, Map => MutableMap}
import scala.reflect.runtime.universe._

/**
 * Util class for reflection logic needed by testutil.
 */
object ReflectionUtil {

  lazy val classLoaderMirror = runtimeMirror(getClass.getClassLoader)
  lazy val SETTER_POSTFIX = "_$eq"

  var fabutAssert: FabutObjectAssert = null

  /**
   * Returns a type of given value
   *
   * @param value
   * @return
   * one of assertable types
   */
  def getAssertableType(value: Any) = value match {
    case _: List[_] => SCALA_LIST_TYPE
    case _: Map[_, _] => SCALA_MAP_TYPE
    case _ if getObjectType(value, COMPLEX_TYPE).isDefined => COMPLEX_TYPE
    case _ if getObjectType(value, ENTITY_TYPE).isDefined => ENTITY_TYPE
    case _ if getObjectType(value, IGNORED_TYPE).isDefined => IGNORED_TYPE
    case _ => PRIMITIVE_TYPE
  }


  /**
   * Returns object type from 'types' for given value
   *
   * @param objectValue
   * object for which we check the type
   * @param assertableType
   * the list of predefined objects from map of types where we should search for value
   * @return
   * the specific type of object
   */

  def getObjectType(objectValue: Any, assertableType: AssertableType): Option[Type] =
    if (objectValue == null) {
      None
    } else {
      try {
        fabutAssert.types(assertableType).find(typeName =>
          typeName.toString == objectValue.getClass.getCanonicalName)

      } catch {
        case e: NoSuchElementException => None
      }
    }


  /**
   * Gets all the properties of given object that need to be asserted.
   *
   * @param objectInstance
   * object instance of fields
   * @param pathName
   * @param objectTypeOption
   * optioned object instance type
   *
   * @return
   * optioned map that has all the properties of object instance
   *
   * @throws ScalaReflectionException
   *
   */
  def getObjectProperties(objectInstance: Any, classTypeOption: Option[Type]): Map[String, Property] = {

    val result: scala.collection.mutable.Map[String, Property] = MutableMap()

    if (classTypeOption == None) {
      return result.toMap
    }

    val classType = classTypeOption.get
    val instanceMirror = classLoaderMirror.reflect(objectInstance)
    val allMembers = extractAllGetMethods(classType)

    try {
      result ++= allMembers.map({
        case member: TermSymbol =>
          val name = member.name.toString
          val value = reflectField(member, classType, instanceMirror)
          (name, Property(name, value.get))
      })
    } catch {
      case e: ScalaReflectionException => println("which field failed with reflection")
    }
    result.toMap
  }

  def extractAllGetMethods(classType: Type): List[TermSymbol] = {

    val allMembers = new ListBuffer[TermSymbol]

    val classes = classType.baseClasses.map(clazz => clazz.typeSignature)

    classes.foreach({ clazz =>
      val terms = clazz.members.collect({ case member if member.isTerm => member.asTerm})
      val membersOfClazz = terms.filter({ member: TermSymbol => member.isGetter && isVariable(member, terms.toList)})

      membersOfClazz.foreach({ member => allMembers += member})
    })
    allMembers.toList
  }

  def isVariable(termMember: TermSymbol, termMembers: List[TermSymbol]): Boolean =
    termMembers.exists(member =>
      member.name.toString.contains(termMember.name.toString) && member.isVar)


  /**
   * Used for @method getFieldsForAssertFromMethods
   *
   * returns value from a field name and instance mirror of class
   *
   * @param propertyName
   * property name that needs to be asserted
   * @param im
   * instance mirror of object instance
   * @param objectType
   * type of object instance
   *
   * @return
   * property value
   * @throws ScalaReflectionException
   */
  def reflectField(propertyName: TermSymbol, objectType: Type, instanceMirror: InstanceMirror): Option[Any] = util.Try {
    val field = instanceMirror.reflectMethod(propertyName.asMethod)
    field()
  }.toOption


  /**
   * Gets field value from given object for given field name via getter.
   *
   * @param propertyName
   * @param objectInstance
   * @param expectedObjectType
   *
   * @return
   * property value or None if it throws exception
   *
   * @throws ScalaReflectionException
   */
  def getFieldValueFromGetter(fieldName: String, objectInstance: Any, expectedObjectTypeOption: Option[Type]): Option[Any] =
    if (expectedObjectTypeOption == None)
      None
    else {
      val expectedObjectType = expectedObjectTypeOption.get
      try {
        val instanceMirror = classLoaderMirror.reflect(objectInstance)
        val terms = expectedObjectType.members.collect({ case member if member.isTerm => member.asTerm})
        val idSymbol = terms.find(member => member.name.toString.contains(fieldName) && member.isGetter).getOrElse(throw new NoSuchElementException("Cannot find getter for id"))
        val field = instanceMirror.reflectMethod(idSymbol.asMethod)
        Some(field())
      } catch {
        case t: ScalaReflectionException =>
          None
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
   * field option value or none if it throws exception
   */
  def setField(fieldName: String, newFieldValue: Any, objectInstance: Any, objectType: Type): Boolean = {

    val methodName = fieldName + SETTER_POSTFIX

    try {
      val im = classLoaderMirror.reflect(objectInstance)
      val mSymbol = objectType.member(TermName(methodName)).asMethod
      val methodMirror = im.reflectMethod(mSymbol)
      methodMirror(newFieldValue)
      return true
    } catch {
      case e: IllegalArgumentException => println(e.getMessage)
      case e: ScalaReflectionException => println(e.getMessage) // throw new ili sta?
    }
    false
  }

  /**
   * Creates copy of given object with empty properties
   *
   * @param objectInstance
   * @param objectTypeOption
   *
   */
  def createEmptyCopy(objectInstance: Any, objectTypeOption: Option[Type]): Option[Any] = {

    if (objectTypeOption == None) throw new CopyException("") //TODO

    val objectType = objectTypeOption.get
    val classSymbol = objectType.typeSymbol.asClass
    val classMirror = classLoaderMirror.reflectClass(classSymbol)

    val constructorsList = objectType.decl(termNames.CONSTRUCTOR).asTerm.alternatives.collect {
      case constructor: MethodSymbol if constructor.paramLists.head.size == 1 || constructor.paramLists.head.size == 0 =>
        constructor
    }.sortBy { constructor => constructor.paramLists.head.size}

    if (constructorsList.nonEmpty) {
      val constructor = constructorsList.head
      val reflectConstructor = classMirror.reflectConstructor(constructor)

      Some(reflectConstructor())
    } else {
      throw new CopyException("Default or copy constructor is missing")
    }

  }

  def getIdValue(entity: Any): Any = {
    getFieldValueFromGetter("id", entity, getObjectType(entity, ENTITY_TYPE)).orNull
  }

  /**
   * Determines if a given object is instance of case class
   */
  def isCaseClass(instanceObject: Any): Boolean = {
    val typeMirror = runtimeMirror(instanceObject.getClass.getClassLoader)
    val instanceMirror = typeMirror.reflect(instanceObject)
    val symbol = instanceMirror.symbol

    symbol.isCaseClass
  }

  def createCopy(objectInstance: Any): Any =
    if (objectInstance == null) {
      return null
    } else {
      getAssertableType(objectInstance) match {
        case SCALA_LIST_TYPE => copyList(objectInstance.asInstanceOf[List[_]])
        case SCALA_MAP_TYPE => copyMap(objectInstance.asInstanceOf[Map[_, _]])
        case _ => createCopyObject(objectInstance, new NodesList).get
      }
    }

  def createCopyObject(objectInstance: Any, nodesList: NodesList): Option[Any] = {

    var flag = 0 // TODO make falt boolean
    val copy = nodesList.expected(objectInstance.asInstanceOf[AnyRef]).getOrElse({
        flag = 1
        createEmptyCopy(objectInstance, getObjectType(objectInstance, getAssertableType(objectInstance)))
          .getOrElse(throw new CopyException(objectInstance.getClass.getSimpleName))
      })

    if (flag == 0) {
      return Some(copy)
    }

    nodesList.addPair(copy.asInstanceOf[AnyRef], objectInstance.asInstanceOf[AnyRef])
    val fieldsForCopy = getObjectProperties(objectInstance, getObjectType(objectInstance, getAssertableType(objectInstance)))

    fieldsForCopy.foreach {
      field =>
        val copiedProperty = copyProperty(field._2.value, nodesList)
        copyValueTo(objectInstance, field._1, copiedProperty, copy)
    }

    Some(copy)
  }

  def copyProperty(propertyForCopy: Any, nodesList: NodesList): Any = {

    if (propertyForCopy == null) {
      return null
    }
    getAssertableType(propertyForCopy) match {
      case COMPLEX_TYPE => createCopyObject(propertyForCopy, nodesList).get
      case SCALA_LIST_TYPE => copyList(propertyForCopy.asInstanceOf[List[_]])
      case SCALA_MAP_TYPE => copyMap(propertyForCopy.asInstanceOf[Map[_, _]])
      case _ => propertyForCopy
    }
  }

  def copyList(list: List[_]): List[Any] = {
    list.map { property => copyProperty(property, new NodesList)}
  }

  def copyMap(mapForCopy: Map[_, _]): Map[Any, Any] = {
    mapForCopy.map { case (key, value) => (key, copyProperty(value, new NodesList))}
  }

  def isComplexType(objectInstance: Any): Boolean = {
    getAssertableType(objectInstance) == COMPLEX_TYPE
  }

  def copyValueTo(objectInstance: Any, propertyName: String, copiedProperty: Any, copiedObject: Any): Boolean =
    getObjectType(copiedObject, getAssertableType(copiedObject)) match {
      case Some(objectType) => setField(propertyName, copiedProperty, copiedObject, objectType)
      case None => false
    }

}
