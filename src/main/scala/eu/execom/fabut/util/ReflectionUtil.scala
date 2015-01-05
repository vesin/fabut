package eu.execom.fabut.util

import eu.execom.fabut.AssertableType._
import eu.execom.fabut.FabutObjectAssert
import eu.execom.fabut.FieldType._
import eu.execom.fabut.exception.CopyException
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.property.Property

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

/**
 * Util class for reflection logic needed by testutil.
 */
object ReflectionUtil {

  val ID = "id"
  val SETTER_POSTFIX = "_$eq"
  val classLoaderMirror = runtimeMirror(getClass.getClassLoader)
  private var _fabutAssert: FabutObjectAssert = null

  def initUtils(fabutAssert: FabutObjectAssert): Unit = _fabutAssert = fabutAssert

  /**
   * Gets all the properties with values of given object that need to be asserted.
   *
   * @param objectInstance
   * the object
   * @param classType
   * optioned objects class type
   *
   * @return
   * map that contains all of the object's properties
   *
   */
  def getObjectProperties(objectInstance: Any, classType: Option[Type], fieldType: FieldType): Map[String, Property] =
    if (classType.isDefined) {
      val instanceMirror = classLoaderMirror.reflect(objectInstance)
      val getters  = fieldType match {
        case FOR_COPY =>  extractFields(classType.get)
        case FOR_ASSERT => extractGetterSymbols(classType.get)
      }
      val properties = getters.map { getter =>
        val name = getter.name.toString
        val value = reflectMethod(getter.asMethod, instanceMirror)
        (name, value)
      }.toMap
      properties.withFilter { case (name, value) => value.isDefined}.map { case (name, value) => (name, Property(name, value.get))}
    } else {
      Map()
    }

  /**
   * Extracts symbols for all public getters of given class type including parent's
   *
   * @param classType
   * the class type
   *
   * @return
   * the list of term symbols of all getters for given class
   **/
  def extractGetterSymbols(classType: Type): List[TermSymbol] = {
    val getters: ListBuffer[TermSymbol] = new ListBuffer()
    val classes = classType.baseClasses.map(clazz => clazz.typeSignature)
    classes.foreach { clazz =>
      val publicMethods = clazz.members.collect { case member: MethodSymbol if member.isPublic => member}
      val vars = clazz.members.collect { case member: TermSymbol if member.isVar => member}
      vars.foreach { variable =>
        if (hasCustomGetter(variable, publicMethods)) {
          getters += publicMethods.find { method => method.name.toString == asGetter(variable)}.get
        } else {
          val getter = publicMethods.find { member => member.name.toString == variable.name.toString.trim }
          if (getter.isDefined) {
            getters += getter.get
          }
        }
      }
    }
    getters.toList
  }

  /**
   * Extracts symbols for all private getters of given class type including parent's
   *
   * @param classType
   * the class type
   *
   * @return
   * the list of term symbols of all getters for given class
   **/
  def extractFields(classType: Type): List[TermSymbol] = {
    val getters = new ListBuffer[TermSymbol]
    val classTypes = classType.baseClasses.map(clazz => clazz.typeSignature)
    classTypes.foreach { clazz =>
      val termSymbols = clazz.members.collect({ case member if member.isTerm => member.asTerm})
      val clazzGetters = termSymbols.filter({ member: TermSymbol => member.isGetter && isVariable(member, termSymbols.toList)})
      clazzGetters.foreach({ getter => getters += getter})
    }
    getters.toList
  }


  /**
   * Removes underscore prefix from field name
   *
   * @param term
   * - term symbol
   *
   * @return
   * - name for field as custom getter
   **/
  def asGetter(term: TermSymbol): String = term.name.toString.dropWhile( char => char == '_').trim

  /**
   * Checks if getter without underscore prefix for given term exists in classes member scope.
   *
   * @param term
   * - term symbol
   * @param terms
   * - members of class
   *
   * @return <code> true </code> if exists, <code> false </code> otherwise.
   **/
  def hasCustomGetter(term: TermSymbol, terms: Iterable[TermSymbol]): Boolean = terms.exists(member => member.name.toString.equals(asGetter(term)))

  /**
   * Checks if term symbol is variable
   *
   * @param termMember
   * the term symbol
   * @param termMembers
   * list of all term symbols
   *
   * @return <code> true </code> if a term is variable <code> false </code> if term is immutable value
   **/
  def isVariable(termMember: TermSymbol, termMembers: List[TermSymbol]): Boolean = termMembers.exists(member => member.name.toString.contains(termMember.name.toString) && member.isVar)

