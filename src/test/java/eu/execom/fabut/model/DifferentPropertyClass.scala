package eu.execom.fabut.model

class DifferentPropertyClass {
  private var _privatePropertyG: String = "testG" //

  def privatePropertyG: String = _privatePropertyG

  def privatePropertyG_=(newProperty: String) =
    _privatePropertyG = newProperty

  private var _privatePropertyNG: String = "testNG"

  var _notPrivateProperty: String = "testN"

  var noUnderscoreProperty: String = "noUnderscore"

  private var noUnderscorePropertyPrivate: String = "noUnderscorePropertyPrivate"

}