package eu.execom.fabut.property;

import org.junit.Assert;
import org.junit.Test;

import eu.execom.fabut.model.EntityTierOneType;
import eu.execom.fabut.property.AbstractSingleProperty;
import eu.execom.fabut.property.IgnoredProperty;
import eu.execom.fabut.property.MultiProperties;
import eu.execom.fabut.property.NotNullProperty;
import eu.execom.fabut.property.NullProperty;
import eu.execom.fabut.property.PropertyFactory;

/**
 * Tests for {@link AbstractSingleProperty}.
 */
public class PropertyTest extends Assert {

    /**
     * Test for ignored when varargs are passed.
     */
    @Test
    public void testIgnored() {
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
     * Test for nulll when varargs are passed.
     */
    @Test
    public void testNulll() {
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
    public void testNotNull() {
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

}
