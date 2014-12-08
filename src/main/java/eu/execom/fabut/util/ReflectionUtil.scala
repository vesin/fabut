package eu.execom.fabut.util

import scala.reflect.runtime.universe._
import scala.collection.mutable.{ Map => MutableMap }
import scala.reflect.ClassTag
import eu.execom.fabut.model.ObjectWithSimpleProperties
import sun.reflect.generics.tree.TypeSignature
import eu.execom.fabut.FabutReportBuilder
import eu.execom.fabut.enums.AssertType._
import eu.execom.fabut.enums.AssertableType._
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.model.ObjectWithSimpleProperties
import eu.execom.fabut.model.ObjectInsideSimpleProperty
import eu.execom.fabut.FabutRepositoryAssert
import eu.execom.fabut.FabutObjectAssert
import eu.execom.fabut.property.Property
import eu.execom.fabut.property.IProperty
import eu.execom.fabut.property.IgnoredProperty
import eu.execom.fabut.FabutObjectAssert
import eu.execom.fabut.exception.CopyException
import eu.execom.fabut.graph.NodesList
import scala.collection.mutable.ListBuffer

object ReflectionUtil {

  lazy val classLoaderMirror = runtimeMirror(getClass.getClassLoader)
  lazy val SETTER_POSTFIX = "_$eq"

  var fabutAssert: FabutObjectAssert = null;

