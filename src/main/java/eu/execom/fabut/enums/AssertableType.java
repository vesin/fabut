package eu.execom.fabut.enums;

/**
 * Types assertable by FABUT.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 * 
 */
public enum AssertableType {

	/** Type that can be split by sub fields and asserted by those fields. */
	COMPLEX_TYPE,

	/** Type that should be ignored during Fabut assert. */
	IGNORED_TYPE,

	/**
	 * Type used for storing in external repositories, main difference to
	 * {@link AssertableType#COMPLEX_TYPE} is that this type requires id field.
	 */
	ENTITY_TYPE,

	/** Type asserted using custom user assert. */
	PRIMITIVE_TYPE,

	SCALA_LIST_TYPE,

	SCALA_MAP_TYPE;

}
