package testproject;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;

import eu.execom.fabut.Fabut;
import eu.execom.fabut.IRepositoryFabutTest;
import eu.execom.fabut.model.EntityTierOneType;
import eu.execom.fabut.model.TierOneType;

public abstract class AbstractTest implements IRepositoryFabutTest {

    public boolean ok = true;

    @Override
    @Before
    public void beforeTest() {
        Fabut.beforeTest(this);
    }

    @Override
    @After
    public void afterTest() {
        Fabut.afterTest();

    }

    @Override
    public List<Class<?>> getComplexTypes() {
        final List<Class<?>> complexTypes = new LinkedList<Class<?>>();
        complexTypes.add(TierOneType.class);
        return complexTypes;
    }

    @Override
    public List<Class<?>> getIgnoredTypes() {
        return new LinkedList<Class<?>>();
    }

    @Override
    public void customAssertEquals(final Object expected, final Object actual) {
        Assert.assertEquals(expected, actual);
    }

    @Override
    public List<Object> findAll(final Class<?> clazz) {
        if (clazz == EntityTierOneType.class) {
            if (ok) {
                final List<Object> entityTierOneTypes = new LinkedList<Object>();
                entityTierOneTypes.add(new EntityTierOneType("a", 1));
                return entityTierOneTypes;
            } else {
                return new LinkedList<Object>();
            }
        }

        return null;
    }

    @Override
    public Object findById(final Class<?> entityClass, final Object id) {
        if (((Integer) id).intValue() == 1) {
            return new EntityTierOneType("a", 1);
        }
        return null;
    }

    @Override
    public List<Class<?>> getEntityTypes() {
        final List<Class<?>> entityTypes = new LinkedList<Class<?>>();
        entityTypes.add(EntityTierOneType.class);
        return entityTypes;
    }

}
