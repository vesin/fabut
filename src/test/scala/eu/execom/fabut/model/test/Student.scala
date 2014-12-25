package eu.execom.fabut.model.test

case class Student(var address: Address, var name: String, var lastName: String, var faculty: Faculty) {

  def this() = this(null, "", "", null)
}