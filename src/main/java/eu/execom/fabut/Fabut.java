package eu.execom.fabut;

import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;
import eu.execom.fabut.enums.AssertType;
import eu.execom.fabut.property.IProperty;
import eu.execom.fabut.property.ISingleProperty;
import eu.execom.fabut.property.IgnoredProperty;
import eu.execom.fabut.property.MultiProperties;
import eu.execom.fabut.property.NotNullProperty;
import eu.execom.fabut.property.NullProperty;
import eu.execom.fabut.property.Property;
import eu.execom.fabut.report.FabutReportBuilder;
import eu.execom.fabut.util.ConversionUtil;

/**
 * Set of method for advanced asserting.
 * 
 * @author Dusko Vesin
 * 
 */
@SuppressWarnings({"rawtypes"})
public final class Fabut {

    private static FabutRepositoryAssert fabutAssert = null;
    private static AssertType assertType;

    /**
     * Private constructor to forbid instancing this class.
     */
    private Fabut() {

    }

    /**
     * This method needs to be called in @Before method of a test in order for {@link Fabut} to work.
     * 
     * @param testInstance
     *            the test instance
     */
    public static synchronized void beforeTest(final Object testInstance) {
        assertType = ConversionUtil.getAssertType(testInstance);
        switch (assertType) {
        case OBJECT_ASSERT:
            fabutAssert = new FabutRepositoryAssert((IFabutTest) testInstance);
            break;
        case REPOSITORY_ASSERT:
            fabutAssert = new FabutRepositoryAssert((IFabutRepositoryTest) testInstance);
            break;
        case UNSUPPORTED_ASSERT:
            throw new IllegalStateException("This test must implement IFabutAssert or IRepositoryFabutAssert");
        default:
            throw new IllegalStateException("Unsupported assert type: " + assertType);
        }
    }

    /**
     * This method needs to be called in @After method of a test in order for {@link Fabut} to work.
     */
    public static void afterTest() {
        // TODO this should be reworked once parameter snapshot is ready
        // fabutAssert.assertParameterSnapshot();

        if (assertType == AssertType.REPOSITORY_ASSERT) {

            final FabutReportBuilder report = new FabutReportBuilder();
            if (!fabutAssert.assertDbSnapshot(report)) {

                throw new AssertionFailedError(report.getMessage());
            }
        }

    }

    /**
     * Creates repository snapshot so it can be asserted with after state after the test execution.
     */
    public static void takeSnapshot() {
        checkIfRepositoryAssert();

        final FabutReportBuilder report = new FabutReportBuilder();
        if (!fabutAssert.takeSnapshot(report)) {

            throw new AssertionFailedError(report.getMessage());
        }

    }

    /**
     * Asserts object with expected properties.
     * 
     * @param message
     *            custom message to be added on top of the report
     * @param object
     *            the object that needs to be asserted
     * @param properties
     *            expected properties for asserting object
     */
    public static void assertObject(final String message, final Object object, final IProperty... properties) {

        final FabutReportBuilder report = new FabutReportBuilder(message);
        if (!fabutAssert.assertObjectWithProperties(report, object, fabutAssert.extractProperties(properties))) {

            throw new AssertionFailedError(report.getMessage());
        }
    }

    /**
     * Asserts object with expected properties.
     * 
     * @param expected
     *            the expected
     * @param properties
     *            expected properties for asserting object
     */
    public static void assertObject(final Object expected, final IProperty... properties) {
        assertObject("", expected, properties);
    }

    /**
     * Asserts two objects.
     * 
     * @param message
     *            custom message to be added on top of the report
     * @param expected
     *            the expected object
     * @param actual
     *            the actual object
     * @param expectedChanges
     *            property difference between expected and actual
     */
    public static void assertObjects(final String message, final Object expected, final Object actual,
            final IProperty... expectedChanges) {

        final FabutReportBuilder report = new FabutReportBuilder(message);
        if (!fabutAssert.assertObjects(report, expected, actual, fabutAssert.extractProperties(expectedChanges))) {

            throw new AssertionFailedError(report.getMessage());
        }
    }

