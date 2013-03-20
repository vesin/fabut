package eu.execom.fabut.model;

/**
 * Class representing tier two objects with one complex object property.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class TierTwoType extends Type {

    /** The Constant PROPERTY. */
    public static final String PROPERTY = "property";

    /** The property. */
    private final TierOneType property;

    /**
     * Instantiates a new tier two type.
     * 
     * @param property
     *            the property
     */
    public TierTwoType(final TierOneType property) {
        this.property = property;
    }

    /**
     * Gets the property.
     * 
     * @return the property
     */
    public TierOneType getProperty() {
        return property;
    }

}
