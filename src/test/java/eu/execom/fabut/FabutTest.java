package eu.execom.fabut;

import junit.framework.AssertionFailedError;

import org.junit.Test;

import eu.execom.fabut.model.EntityTierOneType;
import eu.execom.fabut.model.NoDefaultConstructorEntity;
import eu.execom.fabut.model.TierOneType;
import eu.execom.fabut.property.PropertyFactory;

public class FabutTest extends AbstractFabutRepositoryAssertTest {

    /**
     * Test for {@link Fabut#beforeTest(Object)} if it throws {@link IllegalStateException} if specified test instance
     * doesn't implement {@link IFabutTest} or {@link IRepositoryFabutTest}.
     */
    @Test(expected = IllegalStateException.class)
    public void testBeforeTest() {
        // method
        Fabut.beforeTest(new Object());
    }

    /**
     * Test for {@link Fabut#afterTest()} when snapshot matches after state.
     */
    @Test
    public void testAfterTestSucess() {
        // setup
        Fabut.beforeTest(this);
        Fabut.takeSnapshot();

        // method
        Fabut.afterTest();
    }

    /**
     * Test for {@link Fabut#afterTest()} when snapshot doesn't match after state.
     */
    @Test(expected = AssertionFailedError.class)
    public void testAfterTestFail() {
        // setup
        Fabut.beforeTest(this);
        Fabut.takeSnapshot();
        final EntityTierOneType entityTierOneType = new EntityTierOneType("test", 1);
        getEntityTierOneTypes().add(entityTierOneType);

        // method
        Fabut.afterTest();
    }

    /**
     * Test for {@link Fabut#takeSnapshot()} when there are entites in repository that cannot be copied.
     */
    @Test(expected = AssertionFailedError.class)
    public void testTakeSnapshotFail() {
        // setup
        Fabut.beforeTest(this);
        getNoDefaultConstructorEntities().add(new NoDefaultConstructorEntity("test", 1));

        // method
        Fabut.takeSnapshot();
    }

    /**
     * Test for {@link Fabut#takeSnapshot()} when repository can be copied.
     */
    @Test
    public void testTakeSnapshotSuccess() {
        // setup
        Fabut.beforeTest(this);
        final EntityTierOneType entityTierOneType = new EntityTierOneType("test", 1);
        getEntityTierOneTypes().add(entityTierOneType);
        Fabut.takeSnapshot();

        // method
        Fabut.afterTest();
    }

    /**
     * Test for {@link Fabut#assertObject(Object, eu.execom.fabut.property.IProperty...)} when object is complex type
     * and can be asserted.
     */
    @Test
    public void testAssertObjectWithComplexType() {
        // setup
        Fabut.beforeTest(this);
        final TierOneType object = new TierOneType("test");

        // method
        Fabut.assertObject(object, PropertyFactory.value(TierOneType.PROPERTY, "test"));

    }

    /**
     * Test for {@link Fabut#assertObject(Object, eu.execom.fabut.property.IProperty...)} when object is entity type and
     * can be asserted.
     */
    @Test
    public void testAssertObjectWithEntityType() {
        // setup
        Fabut.beforeTest(this);
        final EntityTierOneType entity = new EntityTierOneType();
        entity.setProperty("test");
        entity.setId(1);

        // method
        Fabut.takeSnapshot();
        getEntityTierOneTypes().add(entity);
        Fabut.assertObject(entity, PropertyFactory.value(EntityTierOneType.ID, 1),
                PropertyFactory.value(EntityTierOneType.PROPERTY, "test"));

        Fabut.afterTest();

    }

    /**
     * Test for {@link Fabut#assertObject(Object, eu.execom.fabut.property.IProperty...)} when object is entity and
     * cannot be asserted.
     */
    @Test(expected = AssertionFailedError.class)
    public void testAssertObjectWithEntityTypeFail() {
        // setup
        Fabut.beforeTest(this);
        final EntityTierOneType entity = new EntityTierOneType();
        entity.setProperty("test");
        entity.setId(1);

        // method
        Fabut.takeSnapshot();
        getEntityTierOneTypes().add(entity);
        Fabut.assertObject(entity, PropertyFactory.value(EntityTierOneType.ID, 1),
                PropertyFactory.value(EntityTierOneType.PROPERTY, "fail"));

    }
}
