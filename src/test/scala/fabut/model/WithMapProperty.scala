package fabut.model

class WithMapProperty {

  var map: Map[String, String] = Map()

  def populateMap(a: String) = {
    map += "Test" -> a
  }

}