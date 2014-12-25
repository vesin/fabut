

package fabut.model

case class Employee(private var _id: Long, private var _name: String) {
  def id: Long = _id

  def id_=(newId: Long) {
    _id = newId
  }

  def name: String = _name

  def name_=(newName: String) {
    _name = newName
  }

  def this(employee: Employee) = this(employee._id, employee._name)
}