package eu.execom.fabut.model

object TrivialClasses {

  class B(var c: Any, var s: String) {
    def this() = this(null, "")
  }

  class A(var b: Any, var s: String) {
    def this() = this(null, "")
  }

  class C(var d: Any, var s: String)

  class D(var e: Any)

  class E(var a: Any)
}