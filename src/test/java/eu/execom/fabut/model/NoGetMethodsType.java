package eu.execom.fabut.model;

/**
 * No conventional get methods type.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class NoGetMethodsType extends Type {

    /** The Constant PROPERTY. */
    public static final String PROPERTY = "property";

    /** The property. */
    private final String property;

    /** The not boolean property. */
    private final String notBooleanProperty;

    /**
     * Instantiates a new no get methods type.
     * 
     * @param property
     *            the property
     */
    public NoGetMethodsType(final String property) {
        this.property = property;
        this.notBooleanProperty = property;
    }

    public NoGetMethodsType() {
        property = PROPERTY;
        notBooleanProperty = PROPERTY;
    }

    /**
     * Property.
     * 
     * @return the string
     */
    public String propertyGetter() {
        return property;
    }

    /**
     * Fake get method.
     * 
     * @return {@link String}
     */
    public String getString() {
        return "String";
    }

    /**
     * Checks if is not boolean property.
     * 
     * @return the string
     */
    public String isNotBooleanProperty() {
        return notBooleanProperty;
    }

    /**
     * Checks if is field.
     * 
     * @return the string
     */
    public String isField() {
        return "not";
    }

}
