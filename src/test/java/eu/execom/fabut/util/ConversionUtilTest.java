package eu.execom.fabut.util;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import eu.execom.fabut.Fabut;
import eu.execom.fabut.property.AbstractSingleProperty;
import eu.execom.fabut.property.ISingleProperty;
import eu.execom.fabut.property.NotNullProperty;
import eu.execom.fabut.property.NullProperty;

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
        final NullProperty nullProperty = Fabut.isNull(TEST);
        final NotNullProperty notNullProperty = Fabut.notNull(TEST);

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
