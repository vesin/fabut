package eu.execom.testutil;

import java.util.List;

@SuppressWarnings("rawtypes")
public interface ITestUtil {

    List findAll(Class<?> clazz);

    Object findById(final Class<?> entityClass, final Object id);

    void beforeTest();

    void afterTest();

    List<Class<?>> getEntityTypes();

    List<Class<?>> getComplexTypes();

    List<Class<?>> getIgnoredTypes();

    void customAssertEquals(Object expected, Object actual);
}
