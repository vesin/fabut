package eu.execom.fabut;

import org.junit.After;
import org.junit.Before;

/**
 * TODO add comments.
 * 
 * @author Dusko Vesin
 * 
 */
public abstract class AbstractFabutTest implements IFabutTest {

    @Before
    @Override
    public void beforeTest() {
        Fabut.beforeTest(this);
    };

    @After
    @Override
    public void afterTest() {
        Fabut.afterTest();
    }

}
