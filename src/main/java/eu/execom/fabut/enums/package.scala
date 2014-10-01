package eu.execom.fabut

//TODO move to enum files
package object enums {
	class AssertableType
	object AssertableType extends Enumeration {

		type AssertableType = Value
		val SCALA_LIST_TYPE, SCALA_MAP_TYPE, COMPLEX_TYPE, ENTITY_TYPE, IGNORED_TYPE, PRIMITIVE_TYPE = Value
	}

	class AssertType
	object AssertType extends Enumeration {

		type AssertType = Value
		val ASSERT_FAILED, ASSERT_SUCCESS = Value
	}
}