package eu.execom.testutil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import eu.execom.testutil.model.EntityTierOneType;
import eu.execom.testutil.model.EntityTierTwoType;
import eu.execom.testutil.model.Type;

/**
 * TODO add comments
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class AbstractExecomRepositoryAssertTest extends AbstractExecomRepositoryAssert<Type, Integer> {
    // mock lists
    private List<EntityTierOneType> list1 = new ArrayList<EntityTierOneType>();
    private List<EntityTierTwoType> list2 = new ArrayList<EntityTierTwoType>();

    public AbstractExecomRepositoryAssertTest() {

    }

    @Override
    public List<?> findAll(final Class<?> entityClass) {
        if (entityClass == EntityTierOneType.class) {
            return list1;
        }
        if (entityClass == EntityTierTwoType.class) {
            return list2;
        }
        return null;
    }

    @Override
    public Object findById(final Class<?> entityClass, final Object id) {
        if (entityClass == EntityTierOneType.class) {
            for (final EntityTierOneType entity : list1) {
                if (entity.getId() == id) {
                    return entity;
                }
            }
        }
        return null;
    }

    @Override
    public void customAssertEquals(final Object expected, final Object actual) {
        Assert.assertEquals(expected, actual);
    }

    public List<EntityTierOneType> getList1() {
        return list1;
    }

    public void setList1(final List<EntityTierOneType> list1) {
        this.list1 = list1;
    }

    public List<EntityTierTwoType> getList2() {
        return list2;
    }

    public void setList2(final List<EntityTierTwoType> list2) {
        this.list2 = list2;
    }

    // @Override
    // @After
    // public void afterTest() {
    // // TestUtilAssert.afterTest();
    // }
    //
    // @Override
    // @Before
    // public void beforeTest() {
    // TestUtilAssert.beforeTest();
    // }
    //
    // @Override
    // public List<Class<?>> getEntityTypes() {
    // final List<Class<?>> entityTypes = new LinkedList<Class<?>>();
    // entityTypes.add(EntityTierTwoType.class);
    // entityTypes.add(EntityTierOneType.class);
    // return entityTypes;
    // }
    //
    // @Override
    // public List<Class<?>> getComplexTypes() {
    // final List<Class<?>> complexTypes = new LinkedList<Class<?>>();
    // complexTypes.add(EntityTierTwoType.class);
    // complexTypes.add(EntityTierOneType.class);
    // complexTypes.add(A.class);
    // complexTypes.add(B.class);
    // complexTypes.add(C.class);
    // return complexTypes;
    // }
    //
    // @Override
    // public List<Class<?>> getIgnoredTypes() {
    // final List<Class<?>> ignoredTypes = new LinkedList<Class<?>>();
    // return ignoredTypes;
    // }

}
