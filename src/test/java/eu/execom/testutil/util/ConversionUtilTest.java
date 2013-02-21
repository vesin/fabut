package eu.execom.testutil.util;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import eu.execom.testutil.property.AbstractSingleProperty;
import eu.execom.testutil.property.ISingleProperty;
import eu.execom.testutil.property.NotNullProperty;
import eu.execom.testutil.property.NullProperty;
import eu.execom.testutil.property.PropertyFactory;

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
     * Test for createListFromVaragrs of {@link ConversionUtil} when there are two properties specified in particular.
     * order.s
     */
    @Test
    public void testCreateListFromVarargsTwoProperties() {
        // setup
        final NullProperty nullProperty = PropertyFactory.nulll(TEST);
        final NotNullProperty notNullProperty = PropertyFactory.notNull(TEST);

        // method
        final List<AbstractSingleProperty> properties = ConversionUtil.createListFromArray(nullProperty,
                notNullProperty);

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
        final List<ISingleProperty> properties = ConversionUtil.createListFromArray();

        // assert
        assertEquals(0, properties.size());
    }

}