    /**
     * Asserts two objects.
     * 
     * @param expected
     *            the expected object
     * @param actual
     *            the actual object
     * @param expectedChanges
     *            property difference between expected and actual
     */
    public static void assertObjects(final Object expected, final Object actual, final IProperty... expectedChanges) {
        assertObjects("", expected, actual, expectedChanges);
    }

    /**
     * Asserts list of expected and actual objects.
     * 
     * @param expected
     *            the expected list
     * @param actuals
     *            the actual array
     */
    public static void assertLists(final List expected, final Object... actuals) {
        assertObjects("", expected, ConversionUtil.createListFromArray(actuals));
    }

    /**
     * Asserts entity with one saved in snapshot.
     * 
     * @param entity
     *            the entity
     * @param expectedChanges
     *            properties changed after the snapshot has been taken
     */
    public static void assertEntityWithSnapshot(final Object entity, final IProperty... expectedChanges) {
        checkIfEntity(entity);
        checkIfValidSnapshot();

        final FabutReportBuilder report = new FabutReportBuilder();
        if (!fabutAssert.assertEntityWithSnapshot(report, entity, fabutAssert.extractProperties(expectedChanges))) {

            throw new AssertionFailedError(report.getMessage());
        }
    }

    /**
     * Marks object as asserted.
     * 
     * @param entity
     *            the entity
     */
    public static void markAsserted(final Object entity) {
        checkIfEntity(entity);
        checkIfValidSnapshot();

        final FabutReportBuilder report = new FabutReportBuilder();
        if (!fabutAssert.markAsAsserted(report, entity, entity.getClass())) {

            throw new AssertionFailedError(report.getMessage());
        }
    }

    /**
     * Assert entity as deleted. It will fail if entity can still be found in snapshot.
     * 
     * @param entity
     *            the entity
     */
    public static void assertEntityAsDeleted(final Object entity) {
        checkIfEntity(entity);
        checkIfValidSnapshot();

        final FabutReportBuilder report = new FabutReportBuilder();
        if (!fabutAssert.assertEntityAsDeleted(report, entity)) {

            throw new AssertionFailedError(report.getMessage());
        }
    }

    /**
     * Ignores the entity.
     * 
     * @param entity
     *            the entity
     */
    public static void ignoreEntity(final Object entity) {
        checkIfEntity(entity);
        checkIfValidSnapshot();

        final FabutReportBuilder report = new FabutReportBuilder();
        if (!fabutAssert.ignoreEntity(report, entity)) {

            throw new AssertionFailedError(report.getMessage());
        }
    }

    private static void checkIfValidSnapshot() {
        if (!fabutAssert.isRepositoryValid()) {
            throw new IllegalStateException("takeSnapshot neeeds to be called before calling this method");
        }
    }

    /**
     * Checks if specified object is entity.
     * 
     * @param entity
     *            the entity
     */
    private static void checkIfEntity(final Object entity) {
        checkIfRepositoryAssert();

        if (!fabutAssert.getEntityTypes().contains(entity.getClass())) {

            throw new IllegalStateException(entity.getClass() + " is not registered as entity type");
        }
    }

    /**
     * Checks if current test is repository test.
     */
    private static void checkIfRepositoryAssert() {

        if (assertType != AssertType.REPOSITORY_ASSERT) {

            throw new IllegalStateException("Test must implement IRepositoryFabutAssert");
        }
    }

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
    public static NullProperty isNull(final String path) {
        return new NullProperty(path);
    }

    /**
     * Create {@link NullProperty} with provided parameters.
     * 
     * @param paths
     *            property paths.
     * @return created objects.
     */
    public static MultiProperties isNull(final String... paths) {
        final List<ISingleProperty> properties = new ArrayList<ISingleProperty>();

        for (final String path : paths) {
            properties.add(isNull(path));
        }

        return new MultiProperties(properties);
    }
}
