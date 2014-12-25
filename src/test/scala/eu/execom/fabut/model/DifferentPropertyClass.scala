package eu.execom.fabut.model

class DifferentPropertyClass {
  var _notPrivateProperty: String = "testN"
  var noUnderscoreProperty: String = "noUnderscore"
  private var _privatePropertyG: String = "testG"
  //
  private var _privatePropertyNG: String = "testNG"
  private var noUnderscorePropertyPrivate: String = "noUnderscorePropertyPrivate"

  def privatePropertyG: String = _privatePropertyG

  def privatePropertyG_=(newProperty: String) =
    _privatePropertyG = newProperty

}