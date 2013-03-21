package eu.execom.fabut;

import java.util.List;

import junit.framework.AssertionFailedError;
import eu.execom.fabut.enums.AssertType;
import eu.execom.fabut.property.IProperty;
import eu.execom.fabut.report.FabutReportBuilder;
import eu.execom.fabut.util.ConversionUtil;

/**
 * TODO add comments.
 * 
 * @author Dusko Vesin
 * 
 */
// TODO for all methods check can method be called
public final class Fabut {

    private static FabutRepositoryAssert fabutAssert = null;
    private static AssertType assertType;

    /**
     * Private constructor to forbid instancing this class.
     */
    private Fabut() {

    }

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

    public static void takeSnapshot() {
        checkIfRepositoryAssert();
        fabutAssert.takeSnapshot();
    }

    public static void assertObject(final String message, final Object expected, final IProperty... properties) {
        final FabutReportBuilder report = new FabutReportBuilder(message);
        final boolean ok = fabutAssert.assertObjectWithProperties(report, expected,
                fabutAssert.extractProperties(properties));
        if (!ok) {
            throw new AssertionFailedError(report.getMessage());
        }
    }

    public static void assertObject(final Object expected, final IProperty... properties) {
        assertObject("", expected, properties);
    }

    public static void assertObjects(final String message, final Object expected, final Object actual,
            final IProperty... excludes) {
        final FabutReportBuilder report = new FabutReportBuilder(message);
        final boolean ok = fabutAssert.assertObjects(report, expected, actual, fabutAssert.extractProperties(excludes));
        if (!ok) {
            throw new AssertionFailedError(report.getMessage());
        }
    }

    public static void assertObjects(final Object expected, final Object actual, final IProperty... excludes) {
        assertObjects("", expected, actual, excludes);
    }

    public static void assertObjects(final List<Object> expected, final Object... actuals) {
        assertObjects(expected, ConversionUtil.createListFromArray(actuals));
    }

    public static void assertEntityWithSnapshot(final Object entity, final IProperty... properties) {
        checkIfEntity(entity);
        final FabutReportBuilder report = new FabutReportBuilder();
        if (!fabutAssert.assertEntityWithSnapshot(new FabutReportBuilder(), entity,
                fabutAssert.extractProperties(properties))) {
            throw new AssertionFailedError(report.getMessage());
        }
    }

    public static void markAsserted(final Object entity) {
        checkIfEntity(entity);
        final FabutReportBuilder report = new FabutReportBuilder();
        if (!fabutAssert.markAsAsserted(report, entity, entity.getClass())) {
            throw new AssertionFailedError(report.getMessage());
        }
    }

    public static void assertEntityAsDeleted(final Object entity) {
        checkIfEntity(entity);
        final FabutReportBuilder report = new FabutReportBuilder();
        if (!fabutAssert.assertEntityAsDeleted(report, entity)) {
            throw new AssertionFailedError(report.getMessage());
        }
    }

    public static void ignoreEntity(final Object entity) {
        checkIfEntity(entity);
        final FabutReportBuilder report = new FabutReportBuilder();
        if (!fabutAssert.ignoreEntity(report, entity)) {
            throw new AssertionFailedError(report.getMessage());
        }
    }

    private static void checkIfEntity(final Object entity) {
        checkIfRepositoryAssert();
        if (!fabutAssert.getEntityTypes().contains(entity)) {
            throw new IllegalStateException(entity.getClass() + " is not registered as entity type");
        }
    }

    private static void checkIfRepositoryAssert() {
        if (assertType != AssertType.REPOSITORY_ASSERT) {
            throw new IllegalStateException("Test must implement IRepositoryFabutAssert");
        }
    }
}
