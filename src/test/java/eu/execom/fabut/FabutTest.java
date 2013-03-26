package eu.execom.fabut;

import org.junit.Test;

import eu.execom.fabut.model.EntityTierOneType;
import eu.execom.fabut.property.IgnoredProperty;
import eu.execom.fabut.property.MultiProperties;
import eu.execom.fabut.property.NotNullProperty;
import eu.execom.fabut.property.NullProperty;
import eu.execom.fabut.property.Property;

public class FabutTest extends org.junit.Assert {

    private static final String TEST = "test";

    /**
     * Test for ignored when varargs are passed.
     */
    @Test
    public void testIgnoredVarargs() {
        // setup
        final String[] properties = new String[] {EntityTierOneType.PROPERTY, EntityTierOneType.ID};

        // method
        final MultiProperties multi = Fabut.ignored(properties);

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
        final MultiProperties multi = Fabut.isNull(properties);

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
        final MultiProperties multi = Fabut.notNull(properties);

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
        final NotNullProperty notNullProperty = Fabut.notNull(EntityTierOneType.PROPERTY);

        // assert
        assertTrue(notNullProperty instanceof NotNullProperty);
        assertEquals(EntityTierOneType.PROPERTY, notNullProperty.getPath());
    }

    /**
     * Test for {@link PropertyFactory#isNull(String)}.
     */
    @Test
    public void testNulll() {
        // method
        final NullProperty nullProperty = Fabut.isNull(EntityTierOneType.PROPERTY);

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
        final IgnoredProperty ignoreProperty = Fabut.ignored(EntityTierOneType.PROPERTY);

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
        final Property<String> changedProperty = Fabut.value(EntityTierOneType.PROPERTY, TEST);

        // assert
        assertTrue(changedProperty instanceof Property<?>);
        assertEquals(TEST, changedProperty.getValue());
        assertEquals(EntityTierOneType.PROPERTY, changedProperty.getPath());
    }

}
