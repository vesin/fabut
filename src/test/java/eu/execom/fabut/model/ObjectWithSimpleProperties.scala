package eu.execom.fabut.model

case class ObjectWithSimpleProperties(private var _username: String, private var _age: Int, var o: ObjectInsideSimpleProperty) {

  def username: String = _username
  def username_=(newName: String) {
    _username = newName
  }

  def age: Long = _age

  def age_=(newAge: Int) {
    _age = newAge
  }

  def this() = this("", 0, null)

}