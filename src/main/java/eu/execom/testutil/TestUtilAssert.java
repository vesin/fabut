package eu.execom.testutil;

import java.util.List;

import eu.execom.testutil.property.IProperty;
import eu.execom.testutil.property.ISingleProperty;
import eu.execom.testutil.util.ReflectionUtil;

/**
 * TODO add comments.
 * 
 * @author Dusko Vesin
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
// TODO for all methods check can method be called
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

    // TODO why is this published ? End user shouldnt be able to us it.
    public static final List<?> findAll(final Class<?> entityClass) {
        return ReflectionUtil.findAll(testClass, entityClass);
    }

    // TODO why is this published ? End user shouldnt be able to us it.
    public static Object findById(final Class<?> entityClass, final Object id) {
        return ReflectionUtil.findById(testClass, entityClass, id);
    }

    public static void beforeTest() {

        // abstractExecomRepositoryAssert = new AbstractExecomRepositoryAssert() {
        //
        // @Override
        // protected List findAll(final Class entityClass) {
        // return TestUtilAssert.findAll(entityClass);
        // }
        //
        // @Override
        // protected Object findById(final Class entityClass, final Object id) {
        // return TestUtilAssert.findById(entityClass, id);
        // }
        //
        // @Override
        // protected void customAssertEquals(final Object expected, final Object actual) {
        // Assert.assertEquals(expected, actual);
        // }
        //
        // };
        abstractExecomRepositoryAssert = new AbstractExecomRepositoryAssert();
        // TODO get instance of the class not class
        testClass = ReflectionUtil.getTestClassFromStackTrace();
        // TODO dont create instance of test class every time, pass it as parameter
        abstractExecomRepositoryAssert.setComplexTypes(ReflectionUtil.getComplexTypes(testClass));
        abstractExecomRepositoryAssert.setIgnoredTypes(ReflectionUtil.getIgnoredTypes(testClass));
        abstractExecomRepositoryAssert.initParametersSnapshot();

        // TODO Move to RepositoryTestUtil
        abstractExecomRepositoryAssert.setEntityTypes(ReflectionUtil.getEntityTypes(testClass));
        abstractExecomRepositoryAssert.initDbSnapshot();
    }

    public static void afterTest() {
        abstractExecomRepositoryAssert.assertSnapshots();
    }

    public static void assertEntityWithSnapshot(final Object actual, final ISingleProperty... properties) {
        abstractExecomRepositoryAssert.assertEntityWithSnapshot("", actual, properties);
    }

    public static void assertObjects(final Object expected, final Object actual) {
        abstractExecomRepositoryAssert.assertObjects(expected, actual);
    }

    public static void assertObject(final Object expected, final IProperty... properties) {
        abstractExecomRepositoryAssert.assertObject(expected, properties);
    }

    public static void assertObjects(final String message, final Object expected, final Object actual,
            final IProperty... excludes) {
        abstractExecomRepositoryAssert.assertObjects(message, expected, actual, excludes);
    }

    public static void assertObjects(final List<Object> expected, final List<Object> actual) {
        abstractExecomRepositoryAssert.assertObjects(expected, actual);
    }

    public static void markAsserted(final Object entity) {
        abstractExecomRepositoryAssert.markAsserted(entity);
    }

    public static void assertObjects(final List expected, final Object... actuals) {
        abstractExecomRepositoryAssert.assertObjects(expected, actuals);
    }

    public static void assertObjects(final Object expected, final Object actual, final IProperty properties) {
        abstractExecomRepositoryAssert.assertObjects(expected, actual, properties);
    }

    public static void assertEntityAsDeleted(final Object entity) {
        abstractExecomRepositoryAssert.assertEntityAsDeleted(entity);
    }
}
