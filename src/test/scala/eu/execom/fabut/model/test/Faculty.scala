package eu.execom.fabut.model.test

case class Faculty(var name: String, var teacher: Teacher) {

  def this() = this("", null)
}