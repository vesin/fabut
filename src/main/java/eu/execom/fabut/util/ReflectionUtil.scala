package eu.execom.fabut.util

import scala.reflect.runtime.universe._
import scala.reflect.ClassTag
import eu.execom.fabut.model.ObjectWithSimpleProperties
import sun.reflect.generics.tree.TypeSignature
import eu.execom.fabut.FabutReport
import eu.execom.fabut.enums.AssertType._

object ReflectionUtil {

  lazy val classLoaderMirror = runtimeMirror(getClass.getClassLoader)
  lazy val SETTER_POSTFIX = "_$eq"

  /**
   * Gets all the fields that need to be asserted within given object.
   *
   * @param objectInstance
   * 		object instance of fields
   * @param pathName
   * @param typeOption
   * 	optioned type of object instance
   *
   * @return
   *    optioned map that has fields asserted from object
   *
   * @throws ScalaReflectionException
   *
   */
  def getFieldsForAssertFromObject(objectInstance: Any, pathName: String, objectTypeOption: Option[Type]): Option[Map[String, Any]] = {

    if (objectTypeOption == None) return None

    val objectType = objectTypeOption.get

    val classMirror = objectType.typeSymbol.asClass
    val im = classLoaderMirror.reflect(objectInstance)

    val isField = (sym: Symbol) => {
      sym.asTerm.isVal || sym.asTerm.isVar
    }
    try {

      //IMPORTANT TO-DO - value is option, handle it, refactor names
      val result = objectType.decls.collect {
        case sym:
          Symbol if isField(sym) => {
          val name = sym.toString.split(' ').last
          val value = reflectField(name)(im)(objectType)
          (pathName + name, value.get)
        }
      }.toMap

      if (result isEmpty)
        Some(Map())
      else {
        Some(result)
      }

    } catch {
      case t: ScalaReflectionException => {
        None
      }
    }
  }

  def reflectProperty(propertyName: String, parentObject: Any, parentObjectTypeOption: Option[Type]): Option[Any] = {

    if (parentObjectTypeOption == None) return None

    val parentObjectType = parentObjectTypeOption.get

    val classMirror = parentObjectType.typeSymbol.asClass
    val im = classLoaderMirror.reflect(parentObject)

    val value = reflectField(propertyName)(im)(parentObjectType)

    value

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
   * @return
   * 		property value
   * @throws ScalaReflectionException
   */
  def reflectField(propertyName: String)(im: InstanceMirror)(objectType: Type) = {
    try {
      val fieldSymbol = objectType.decl(TermName(propertyName)).asMethod
      val field = im.reflectMethod(fieldSymbol)
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
      val valueOption = reflectField(objectName)(im)(expectedObjectType)
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
  def getFieldValueFromGetter(propertyName: String, objectInstance: Any, expectedObjectTypeOption: Option[Type]): Option[Any] = {

    if (expectedObjectTypeOption == None) return None

    val expectedObjectType = expectedObjectTypeOption.get
    val classMirror = expectedObjectType.typeSymbol.asClass
    val im = classLoaderMirror.reflect(objectInstance)

    try {
      val fieldSymbol = expectedObjectType.decl(TermName(propertyName)).asMethod
      val field = im.reflectMethod(fieldSymbol)
      Some(field())
    } catch {
      case t: ScalaReflectionException => None
    }
  }

  /**
   * ATM not used anywhere
   *
   * Sets the field value from given object for given field name via setter.
   *
   * @param objectInstance
   * @param propertyName
   * @param newPropertyValue
   *
   * @return
   * 	field value or null if it throws exception
   *
   * @throws ScalaReflectionException
   * @throws IllegalArgumentException
   */
  def setField[T: TypeTag](objectInstance: T)(propertyName: String)(newPropertyValue: Any)(implicit ct: ClassTag[T]) = {

    val methodName = propertyName + SETTER_POSTFIX

    val im = classLoaderMirror.reflect(objectInstance)
    val mSymbol = typeOf[T].decl(TermName(methodName)).asMethod
    val mMirror = im.reflectMethod(mSymbol)

    try {
      mMirror(newPropertyValue)
    } catch {
      case t: IllegalArgumentException => t.printStackTrace()
      //TO - DO whattt??
      case t: ScalaReflectionException => ()
    }

  }

  /**
   * Method is used to assert each primitive element from map on given depth
   * with its corresponding element from expected object, returns report that is
   * fulfilled with messages if any of the actual primitive value is not as
   * expected value
   *
   * @param pathcut
   * 	number of chars we cut from pathname so we could have a name of object
   * @param primitives
   * 	map that contains name and value of each primitive on given depth
   * @param expectedObject
   * @param expectedObjectType
   * @param report
   *
   * @return
   * 	report with added fail messages if assert fail occurs
   * @throws ScalaReflectionException
   */
  def reflectPrimitives(prefixMessage: String, pathcut: Int, primitives: Map[String, Any], expectedObject: Any, expectedObjectTypeOption: Option[Type], expectedList: Map[String, Any], report: FabutReport): Map[String, Any] = {

    var uncheckedExpectedObjectProperties = expectedList

    if (expectedObjectTypeOption == None) {
      report.addObjectNullExceptionMessage(prefixMessage, "E", "")
      return uncheckedExpectedObjectProperties
    }
    val expectedObjectType = expectedObjectTypeOption.get
    val classMirror = expectedObjectType.typeSymbol.asClass
    val im = classLoaderMirror.reflect(expectedObject)

    var fieldValue: Any = null
    primitives foreach {

      p: (String, Any) =>
        try {
          fieldValue = expectedList(p._1)
          uncheckedExpectedObjectProperties -= p._1
        } catch {
          case t: NoSuchElementException =>
            try {
              val fieldSymbol = expectedObjectType.decl(TermName(p._1.substring(pathcut))).asMethod
              val field = im.reflectMethod(fieldSymbol)
              fieldValue = field()
            } catch {
              case t: ScalaReflectionException => None
            }
        }

        if (fieldValue != p._2) {
          report.addPropertiesExceptionMessage(
            prefixMessage,
            p._1,
            if (p._2 == null) "null" else p._2,
            if (fieldValue == null) "null" else fieldValue)
        }
    }
    uncheckedExpectedObjectProperties

  }
}
