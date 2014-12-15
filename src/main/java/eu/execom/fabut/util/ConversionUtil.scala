package eu.execom.fabut.util

import eu.execom.fabut.enums.AssertType
import eu.execom.fabut.{IFabutRepositoryTest, IFabutTest}

object ConversionUtil {

  def getAssertType(testInstance: Any): AssertType.Value =
    testInstance match {
      case instance: IFabutRepositoryTest => AssertType.REPOSITORY_ASSERT
      case instance: IFabutTest => AssertType.OBJECT_ASSERT
      case _ => AssertType.UNSUPPORTED_ASSERT
    }

}