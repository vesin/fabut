package eu.execom.fabut.util

import eu.execom.fabut.AssertableType._
import eu.execom.fabut.FabutObjectAssert
import eu.execom.fabut.exception.CopyException
import eu.execom.fabut.graph.NodesList
import eu.execom.fabut.property.Property

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

/**
 * Util class for reflection logic needed by testutil.
 */
object ReflectionUtil {


  val classLoaderMirror = runtimeMirror(getClass.getClassLoader)

  val SETTER_POSTFIX = "_$eq"

  var fabutAssert: FabutObjectAssert = null

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
  def getObjectProperties(objectInstance: Any, classType: Option[Type]): Map[String, Property] =
    if (classType.isDefined) {
      val instanceMirror = classLoaderMirror.reflect(objectInstance)
      val getterSymbols = extractGetters(classType.get)

      val members = getterSymbols.map { symbol =>
        val name = symbol.name.toString
        val value = util.Try {
          reflectField(symbol, instanceMirror).get
        }.toOption
        (name, value)
      }.toMap

      members.filter { case (name, value) => value.isDefined}.map { case (name, value) => (name, Property(name, value.get))}
    } else {
      Map()
    }

  /**
   * Extracts symbols for all getters of given class type including parent's
   *
   * @param classType
   * the class type
   *
   * @return
   * the list of term symbols of all getters for given class
   **/
  def extractGetters(classType: Type): List[TermSymbol] = {

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
   * Checks if term symbol is variable
   *
   * @param termMember
   * the term symbol
   * @param termMembers
   * list of all term symbols
   *
   * @return <code> true </code> if a term is variable <code> false </code> if term is immutable value
   **/
  def isVariable(termMember: TermSymbol, termMembers: List[TermSymbol]): Boolean =
    termMembers.exists(member =>
      member.name.toString.contains(termMember.name.toString) && member.isVar)

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
   *
   * Sets the field value of given object for given field name and new value via setter.
   *
   * @param fieldName
   * name of the field
   * @param newFieldValue
   * new field value
   * @param objectInstance
   * parent object of the field
   * @param objectType
   * type of the parent object
   *
   * @return <code> true </code> if change of value succeded, <code> false </code> otherwise.
   */
  def setField(fieldName: String, newFieldValue: Any, objectInstance: Any, objectType: Type) = try {
    val instanceMirror = classLoaderMirror.reflect(objectInstance)
    val mSymbol = objectType.member(TermName(fieldName + SETTER_POSTFIX)).asMethod
    val methodMirror = instanceMirror.reflectMethod(mSymbol)
    methodMirror(newFieldValue)
    true
  } catch {
    case e: IllegalArgumentException => false
    case e: ScalaReflectionException => false
  } // TODO throw some unsuccesful set for methods bla bla?

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
   * //TODO add throws CopyException
   * when there is no default or copy constructor of given class
   *
   */
  def createEmptyCopy(objectInstance: Any, objectType: Type): Option[Any] = {

    val classSymbol = objectType.typeSymbol.asClass
    val classMirror = classLoaderMirror.reflectClass(classSymbol)

    val constructorsList = objectType.decl(termNames.CONSTRUCTOR).asTerm.alternatives.collect {
      case constructor: MethodSymbol if constructor.paramLists.head.size == 1 || constructor.paramLists.head.size == 0 =>
        constructor
    }.sortBy { constructor => constructor.paramLists.head.size}

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
  def getIdValue(entity: Any): Option[Any] = {
    val entityType = getClassType(entity, getAssertableType(entity))
    if(entityType.isDefined){
      getFieldValueFromGetter("id", entity, entityType.get)
    } else {
      None
    }
    //val entityType = getClassType(entity, getAssertableType(entity)).get
   // val entityType = getClassType(entity, ENTITY_TYPE).getOrElse(throw new IllegalStateException(s"Undefined class type or not entity ${entity.getClass.getSimpleName}"))
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
    val idSymbol = terms.find(member => member.name.toString.contains(propertyName) && member.isGetter).get
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
        case UNDEFINED_TYPE =>
          throw new IllegalStateException(s"Unknown class type: ${objectInstance.getClass}")
        case _ => createInnerObjectCopy(objectInstance, new NodesList).get
      }
    } else {
      objectInstance // TODO should this happen?
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
  def createInnerObjectCopy(objectInstance: Any, nodesList: NodesList): Option[Any] = {
    var inList = true

    val copy = nodesList.expected(objectInstance.asInstanceOf[AnyRef]).getOrElse({
      inList = false
      createEmptyCopy(objectInstance, getClassType(objectInstance, getAssertableType(objectInstance)).get)
        .getOrElse(throw new CopyException(objectInstance.getClass.getSimpleName))
    })

    if (!inList) {
      nodesList.addPair(copy.asInstanceOf[AnyRef], objectInstance.asInstanceOf[AnyRef])
      val fieldsForCopy = getObjectProperties(objectInstance, getClassType(objectInstance, getAssertableType(objectInstance)))

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
        case UNDEFINED_TYPE => throw new IllegalStateException("Uknown class type: " + property.getClass.getSimpleName)
        case COMPLEX_TYPE => createInnerObjectCopy(property, nodesList).get
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
  def copyList(list: List[_]): List[Any] =
    list.map { property => copyProperty(property, new NodesList)}

  /**
   * Creates a copy of map by creating a deep copy for each object in the map if its not primitive
   *
   * @param mapForCopy
   * the map for copy
   *
   * @return
   * the copied map
   **/
  def copyMap(mapForCopy: Map[_, _]): Map[Any, Any] =
    mapForCopy.map { case (key, value) => (key, copyProperty(value, new NodesList))}

  /**
   * Checks if object is of complex type
   *
   * @param objectInstance
     * the object instance
   *
   * @return <code> true </code> if object is complex , <code> false </code> otherwise.
   */
  def isComplexType(objectInstance: Any): Boolean =
    getAssertableType(objectInstance) == COMPLEX_TYPE

  /**
   * Returns a type of given object
   *
   * @param value
   * the object value
   * @return
   * one of assertable types
   */
  def getAssertableType(value: Any) = value match {
    case _: List[_] => SCALA_LIST_TYPE
    case _: Map[_, _] => SCALA_MAP_TYPE
    case _ if isPrimitive(value) => PRIMITIVE_TYPE
    case _ if getClassType(value, COMPLEX_TYPE).isDefined => COMPLEX_TYPE
    case _ if getClassType(value, ENTITY_TYPE).isDefined => ENTITY_TYPE
    case _ if getClassType(value, IGNORED_TYPE).isDefined => IGNORED_TYPE
    case _ => UNDEFINED_TYPE
  }

  /**
   * Returns class type from 'types' for given object
   *
   * @param objectValue
   * the object value
   * @param assertableType
   * the object type
   * @return
   * the specific type of class in case it exists for the given object type
   */

  def getClassType(objectValue: Any, assertableType: AssertableType): Option[Type] = util.Try {
    fabutAssert.types(assertableType).find(typeName =>
      typeName.toString == objectValue.getClass.getCanonicalName).get
  }.toOption

  /**
   * Checks if the value is primitive
   *
   * @param value
   * the value
   *
   * @return <code> true </code> if value is primitive </code> otherwise.
   **/
  def isPrimitive(value: Any): Boolean = value match {
    case _: Byte => true
    case _: Short => true
    case _: Int => true
    case _: Long => true
    case _: Float => true
    case _: Double => true
    case _: Char => true
    case _: Boolean => true
    case _: Unit => true
    case _: String => true
    case null => true
    case _ => false
  }

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
