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
     * Create {@link Property} with provided parameters.
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
    public static <T> Property<T> value(final String path, final T expectedValue) {
        return new Property<T>(path, expectedValue);
    }

    /**
     * Create {@link IgnoredProperty} with provided parameter.
     * 
     * @param path
     *            property path.
     * @return created object.
     */
    public static IgnoredProperty ignored(final String path) {
        return new IgnoredProperty(path);
    }

    /**
     * Create {@link IgnoredProperty} with provided parameters.
     * 
     * @param paths
     *            property path.
     * @return created objects.
     */
    public static MultiProperties ignored(final String... paths) {
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();

        for (final String path : paths) {
            properties.add(ignored(path));
        }

        return new MultiProperties(properties);
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
     * @param paths
     *            property paths.
     * @return created objects.
     */
    public static MultiProperties notNull(final String... paths) {
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();

        for (final String path : paths) {
            properties.add(notNull(path));
        }

        return new MultiProperties(properties);
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
     * @param paths
     *            property paths.
     * @return created objects.
     */
    public static MultiProperties nulll(final String... paths) {
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();

        for (final String path : paths) {
            properties.add(nulll(path));
        }

        return new MultiProperties(properties);
    }

}
