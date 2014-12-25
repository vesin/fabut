package eu.execom.fabut.model.test

case class Teacher(var name: String, var student: Student, var address: Address) {

  def this() = this("", null, null)
}