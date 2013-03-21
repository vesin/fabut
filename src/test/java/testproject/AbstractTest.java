package testproject;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;

import eu.execom.fabut.Fabut;
import eu.execom.fabut.IFabutTest;
import eu.execom.fabut.model.TierOneType;

public abstract class AbstractTest implements IFabutTest {

    @Override
    @Before
    public void beforeTest() {
        Fabut.beforeTest(this);
    }

    @Override
    public void afterTest() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Class<?>> getComplexTypes() {
        final List<Class<?>> complexTypes = new LinkedList<Class<?>>();
        complexTypes.add(TierOneType.class);
        return complexTypes;
    }

    @Override
    public List<Class<?>> getIgnoredTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void customAssertEquals(final Object expected, final Object actual) {
        // TODO Auto-generated method stub

    }

}
