package eu.execom.testutil;

import java.util.List;

/**
 * TODO add comments.
 * 
 * @author Dusko Vesin
 * 
 */
@SuppressWarnings("rawtypes")
public interface ITestUtil {

    void beforeTest();

    void afterTest();

    List<Class<?>> getComplexTypes();

    List<Class<?>> getIgnoredTypes();

    void customAssertEquals(Object expected, Object actual);
}
