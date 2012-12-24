package eu.execom.testutil;

import java.util.List;

import eu.execom.testutil.property.IProperty;
import eu.execom.testutil.property.ISingleProperty;

/**
 * Interface for object assertion.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 * @param <T>
 */
public interface EntityAssert<T> {

    /**
     * Asserts list of objects with specified vararg array.
     * 
     * @param expected
     *            list of expected objects
     * @param actuals
     *            array of actual objects
     * @param <X>
     *            type of asserted object
     */
    <X> void assertObjects(final List<X> expected, final X... actuals);

    /**
     * Asserts list of objects with specified list of objects.
     * 
     * @param expected
     *            list of expected objects
     * @param actual
     *            list of actual objects
     * @param <X>
     *            type of asserted object
     */
    <X> void assertObjects(final List<X> expected, final List<X> actual);

    /**
     * Asserts two objects using array of properties who exclude properties from expected object.
     * 
     * @param expected
     *            expected object
     * @param actual
     *            actual object
     * @param excludes
     *            array of excluded properties
     * @param <X>
     *            type of asserted object
     */
    <X> void assertObjects(final X expected, final X actual, final IProperty... excludes);

    /**
     * Asserts two objects using list of properties who exclude properties from expected object.
     * 
     * @param message
     *            starting message for report
     * @param expected
     *            expected object
     * @param actual
     *            actual object
     * @param excludedProperties
     *            list of excluded properties
     * @param <X>
     *            type of asserted object
     */
    <X> void assertObjects(final String message, final X expected, final X actual,
            final List<ISingleProperty> excludedProperties);

    /**
     * Asserts two objects using array of properties who exclude properties from expected object.
     * 
     * @param message
     *            starting message for report
     * @param expected
     *            expected object
     * @param actual
     *            actual object
     * @param excludes
     *            array of excluded properties
     * @param <X>
     *            type of asserted object
     */
    <X> void assertObjects(final String message, final X expected, final X actual, final IProperty... excludes);

    /**
     * Asserts object with array of properties.
     * 
     * @param actual
     *            actual object
     * @param excludes
     *            array of properties
     * @param <X>
     *            type of asserted object
     */
    <X> void assertObject(final X actual, final IProperty... excludes);

    /**
     * Asserts object with array of properties.
     * 
     * @param message
     *            starting message for report
     * @param actual
     *            actual object
     * @param excludes
     *            array of properties
     * @param <X>
     *            type of asserted object
     */
    <X> void assertObject(final String message, final X actual, final IProperty... excludes);

}
