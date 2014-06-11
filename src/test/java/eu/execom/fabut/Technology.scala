package eu.execom.fabut

class Technology(private var _name: String) {
  def name: String = _name

  def name_=(newName: String) {
    val list =
      _name = newName
  }

  def getList: List[String] = {
    List("Java", "Scala", "Javascript")
  }

}