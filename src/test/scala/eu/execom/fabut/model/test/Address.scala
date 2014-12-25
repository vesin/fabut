package eu.execom.fabut.model.test

case class Address(var city: String, var street: String, var streetNumber: String) {

  def this() = this("", "", "")
}