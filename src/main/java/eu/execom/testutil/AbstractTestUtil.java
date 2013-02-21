package eu.execom.testutil;

import org.junit.After;
import org.junit.Before;

/**
 * TODO add comments.
 * 
 * @author Dusko Vesin
 * 
 */
public abstract class AbstractTestUtil implements ITestUtil {

    @Before
    @Override
    public void beforeTest() {
        TestUtilAssert.beforeTest();
    };

    @After
    @Override
    public void afterTest() {
        TestUtilAssert.afterTest();
    }

}
