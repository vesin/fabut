package eu.execom.testutil;

import java.util.List;

import eu.execom.testutil.property.IProperty;
import eu.execom.testutil.property.ISingleProperty;
import eu.execom.testutil.util.ReflectionUtil;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TestUtilAssert {

    private static Class<?> testClass = null;
    private static AbstractExecomRepositoryAssert abstractExecomRepositoryAssert = null;

    public static void takeSnapshot() {
        abstractExecomRepositoryAssert.takeSnapshot();
    }

    /**
     * Asserts current database snapshot with one previously taken.
     */
    public static void assertDbState() {
        abstractExecomRepositoryAssert.assertDbState();
    }

    public static final List<?> findAll(final Class<?> entityClass) {
        return ReflectionUtil.findAll(testClass, entityClass);
    }

    public static Object findById(final Class<?> entityClass, final Object id) {
        return ReflectionUtil.findById(testClass, entityClass, id);
    }

    public static void beforeTest() {
        abstractExecomRepositoryAssert = new AbstractExecomRepositoryAssert();
        TestUtilAssert.testClass = ReflectionUtil.getTestClassFromStackTrace();
        TestUtilAssert.abstractExecomRepositoryAssert.setComplexTypes(ReflectionUtil.getComplexTypes(testClass));
        TestUtilAssert.abstractExecomRepositoryAssert.setEntityTypes(ReflectionUtil.getEntityTypes(testClass));
        TestUtilAssert.abstractExecomRepositoryAssert.setIgnoredTypes(ReflectionUtil.getIgnoredTypes(testClass));
        TestUtilAssert.abstractExecomRepositoryAssert.initDbSnapshot();
        TestUtilAssert.abstractExecomRepositoryAssert.initParametersSnapshot();
    }

    public static void afterTest() {
        TestUtilAssert.abstractExecomRepositoryAssert.assertSnapshots();
    }

    public static void assertEntityWithSnapshot(final Object actual, final ISingleProperty... properties) {
        TestUtilAssert.abstractExecomRepositoryAssert.assertEntityWithSnapshot("", actual, properties);
    }

    public static void assertObjects(final Object expected, final Object actual) {
        TestUtilAssert.abstractExecomRepositoryAssert.assertObjects(expected, actual);
    }

    public static void assertObject(final Object expected, final IProperty... properties) {
        TestUtilAssert.abstractExecomRepositoryAssert.assertObject(expected, properties);
    }

    public static void assertObjects(final String message, final Object expected, final Object actual,
            final IProperty... excludes) {
        TestUtilAssert.abstractExecomRepositoryAssert.assertObjects(message, expected, actual, excludes);
    }

    public static void assertObjects(final List<Object> expected, final List<Object> actual) {
        TestUtilAssert.abstractExecomRepositoryAssert.assertObjects(expected, actual);
    }

    public static void markAsserted(final Object entity) {
        abstractExecomRepositoryAssert.markAsserted(entity);
    }

    public static void assertObjects(final List expected, final Object... actuals) {
        TestUtilAssert.abstractExecomRepositoryAssert.assertObjects(expected, actuals);
    }

    public static void assertObjects(final Object expected, final Object actual, final IProperty properties) {
        TestUtilAssert.abstractExecomRepositoryAssert.assertObjects(expected, actual, properties);
    }

    public static void assertEntityAsDeleted(final Object entity) {
        TestUtilAssert.abstractExecomRepositoryAssert.assertEntityAsDeleted(entity);
    }

    public static void ignoreEntity(final Object entity) {
        abstractExecomRepositoryAssert.ignoreEntity(entity);
    }
}