  /**
   * Reflects a value for given term symbol and instance mirror
   *
   * @param symbol
   * symbol of the method
   * @param instanceMirror
   * instance mirror of object instance
   *
   * @return
   * value of field if method in instance mirror exists
   */
  def reflectField(symbol: TermSymbol, instanceMirror: InstanceMirror): Option[Any] = util.Try {
    val field = instanceMirror.reflectMethod(symbol.asMethod)
    field()
  }.toOption


  /**
   * Reflects a method from instance mirror
   *
   * @param method
   * - method symbol
   * @param instanceMirror
   * - object's instance mirror
   *
   * @return
   * - value from invoking the method
   * */
  def reflectMethod(method: MethodSymbol, instanceMirror: InstanceMirror): Option[Any] = util.Try {
    val field = instanceMirror.reflectMethod(method)
    field()
  }.toOption

  /**
   *
   * Sets the field value of given object for given field name and new value via setter.
   *
   * @param fieldName
   * name of the field
   * @param newFieldValue
   * new field value
   * @param objectInstance
   * parent object of the field
   * @param classType
   * type of the parent object
   *
   * @return <code> true </code> if change of value succeded, <code> false </code> otherwise.
   */
  def setField(fieldName: String, newFieldValue: Any, objectInstance: Any, classType: Type):Boolean = try {
    val instanceMirror = classLoaderMirror.reflect(objectInstance)
    val mSymbol = classType.member(TermName(fieldName+SETTER_POSTFIX)).asMethod
    val fieldMirror = instanceMirror.reflectMethod(mSymbol)
        fieldMirror(newFieldValue)
        true
      } catch {
        case e: ScalaReflectionException => false
        case e: AssertionError => false
        case e: NoSuchElementException => false
  }

  /**
   * Creates empty copy of given object
   *
   * @param objectInstance
   * object that we want to copy
   * @param objectType
   * type of the object
   *
   * @return
   * empty copy of given object or None if reflection is unsuccessful
   *
   * throws CopyException
   * when there is no default or copy constructor of given class
   *
   */
  def createEmptyCopy(objectInstance: Any, objectType: Type): Option[Any] = {
    val classSymbol = objectType.typeSymbol.asClass
    val classMirror = classLoaderMirror.reflectClass(classSymbol)
    val constructorsList = objectType.decl(termNames.CONSTRUCTOR).asTerm.alternatives.collect{
      case constructor: MethodSymbol if constructor.paramLists.head.size == 1 || constructor.paramLists.head.size == 0 => constructor
    }.sortBy(constructor => constructor.paramLists.head.size)

    if (constructorsList.nonEmpty) {
      val constructor = constructorsList.head
      util.Try {
        val reflectConstructor = classMirror.reflectConstructor(constructor)
        reflectConstructor()
      }.toOption
    } else {
      throw new CopyException(s"Default or copy constructor is missing for class type $objectType")
    }
  }

  /**
   * Gets id value for given entity
   *
   * @param entity
   * the entity
   *
   * @return
   * the value of id property
   **/
  def getIdValue(entity: Any): Option[Any] = getClassType(entity, getAssertableType(entity)) match {
      case Some(entityType) => getFieldValueFromGetter(ID, entity, entityType)
      case _ => None
  }

  /**
   * Gets property value from given object for given property name via getter.
   *
   * @param propertyName
   * name of the property
   * @param objectInstance
   * parent object of the property
   * @param objectType
   * type of object
   *
   * @return
   * property value or None if the property with given value doesn't exist
   *
   */
  def getFieldValueFromGetter(propertyName: String, objectInstance: Any, objectType: Type): Option[Any] = util.Try {
    val instanceMirror = classLoaderMirror.reflect(objectInstance)
    val terms = objectType.members.collect({ case member if member.isTerm => member.asTerm})
    val idSymbol = terms.find(member => member.name.toString.equals(propertyName)).get
    val field = instanceMirror.reflectMethod(idSymbol.asMethod)
    field()
  }.toOption

  /**
   * Determines if a given object is instance of case class
   * edit: Unused ATM
   *
   * @param objectInstance
   * object instance
   *
   * @return <code> true </code> if object is case class <code> false </code> otherwise.
   */
  def isCaseClass(objectInstance: Any): Boolean = {
    val typeMirror = runtimeMirror(objectInstance.getClass.getClassLoader)
    val instanceMirror = typeMirror.reflect(objectInstance)
    val symbol = instanceMirror.symbol
    symbol.isCaseClass
  }

  /**
   * Creates a deep copy for given object
   *
   * @param objectInstance
   * object instance
   *
   * @return
   * deep copy for given object instance
   **/
  def createCopy(objectInstance: Any): Any =
    if (objectInstance != null) {
      getAssertableType(objectInstance) match {
        case SCALA_LIST_TYPE => copyList(objectInstance.asInstanceOf[List[_]])
        case SCALA_MAP_TYPE => copyMap(objectInstance.asInstanceOf[Map[_, _]])
        case _ => createObjectCopy(objectInstance, new NodesList).get
      }
    } else {
      objectInstance
    }

