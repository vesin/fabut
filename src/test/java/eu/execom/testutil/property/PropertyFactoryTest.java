package eu.execom.testutil.property;

import org.junit.Assert;
import org.junit.Test;

import eu.execom.testutil.model.EntityTierOneType;

/**
 * Tests for {@link AbstractSingleProperty}.
 */
public class PropertyFactoryTest extends Assert {

    private static final String TEST = "test";

    /**
     * Test for ignored when varargs are passed.
     */
    @Test
    public void testIgnoredVarargs() {
        // setup
        final String[] properties = new String[] {EntityTierOneType.PROPERTY, EntityTierOneType.ID};

        // method
        final MultiProperties multi = PropertyFactory.ignored(properties);

        // assert
        assertEquals(properties.length, multi.getProperties().size());

        for (int i = 0; i < properties.length; i++) {
            assertTrue(multi.getProperties().get(i) instanceof IgnoredProperty);
            assertEquals(properties[i], multi.getProperties().get(i).getPath());
        }
    }

    /**
     * Test for null when varargs are passed.
     */
    @Test
    public void testNulllVarargs() {
        // setup
        final String[] properties = new String[] {EntityTierOneType.PROPERTY, EntityTierOneType.ID};

        // method
        final MultiProperties multi = PropertyFactory.nulll(properties);

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
        final MultiProperties multi = PropertyFactory.notNull(properties);

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
        final IgnoredProperty ignoreProperty = PropertyFactory.ignored(EntityTierOneType.PROPERTY);

        // assert
        assertTrue(ignoreProperty instanceof IgnoredProperty);
        assertEquals(EntityTierOneType.PROPERTY, ignoreProperty.getPath());
    }

    /**
     * Test for {@link PropertyFactory#value(String, Object)}.
     */
    @Test
    public void testValue() {
        // method
        final Property<String> changedProperty = PropertyFactory.value(EntityTierOneType.PROPERTY, TEST);

        // assert
        assertTrue(changedProperty instanceof Property<?>);
        assertEquals(TEST, changedProperty.geValue());
        assertEquals(EntityTierOneType.PROPERTY, changedProperty.getPath());
    }
}