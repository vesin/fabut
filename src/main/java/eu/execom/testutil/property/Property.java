package eu.execom.testutil.property;

/**
 * Abstract property definition that contains path to some property that is used.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public abstract class Property implements IProperty {

    private String path;

    /**
     * Default constructor.
     * 
     * @param path
     *            property path
     */
    public Property(final String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    /**
     * Set property path.
     * 
     * @param path
     *            new path.
     */
    @Override
    public void setPath(final String path) {
        this.path = path;
    }

    @Override
    public boolean equals(final Object obj) {
        final IProperty property = (IProperty) obj;
        return path.equalsIgnoreCase(property.getPath());
    }

    /**
     * Create {@link ChangedProperty} with provided parameters.
     * 
     * @param path
     *            property path.
     * @param expectedValue
     *            expected values
     * @return created object.
     * 
     * @param <T>
     *            generic type
     */
    public static <T> ChangedProperty<T> changed(final String path, final T expectedValue) {
        return new ChangedProperty<T>(path, expectedValue);
    }

    /**
     * Create {@link IgnoreProperty} with provided parameters.
     * 
     * @param path
     *            property path.
     * @return created object.
     */
    public static IgnoreProperty ignored(final String path) {
        return new IgnoreProperty(path);
    }

    /**
     * Create {@link NotNullProperty} with provided parameters.
     * 
     * @param path
     *            property path.
     * @return created object.
     */
    public static NotNullProperty notNull(final String path) {
        return new NotNullProperty(path);
    }

    /**
     * Create {@link NullProperty} with provided parameters.
     * 
     * @param path
     *            property path.
     * @return created object.
     */
    public static NullProperty nulll(final String path) {
        return new NullProperty(path);
    }

}