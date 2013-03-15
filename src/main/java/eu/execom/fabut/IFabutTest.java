package eu.execom.fabut;

import java.util.List;

/**
 * TODO add comments.
 * 
 * @author Dusko Vesin
 * 
 */
public interface IFabutTest {

    void beforeTest();

    void afterTest();

    List<Class<?>> getComplexTypes();

    List<Class<?>> getIgnoredTypes();

    void customAssertEquals(Object expected, Object actual);
}
