package eu.execom.fabut;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import eu.execom.fabut.property.ISingleProperty;

/**
 * Tests methods from {@link FabutObjectAssert}.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class FabutObjectAssertTest extends AbstractFabutObjectAssertTest {

	@Test
	public void testGetPropertyFromList() {
		// setup
		String propertyPath = "eu$execom$fabut$model$Employee$$_id";
		List<ISingleProperty> properties = new LinkedList<ISingleProperty>();
		properties.add(Fabut.value("_id", 1l));

		// method
		ISingleProperty property = getFabutObjectAssert().getPropertyFromList(
				propertyPath, properties);

		// assert
		assertEquals("_id", property.getPath());
	}
}
