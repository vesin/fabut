package eu.execom.fabut
import eu.execom.fabut.util.ReflectionUtil._

class FabutRepositoryAssert(fabutTest: IFabutTest) extends FabutObjectAssert(fabutTest) {
  setFabutAssert(this)
}