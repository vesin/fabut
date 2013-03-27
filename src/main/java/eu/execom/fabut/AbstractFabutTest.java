package eu.execom.fabut;

import org.junit.After;
import org.junit.Before;

/**
 * TODO add comments.
 * 
 * @author Dusko Vesin
 * 
 */
public abstract class AbstractFabutTest extends Fabut implements IFabutTest {

    public AbstractFabutTest() {
        super();
    }

    @Before
    @Override
    public void beforeTest() {
        Fabut.beforeTest(this);
    };

    @After
    public void after() {
        Fabut.afterTest();
    }

}
