package eu.execom.fabut;

import java.util.List;

import junit.framework.AssertionFailedError;
import eu.execom.fabut.enums.AssertType;
import eu.execom.fabut.property.IProperty;
import eu.execom.fabut.report.FabutReportBuilder;
import eu.execom.fabut.util.ConversionUtil;

/**
 * Set of method for advanced asserting.
 * 
 * @author Dusko Vesin
 * 
 */
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
            fabutAssert = new FabutRepositoryAssert((IRepositoryFabutTest) testInstance);
            break;
        case UNSUPPORTED_ASSERT:
            throw new IllegalStateException("This test must implement IFabutAssert or IRepositoryFabutAssert");
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
            final boolean ok = fabutAssert.assertDbSnapshot(report);
            if (!ok) {
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
        final boolean ok = fabutAssert.assertObjectWithProperties(report, object,
                fabutAssert.extractProperties(properties));
        if (!ok) {
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
     * @param properties
     *            property difference between expected and actual
     */
    public static void assertObjects(final String message, final Object expected, final Object actual,
            final IProperty... properties) {
        final FabutReportBuilder report = new FabutReportBuilder(message);
        final boolean ok = fabutAssert.assertObjects(report, expected, actual,
                fabutAssert.extractProperties(properties));
        if (!ok) {
            throw new AssertionFailedError(report.getMessage());
        }
    }

    /**
     * Asserts two objects
     * 
     * @param expected
     *            the expected object
     * @param actual
     *            the actual object
     * @param properties
     *            property difference between expected and actual
     */
    public static void assertObjects(final Object expected, final Object actual, final IProperty... excludes) {
        assertObjects("", expected, actual, excludes);
    }

    /**
     * Asserts list of expected and actual objects
     * 
     * @param expected
     *            the expected list
     * @param actual
     *            the actual array
     */
    public static void assertObjects(final List<Object> expected, final Object... actuals) {
        assertObjects(expected, ConversionUtil.createListFromArray(actuals));
    }

    /**
     * Asserts entity with one saved in snapshot.
     * 
     * @param entity
     *            the entity
     * @param properties
     *            properties changed after the snapshot has been taken
     */
    public static void assertEntityWithSnapshot(final Object entity, final IProperty... properties) {
        checkIfEntity(entity);
        final FabutReportBuilder report = new FabutReportBuilder();
        if (!fabutAssert.assertEntityWithSnapshot(new FabutReportBuilder(), entity,
                fabutAssert.extractProperties(properties))) {
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
        final FabutReportBuilder report = new FabutReportBuilder();
        if (!fabutAssert.ignoreEntity(report, entity)) {
            throw new AssertionFailedError(report.getMessage());
        }
    }

    /**
     * Checks if specified object is entity.
     * 
     * @param entity
     *            the entity
     */
    public static void checkIfEntity(final Object entity) {
        checkIfRepositoryAssert();
        if (!fabutAssert.getEntityTypes().contains(entity)) {
            throw new IllegalStateException(entity.getClass() + " is not registered as entity type");
        }
    }

    /**
     * Checks if current test is repository test.
     */
    public static void checkIfRepositoryAssert() {
        if (assertType != AssertType.REPOSITORY_ASSERT) {
            throw new IllegalStateException("Test must implement IRepositoryFabutAssert");
        }
    }
}
