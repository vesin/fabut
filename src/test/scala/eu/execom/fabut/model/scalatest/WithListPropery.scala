package fabut.model

class WithListProperty {
  var list: List[String] = List()

  def populateList(a: String) = {
    list = List(a)
  }

}