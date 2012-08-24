package eu.execom.testutil.model;

import java.util.List;

/**
 * Tier two type with list as property.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class TierTwoTypeWithListProperty extends Type {

    /** The Constant PROPERTY. */
    public static final String PROPERTY = "property";

    /** The property. */
    private final List<String> property;

    /**
     * Instantiates a new tier two type with list property.
     * 
     * @param property
     *            the property
     */
    public TierTwoTypeWithListProperty(final List<String> property) {
        this.property = property;
    }

    /**
     * Gets the property.
     * 
     * @return the property
     */
    public List<String> getProperty() {
        return property;
    }

}
