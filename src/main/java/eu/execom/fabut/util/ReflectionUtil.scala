package eu.execom.fabut.util

import scala.reflect.runtime.universe._
import scala.reflect.ClassTag
import eu.execom.fabut.model.ObjectWithSimpleProperties
import sun.reflect.generics.tree.TypeSignature
import eu.execom.fabut.FabutReport
import eu.execom.fabut.enums.AssertType._

object ReflectionUtil {

  val classLoaderMirror = runtimeMirror(getClass.getClassLoader)

  /**
   * Gets all the fields that need to be asserted within given object.
   *
   * @param obj
   * 		object instance of fields
   * @param path
   * @param t
   * 	type of object instance
   *
   * @return Seq[Symbol]
   * 		seq of field symbols
   *
   * @throws ScalaReflectionException
   *
   */
  def getFieldsForAssertFromObject(obj: Any, path: String, t: Type): Map[String, Any] = {

    if (t == null) return null

    val classMirror = t.typeSymbol.asClass
    val im = classLoaderMirror.reflect(obj)

    val isField = (sym: Symbol) => {
      sym.asTerm.isVal || sym.asTerm.isVar
    }
    try {

      val result = t.decls.collect {
        case sym:
          Symbol if isField(sym) => {
          val name = sym.toString.split(' ').last
          val value = reflectField(name)(im)(t)
          (path + name, value)
        }
      }.toSeq.reverse

      if (result isEmpty)
        Map()
      else {
        result.toMap
      }

    } catch {
      case t: ScalaReflectionException => null
    }
  }

  /**
   * Used for @method getFieldsForAssertFromMethods
   *
   * returns value from a field name and instance mirror of class
   *
   * @param fieldName
   * @param im
   * 		InstanceMirror
   * @param typeOf
   * 	type of object instance
   * @return
   * 		field value
   * @throws ScalaReflectionException
   */
  def reflectField(fieldName: String)(im: InstanceMirror)(typeOf: Type) = {
    try {
      val fieldSymbol = typeOf.decl(TermName(fieldName)).asMethod
      val field = im.reflectMethod(fieldSymbol)
      field()
    } catch {
      case t: ScalaReflectionException => Nil
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
  def reflectObject(objectName: String, expectedObject: Any, expectedObjecType: Type): Any = {

    if (expectedObjecType == null) return null

    try {
      val classMirror = expectedObjecType.typeSymbol.asClass
      val im = classLoaderMirror.reflect(expectedObject)
      val value = reflectField(objectName)(im)(expectedObjecType)

      value

    } catch {
      case t: ScalaReflectionException => Nil
    }
  }

  /**
   * Gets field value from given object for given field name via getter.
   *
   * @param fieldName
   * @param obj
   * @param expectedObjectType
   *
   * @return
   * 	field value or null if it throws exception
   *
   * @throws ScalaReflectionException
   */
  def getFieldValueFromGetter(fieldName: String, obj: Any, expectedObjectType: Type): Any = {

    if (expectedObjectType == null) return null

    val classMirror = expectedObjectType.typeSymbol.asClass
    val im = classLoaderMirror.reflect(obj)

    try {
      val fieldSymbol = expectedObjectType.decl(TermName(fieldName)).asMethod
      val field = im.reflectMethod(fieldSymbol)
      field()
    } catch {
      case t: ScalaReflectionException => ()
    }
  }

  /**
   * ATM not used anywhere
   *
   * Sets the field value from given object for given field name via setter.
   *
   * @param object
   * @param fieldName
   * @param newValue
   *
   * @return
   * 	field value or null if it throws exception
   *
   * @throws ScalaReflectionException
   * @throws IllegalArgumentException
   */
  def setField[T: TypeTag](obj: T)(fieldName: String)(newValue: Any)(implicit ct: ClassTag[T]) = {

    val methodName = fieldName + "_$eq"

    val im = classLoaderMirror.reflect(obj)
    val mSymbol = typeOf[T].decl(TermName(methodName)).asMethod
    val mMirror = im.reflectMethod(mSymbol)

    try {
      mMirror(newValue)
    } catch {
      case t: IllegalArgumentException => t.printStackTrace()
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
  def reflectPrimitives(pathcut: Int, primitives: Map[String, Any], expectedObject: Any, expectedObjectType: Type, report: FabutReport): FabutReport = {

    if (expectedObjectType == null) {
      report.addResult(ASSERT_FAILED)
      report.addObjectNullExceptionMessage("E", "")
      return report
    }

    val classMirror = expectedObjectType.typeSymbol.asClass
    val im = classLoaderMirror.reflect(expectedObject)

    primitives foreach {
      p: (String, Any) =>
        {
          try {
            val fieldSymbol = expectedObjectType.decl(TermName(p._1.substring(pathcut))).asMethod
            val field = im.reflectMethod(fieldSymbol)
            val fieldValue = field()

            if (fieldValue != p._2) {
              report.addResult(ASSERT_FAILED)
              report.addPropertiesExceptionMessage(
                p._1,
                if (p._2 == null) "null" else p._2.toString,
                if (fieldValue == null) "null" else fieldValue.toString)
            }
          } catch {
            case t: ScalaReflectionException => ()
          }
        }
    }
    report
  }
}