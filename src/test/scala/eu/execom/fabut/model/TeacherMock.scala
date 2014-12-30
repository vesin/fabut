package eu.execom.fabut.model


case class TeacherMock(private var _id: Int, var __name: String, private var _cardSerialNumber: String, private var _schoolId: Int, private var _approved: Boolean) {
  private var __id: Int = _id

  def id_persisted: Int = _id

  def id: Int = _id

  def id_=(newId: Int): Any = _id
}
