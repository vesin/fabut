package eu.execom.fabut.model

case class ObjectWithSimpleProperties(private var _username: String,private var _age: Int, var o: ObjectInsideSimpleProperty) {

  def username: String = _username

  def username_=(newName: String) {
    _username = newName
  }

  def age: Long = _age

  def age_=(newAge: Long) {
    _age = newAge.asInstanceOf[Int]
  }

  def this() = this("", 0, null)
}