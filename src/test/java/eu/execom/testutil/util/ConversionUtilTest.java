package eu.execom.testutil.util;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import eu.execom.testutil.property.IProperty;
import eu.execom.testutil.property.NotNullProperty;
import eu.execom.testutil.property.NullProperty;
import eu.execom.testutil.property.Property;

/**
 * Tests for {@link ConversionUtil}.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class ConversionUtilTest extends Assert {

    private static final String TEST = "test";

    /**
     * Test for createListFromVaragrs of {@link ConversionUtil} when there are two properties specified in particular
     * order.s
     */
    @Test
    public void testCreateListFromVarargsTwoProperties() {
        // setup
        final NullProperty nullProperty = Property.nulll(TEST);
        final NotNullProperty notNullProperty = Property.notNull(TEST);

        // method
        final List<Property> properties = ConversionUtil.createListFromArray(nullProperty, notNullProperty);

        // assert
        assertEquals(2, properties.size());
        assertEquals(nullProperty, properties.get(0));
        assertEquals(notNullProperty, properties.get(1));
    }

    /**
     * Test for createListFromVaragrs of {@link ConversionUtil} when there are none properties specified.
     */
    @Test
    public void testCreateListFromVarargsNoProperties() {
        // method
        final List<IProperty> properties = ConversionUtil.createListFromArray();

        // assert
        assertEquals(0, properties.size());
    }

}