  def setFabutAssert(fabutAssert: FabutObjectAssert) {
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
  def getAssertableType(value: Any) = {

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
      fabutAssert.types(assertableType).find(
        typeName =>
          (typeName.toString == objectValue.getClass.getCanonicalName)) match {
          case n: Some[Type] =>
            Some(n.get)
          case _ =>
            None
        }
    } catch {
      case e: NoSuchElementException => None
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
  def getObjectProperties(objectInstance: Any, classTypeOption: Option[Type]): Map[String, Property] = {

    var result: Map[String, Property] = Map()

    if (classTypeOption == None) {
      return result
    }

    val classType = classTypeOption.get

    val classMirror = classType.typeSymbol.asClass
    val instanceMirror = classLoaderMirror.reflect(objectInstance)

    val allMembers = extractAllGetMethods(classType)

    try {
      result ++= allMembers map {
        case member: TermSymbol =>
          val name = member.name.toString
          val value = reflectField(member, classType, instanceMirror)
          (name, Property(name, value.get))
      }
    } catch {
      case e: ScalaReflectionException => println("which field failed with reflection")
    }
    return result
  }

  /** testing */
  //  def pullMembers[T: TypeTag](objectInstance: T)(implicit ct: ClassTag[T]) {
  //
  //    val objectType = typeOf[T]
  //
  //    val classMirror = objectType.typeSymbol.asClass
  //    val im = classLoaderMirror.reflect(objectInstance)
  //
  //    val terms = objectType.members.collect({ case x if x.isTerm => x.asTerm }).filter(_.isGetter)
  //    //val vars = terms.filter(field => field.isGetter && terms.exists(field.setter == _)).map(_.name.toString)
  //    val all = objectType.members
  //    val getters = objectType.members.collect {
  //      case symbol: TermSymbol => if (symbol.isAccessor) symbol
  //    }
  //    //
  //    //    println(terms.foreach { g => println(g) })
  //    //    println(objectType.baseClasses)
  //
  //  }

  def extractAllGetMethods(classType: Type): List[TermSymbol] = {

    val allMembers = new ListBuffer[TermSymbol]

    val classes = classType.baseClasses.map(clazz => clazz.typeSignature)

    val members = classes foreach {
      clazz =>
        val terms = clazz.members.collect { case member if member.isTerm => member.asTerm }
        val membersOfClazz = terms filter { member: TermSymbol => member.isGetter && isVariable(member, terms.toList) }
        membersOfClazz foreach { member => allMembers += member }
    }
    allMembers.toList
  }

  def isVariable(termMember: TermSymbol, termMembers: List[TermSymbol]): Boolean = {
    val variable = termMembers.exists {
      member => (member.name.toString.contains(termMember.name.toString) && member.isVar)
    }
    if (variable) true else false
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
  def reflectField(propertyName: TermSymbol, objectType: Type, instanceMirror: InstanceMirror) = {

    try {
      val field = instanceMirror.reflectMethod(propertyName.asMethod)
      Some(field())
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
      val terms = expectedObjectType.members.collect { case member if member.isTerm => member.asTerm }
      val idSymbol = terms find { member: TermSymbol => member.name.toString.contains(fieldName) && member.isGetter } getOrElse (throw new NoSuchElementException("Cannot find getter for id"))
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
   * 	field option value or none if it throws exception
   *
   * @throws ScalaReflectionException
   * @throws IllegalArgumentException
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
      case e: IllegalArgumentException => println(e.getMessage())
      case e: ScalaReflectionException => println(e.getMessage() + " " + fieldName) // throw new ili sta?
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

    if (objectTypeOption == None) throw new CopyException("")

    val objectType = objectTypeOption.get
    val classSymbol = objectType.typeSymbol.asClass
    val classMirror = classLoaderMirror.reflectClass(classSymbol)

    val constructorsList = objectType.decl(termNames.CONSTRUCTOR).asTerm.alternatives.collect {
      case constructor: MethodSymbol if constructor.paramLists.head.size == 1 || constructor.paramLists.head.size == 0 =>
        constructor
    }.sortBy { constructor => constructor.paramLists.head.size }

    if (constructorsList.nonEmpty) {
      val constructor = constructorsList.head
      val paramSize = constructor.paramLists.head.size
      val reflectConstructor = classMirror.reflectConstructor(constructor)

      Some(reflectConstructor())
    } else {
      throw new CopyException("Default or copy constructor is missing")
    }

  }

  def getIdValue(entity: Any): Any = {
    getFieldValueFromGetter("id", entity, getObjectType(entity, ENTITY_TYPE)).getOrElse(null)
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

  def createCopy(objectInstance: Any): Any = {

    if (objectInstance == null) {
      return null
    }
    getAssertableType(objectInstance) match {
      case SCALA_LIST_TYPE =>
        copyList(objectInstance.asInstanceOf[List[_]])
      case SCALA_MAP_TYPE =>
        copyMap(objectInstance.asInstanceOf[Map[_, _]])
      case _ =>
        createCopyObject(objectInstance, new NodesList).get
    }
  }

  def createCopyObject(objectInstance: Any, nodesList: NodesList): Option[Any] = {

    var flag = 0
    val copy = nodesList.getExpected(objectInstance).getOrElse {
      flag = 1
      createEmptyCopy(objectInstance, getObjectType(objectInstance, getAssertableType(objectInstance)))
        .getOrElse {
          throw new CopyException(objectInstance.getClass.getSimpleName)
          return None
        }
    }

    if (flag == 0) {
      return Some(copy)
    }

    nodesList.addPair(copy, objectInstance)
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
      case COMPLEX_TYPE =>
        createCopyObject(propertyForCopy, nodesList).get
      case SCALA_LIST_TYPE =>
        copyList(propertyForCopy.asInstanceOf[List[_]])
      case SCALA_MAP_TYPE =>
        copyMap(propertyForCopy.asInstanceOf[Map[_, _]])
      case _ =>
        propertyForCopy
    }
  }

  def copyList(list: List[_]): List[Any] = {
    list.map { property => copyProperty(property, new NodesList) }
  }

  def copyMap(mapForCopy: Map[_, _]): Map[Any, Any] = {
    mapForCopy.map { case (key, value) => (key, copyProperty(value, new NodesList)) }
  }

  def isComplexType(objectInstance: Any): Boolean = {
    getAssertableType(objectInstance) == COMPLEX_TYPE
  }

  def copyValueTo(objectInstance: Any, propertyName: String, copiedProperty: Any, copiedObject: Any): Boolean = {

    val objectType = getObjectType(copiedObject, getAssertableType(copiedObject)).getOrElse {
      return false
    }
    setField(propertyName, copiedProperty, copiedObject, objectType)
  }

}