  /**
   * Creates a deep copy for given inner object
   *
   * @param objectInstance
   * the object instance
   * @param nodesList
   * the list of already copied objects so we don't get stuck into recursive copying
   *
   * @return
   * deep copy of object
   **/
  def createObjectCopy(objectInstance: Any, nodesList: NodesList): Option[Any] = {
    var inList = true

    val copy = nodesList.expected(objectInstance).getOrElse{
      inList = false
      createEmptyCopy(objectInstance, getClassType(objectInstance, getAssertableType(objectInstance)).get)
        .getOrElse(throw new CopyException(objectInstance.getClass.getSimpleName))
    }

    if (!inList) {
      nodesList.addPair(copy, objectInstance)
      val fieldsForCopy = getObjectProperties(objectInstance, getClassType(objectInstance, getAssertableType(objectInstance)),FOR_COPY)

      fieldsForCopy.foreach { field =>
        val copiedProperty = copyProperty(field._2.value, nodesList)
        copyValueTo(field._1, copiedProperty, copy)
      }
    }
    Some(copy)
  }

  /**
   * Creates a copy of given object property
   *
   * @param property
   * value of the property
   * @param nodesList
   * the list of already copied objects so we don't get stuck into recursive copying
   *
   * @return
   * copied value of property
   *
   * @throws IllegalStateException in case class type is not supported
   **/
  def copyProperty(property: Any, nodesList: NodesList): Any =
    if (property != null) {
      getAssertableType(property) match {
        case COMPLEX_TYPE => createObjectCopy(property, nodesList).get
        case SCALA_LIST_TYPE => copyList(property.asInstanceOf[List[_]])
        case SCALA_MAP_TYPE => copyMap(property.asInstanceOf[Map[_, _]])
        case _ => property
      }
    } else {
      property
    }

  /**
   * Creates a copy of list by creating a deep copy for each object in the list if its not primitive
   *
   * @param list
   * the list for copy
   *
   * @return
   * the copied list
   **/
  def copyList(list: List[_]): List[Any] = list.map { property => copyProperty(property, new NodesList)}

  /**
   * Creates a copy of map by creating a deep copy for each object in the map if its not primitive
   *
   * @param mapForCopy
   * the map for copy
   *
   * @return
   * the copied map
   **/
  def copyMap(mapForCopy: Map[_, _]): Map[Any, Any] = mapForCopy.map{ case (key, value) => (key, copyProperty(value, new NodesList))}

  /**
   * Checks if object is of complex type
   *
   * @param objectInstance
     * the object instance
   *
   * @return <code> true </code> if object is complex , <code> false </code> otherwise.
   */
  def isComplexType(objectInstance: Any): Boolean = getAssertableType(objectInstance) == COMPLEX_TYPE

  /**
   * Returns a type of given object
   *
   * @param value
   * the object value
   * @return
   * one of assertable types
   */
  def getAssertableType(value: Any): AssertableType = value match {
    case _: List[_] => SCALA_LIST_TYPE
    case _: Map[_, _] => SCALA_MAP_TYPE
    case _ if getClassType(value, COMPLEX_TYPE).isDefined => COMPLEX_TYPE
    case _ if getClassType(value, ENTITY_TYPE).isDefined => ENTITY_TYPE
    case _ if getClassType(value, IGNORED_TYPE).isDefined => IGNORED_TYPE
    case _ => PRIMITIVE_TYPE
  }

  /**
   * Returns class type from 'types' for given object
   *
   * @param objectInstance
   * the object value
   * @param assertableType
   * the object type
   * @return
   * the specific type of class in case it exists for the given object type
   */

  def getClassType(objectInstance: Any, assertableType: AssertableType): Option[Type] = util.Try {
    _fabutAssert.types(assertableType).find(typeName =>
      typeName.toString == objectInstance.getClass.getCanonicalName).get
  }.toOption

  /**
   * Sets the copied property to copied object
   *
   * @param propertyName
   * name of the property we want to set
   * @param copiedProperty
   * property value
   * @param copiedObject
   * parent copied object whose property we want to set
   *
   * @return <code> true </code> if we've set the property value <code> false </code> if it failed.
   **/
  def copyValueTo(propertyName: String, copiedProperty: Any, copiedObject: Any): Boolean = {
    val objectType = getClassType(copiedObject, getAssertableType(copiedObject))
    setField(propertyName, copiedProperty, copiedObject, objectType.get)
  }
}
