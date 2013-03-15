package eu.execom.fabut.model;

/**
 * Type with one ignored type property.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class TierTwoTypeWithIgnoreProperty extends Type {

    /** The Constant IGNORED_TYPE. */
    public static final String IGNORED_TYPE = "ignoredType";

    /** The ignored type. */
    private final IgnoredType ignoredType;

    /**
     * Instantiates a new tier two type with ignore property.
     * 
     * @param ignoredType
     *            the ignored type
     */
    public TierTwoTypeWithIgnoreProperty(final IgnoredType ignoredType) {
        this.ignoredType = ignoredType;
    }

    /**
     * Gets the ignored type.
     * 
     * @return the ignored type
     */
    public IgnoredType getIgnoredType() {
        return ignoredType;
    }

}
