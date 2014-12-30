package eu.execom.fabut.util


import eu.execom.fabut.{AssertType, Fabut, FabutRepository}

/**
 * Util class for conversions needed by testutil.
 */
object ConversionUtil {

  /**
   * Gets the assert type based on which of the Fabut interfaces does test instance implements.
   *
   * @param instance
   * the test instance
   *
   * @return the assert type
   **/
  def getAssertType(instance: AnyRef): AssertType.Value =
    instance match {
      case _: FabutRepository => AssertType.REPOSITORY_ASSERT
      case _: Fabut => AssertType.OBJECT_ASSERT
      case _ => AssertType.UNSUPPORTED_ASSERT
    }
}