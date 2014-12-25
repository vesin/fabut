package fabut.model

case class Contract(private var _id: Long, private var _employeeId: Long, private var _employee: Employee) {
  def id: Long = _id

  def id_=(newId: Long) {
    _id = newId
  }

  def employeeId: Long = _employeeId

  def employeeId_=(newEmployeeId: Long) {
    _employeeId = newEmployeeId
  }

  def employee: Employee = _employee

  def employee_=(newEmployee: Employee) {
    _employee = newEmployee
  }

  def this(contract: Contract) = this(contract._id, contract._employeeId, contract._employee)
}