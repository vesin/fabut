package eu.execom.testutil.property;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract property definition that contains path to some property that is used.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public abstract class Property implements ISingleProperty {

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
        final ISingleProperty property = (ISingleProperty) obj;
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
    public static MultiProperty ignored(final String... paths) {
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();

        for (final String path : paths) {
            properties.add(new IgnoreProperty(path));
        }

        return new MultiProperty(properties);
    }

    /**
     * Create {@link NotNullProperty} with provided parameters.
     * 
     * @param path
     *            property path.
     * @return created object.
     */
    public static MultiProperty notNull(final String... paths) {
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();

        for (final String path : paths) {
            properties.add(new NotNullProperty(path));
        }

        return new MultiProperty(properties);
    }

    /**
     * Create {@link NullProperty} with provided parameters.
     * 
     * @param path
     *            property path.
     * @return created object.
     */
    public static MultiProperty nulll(final String... paths) {
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();

        for (final String path : paths) {
            properties.add(new NullProperty(path));
        }

        return new MultiProperty(properties);
    }
}
