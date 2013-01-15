package eu.execom.testutil.property;

import org.junit.Test;

import eu.execom.testutil.AbstractExecomAssertTest;
import eu.execom.testutil.model.EntityTierOneType;

/**
 * Tests for {@link Property}.
 */
public class PropertyFactoryTest extends AbstractExecomAssertTest {

    private static String TEST = "test";

    /**
     * Test for ignored when varargs are passed.
     */
    @Test
    public void testIgnoredVarargs() {
        // setup
        final String[] properties = new String[] {EntityTierOneType.PROPERTY, EntityTierOneType.ID};

        // method
        final MultiProperty multi = PropertyFactory.ignored(properties);

        // assert
        assertEquals(properties.length, multi.getProperties().size());

        for (int i = 0; i < properties.length; i++) {
            assertTrue(multi.getProperties().get(i) instanceof IgnoreProperty);
            assertEquals(properties[i], multi.getProperties().get(i).getPath());
        }
    }

    /**
     * Test for nulll when varargs are passed.
     */
    @Test
    public void testNulllVarargs() {
        // setup
        final String[] properties = new String[] {EntityTierOneType.PROPERTY, EntityTierOneType.ID};

        // method
        final MultiProperty multi = PropertyFactory.nulll(properties);

        // assert
        assertEquals(properties.length, multi.getProperties().size());

        for (int i = 0; i < properties.length; i++) {
            assertTrue(multi.getProperties().get(i) instanceof NullProperty);
            assertEquals(properties[i], multi.getProperties().get(i).getPath());
        }
    }

    /**
     * Test for notNull when varargs are passed.
     */
    @Test
    public void testNotNullVarargs() {
        // setup
        final String[] properties = new String[] {EntityTierOneType.PROPERTY, EntityTierOneType.ID};

        // method
        final MultiProperty multi = PropertyFactory.notNull(properties);

        // assert
        assertEquals(properties.length, multi.getProperties().size());

        for (int i = 0; i < properties.length; i++) {
            assertTrue(multi.getProperties().get(i) instanceof NotNullProperty);
            assertEquals(properties[i], multi.getProperties().get(i).getPath());
        }
    }

    /**
     * Test for {@link PropertyFactory#notNull(String)}.
     */
    @Test
    public void testNotNull() {
        // method
        final NotNullProperty notNullProperty = PropertyFactory.notNull(EntityTierOneType.PROPERTY);

        // assert
        assertTrue(notNullProperty instanceof NotNullProperty);
        assertEquals(EntityTierOneType.PROPERTY, notNullProperty.getPath());
    }

    /**
     * Test for {@link PropertyFactory#nulll(String)}.
     */
    @Test
    public void testNulll() {
        // method
        final NullProperty nullProperty = PropertyFactory.nulll(EntityTierOneType.PROPERTY);

        // assert
        assertTrue(nullProperty instanceof NullProperty);
        assertEquals(EntityTierOneType.PROPERTY, nullProperty.getPath());
    }

    /**
     * Test for {@link PropertyFactory#ignored(String)}.
     */
    @Test
    public void testIgnored() {
        // method
        final IgnoreProperty ignoreProperty = PropertyFactory.ignored(EntityTierOneType.PROPERTY);

        // assert
        assertTrue(ignoreProperty instanceof IgnoreProperty);
        assertEquals(EntityTierOneType.PROPERTY, ignoreProperty.getPath());
    }

    /**
     * Test for {@link PropertyFactory#changed(String, Object)}
     */
    @Test
    public void testChanged() {
        // method
        final ChangedProperty<String> changedProperty = PropertyFactory.changed(EntityTierOneType.PROPERTY, TEST);

        // assert
        assertTrue(changedProperty instanceof ChangedProperty<?>);
        assertEquals(TEST, changedProperty.getExpectedValue());
        assertEquals(EntityTierOneType.PROPERTY, changedProperty.getPath());
    }
}