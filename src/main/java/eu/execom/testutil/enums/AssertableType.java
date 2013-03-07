package eu.execom.testutil.enums;

import java.util.List;

/**
 * Types assertable by FABUT.
 * 
 * @author nolah
 * 
 */
public enum AssertableType {

    /** Type that can be split by sub fields and asserted by those fields. */
    COMPLEX_TYPE,

    /** Type that should be ignored during Fabut assert. */
    IGNORED_TYPE,

    /**
     * Type used for storing in external repositories, main diffence to {@link AssertableType#COMPLEX_TYPE} is that this
     * type requires id field.
     */
    ENTITY_TYPE,

    /** Type asserted using custom user assert. */
    PRIMITIVE_TYPE,

    /** Type that implements {@link List}, its asserted iterating over its elements and asserting those. */
    LIST_TYPE;

}
