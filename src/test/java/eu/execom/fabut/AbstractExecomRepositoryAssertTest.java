package eu.execom.fabut;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import eu.execom.fabut.FabutRepositoryAssert;
import eu.execom.fabut.model.EntityTierOneType;
import eu.execom.fabut.model.EntityTierTwoType;

/**
 * TODO add comments
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class AbstractExecomRepositoryAssertTest extends FabutRepositoryAssert {
    // mock lists
    private List<Object> list1 = new ArrayList<Object>();
    private List<Object> list2 = new ArrayList<Object>();

    public AbstractExecomRepositoryAssertTest() {

    }

    @Override
    public List<Object> findAll(final Class<?> entityClass) {
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
            for (final Object entity : list1) {
                if (((EntityTierOneType) entity).getId() == id) {
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

    public List<Object> getList1() {
        return list1;
    }

    public void setList1(final List<Object> list1) {
        this.list1 = list1;
    }

    public List<Object> getList2() {
        return list2;
    }

    public void setList2(final List<Object> list2) {
        this.list2 = list2;
    }

}
