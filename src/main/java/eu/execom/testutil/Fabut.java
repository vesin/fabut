package eu.execom.testutil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.execom.testutil.property.IProperty;
import eu.execom.testutil.property.ISingleProperty;
import eu.execom.testutil.report.FabutReportBuilder;
import eu.execom.testutil.util.ConversionUtil;
import eu.execom.testutil.util.ReflectionUtil;

/**
 * TODO add comments.
 * 
 * @author Dusko Vesin
 * 
 */
// TODO for all methods check can method be called
public class Fabut {

    private static Class<?> testClass = null;
    private static FabutRepositoryAssert abstractExecomRepositoryAssert = null;

    public static void takeSnapshot() {
        abstractExecomRepositoryAssert.takeSnapshot();
    }

    /**
     * Asserts current database snapshot with one previously taken.
     */
    public static void assertDbState() {
        abstractExecomRepositoryAssert.assertDbState();
    }

    // TODO why is this published ? End user shouldnt be able to us it.
    public static final List<Object> findAll(final Class<?> entityClass) {
        return ReflectionUtil.findAll(testClass, entityClass);
    }

    // TODO why is this published ? End user shouldnt be able to us it.
    public static Object findById(final Class<?> entityClass, final Object id) {
        return ReflectionUtil.findById(testClass, entityClass, id);
    }

    public static void beforeTest() {
        abstractExecomRepositoryAssert = new FabutRepositoryAssert();
        // TODO get instance of the class not class
        testClass = ReflectionUtil.getTestClassFromStackTrace();
        // TODO dont create instance of test class every time, pass it as parameter
        // TODO throw IllegalStateException if getComplexTypes returns null
        abstractExecomRepositoryAssert.setComplexTypes(ReflectionUtil.getComplexTypes(testClass));
        // TODO throw IllegalStateException if getIgnoredTypes returns null
        abstractExecomRepositoryAssert.setIgnoredTypes(ReflectionUtil.getIgnoredTypes(testClass));
        abstractExecomRepositoryAssert.initParametersSnapshot();

        // TODO Move to RepositoryTestUtil
        // TODO throw IllegalStateException if getEntityTypes returns null
        abstractExecomRepositoryAssert.setEntityTypes(ReflectionUtil.getEntityTypes(testClass));
        abstractExecomRepositoryAssert.initDbSnapshot();
    }

    public static void afterTest() {
        abstractExecomRepositoryAssert.assertSnapshot();
    }

    public static void assertEntityWithSnapshot(final Object actual, final IProperty... properties) {
        abstractExecomRepositoryAssert.assertEntityWithSnapshot(new FabutReportBuilder(), actual,
                abstractExecomRepositoryAssert.extractProperties(properties));
    }

    public static void assertObjects(final Object expected, final Object actual) {
        abstractExecomRepositoryAssert.assertObjects(new FabutReportBuilder(), expected, actual,
                new ArrayList<ISingleProperty>());
    }

    public static void assertObject(final Object expected, final IProperty... properties) {
        abstractExecomRepositoryAssert.assertObjectWithProperties(new FabutReportBuilder(), expected,
                abstractExecomRepositoryAssert.extractProperties(properties));
    }

    public static void assertObjects(final String message, final Object expected, final Object actual,
            final IProperty... excludes) {
        abstractExecomRepositoryAssert.assertObjects(new FabutReportBuilder(message), expected, actual,
                abstractExecomRepositoryAssert.extractProperties(excludes));
    }

    public static void assertObjects(final List<Object> expected, final List<Object> actual) {
        abstractExecomRepositoryAssert.assertObjects(new FabutReportBuilder(), expected, actual,
                new LinkedList<ISingleProperty>());
    }

    public static void markAsserted(final Object entity) {
        abstractExecomRepositoryAssert.markAsserted(entity);
    }

    public static void assertObjects(final List expected, final Object... actuals) {
        abstractExecomRepositoryAssert.assertObjects(new FabutReportBuilder(), expected,
                ConversionUtil.createListFromArray(actuals), new LinkedList<ISingleProperty>());
    }

    public static void assertEntityAsDeleted(final Object entity) {
        abstractExecomRepositoryAssert.assertEntityAsDeleted(entity);
    }

    public static void ignoreEntity(final Object entity) {
        abstractExecomRepositoryAssert.ignoreEntity(entity);
    }
}
