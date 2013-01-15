package eu.execom.testutil.property;

import java.util.ArrayList;
import java.util.List;

/**
 * Class with methods for creating instances of property types.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public abstract class PropertyFactory {

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
     * Create {@link IgnoreProperty} with provided parameter.
     * 
     * @param path
     *            property path.
     * @return created object.
     */
    public static IgnoreProperty ignored(final String path) {
        return new IgnoreProperty(path);
    }

    /**
     * Create {@link IgnoreProperty} with provided parameters.
     * 
     * @param path
     *            property path.
     * @return created objects.
     */
    public static MultiProperty ignored(final String... paths) {
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();

        for (final String path : paths) {
            properties.add(new IgnoreProperty(path));
        }

        return new MultiProperty(properties);
    }

    /**
     * Create {@link NotNullProperty} with provided parameter.
     * 
     * @param path
     *            property path.
     * @return created object.
     */
    public static NotNullProperty notNull(final String path) {
        return new NotNullProperty(path);
    }

    /**
     * Create {@link NotNullProperty} with provided parameters.
     * 
     * @param path
     *            property path.
     * @return created objects.
     */
    public static MultiProperty notNull(final String... paths) {
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();

        for (final String path : paths) {
            properties.add(new NotNullProperty(path));
        }

        return new MultiProperty(properties);
    }

    /**
     * Create {@link NullProperty} with provided parameter.
     * 
     * @param path
     *            property path.
     * @return created object.
     */
    public static NullProperty nulll(final String path) {
        return new NullProperty(path);
    }

    /**
     * Create {@link NullProperty} with provided parameters.
     * 
     * @param path
     *            property path.
     * @return created objects.
     */
    public static MultiProperty nulll(final String... paths) {
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();

        for (final String path : paths) {
            properties.add(new NullProperty(path));
        }

        return new MultiProperty(properties);
    }

}
