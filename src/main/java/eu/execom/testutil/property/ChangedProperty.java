package eu.execom.testutil.property;

/**
 * {@link Property} extension with focus on new value of the property..
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 * @param <T>
 *            property type
 */
// TODO rename it, this name is totally wrong
public class ChangedProperty<T> extends Property {

    private final T expectedValue;

    /**
     * Change property default constructor.
     * 
     * @param path
     *            property path
     * @param expectedValue
     *            expected property value
     */
    protected ChangedProperty(final String path, final T expectedValue) {
        super(path);
        this.expectedValue = expectedValue;
    }

    /**
     * Get expected value.
     * 
     * @return expected value.
     */
    public T getExpectedValue() {
        return expectedValue;
    }

    @Override
    public ISingleProperty getCopy() {
        return new ChangedProperty<T>(getPath(), expectedValue);
    }

}
