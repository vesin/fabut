package eu.execom.fabut.util


import eu.execom.fabut.{AssertType, IFabutRepositoryTest, IFabutTest}

/**
 * Util class for conversions needed by testutil.
 */
object ConversionUtil {

  /**
   * Gets the assert type based on which of the Fabut interfaces does test instance implements.
   *
   * @param testInstance
   * the test instance
   *
   * @return the assert type
   **/
  def getAssertType(testInstance: Any): AssertType.Value =
    testInstance match {
      case instance: IFabutRepositoryTest => AssertType.REPOSITORY_ASSERT
      case instance: IFabutTest => AssertType.OBJECT_ASSERT
      case _ => AssertType.UNSUPPORTED_ASSERT
    }
}