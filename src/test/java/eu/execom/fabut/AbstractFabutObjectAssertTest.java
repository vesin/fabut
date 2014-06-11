package eu.execom.fabut;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;

/**
 * TODO add comments
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public abstract class AbstractFabutObjectAssertTest extends Assert implements
		IFabutTest {

	private FabutObjectAssert fabutObjectAssert;

	/**
	 * Default constructor.
	 */
	public AbstractFabutObjectAssertTest() {
		super();
	}

	@Override
	@Before
	public void fabutBeforeTest() {
		fabutObjectAssert = new FabutObjectAssert(this);
	}

	@Override
	public void fabutAfterTest() {
	}

	@Override
	public List<Class<?>> getComplexTypes() {
		final List<Class<?>> complexTypes = new LinkedList<Class<?>>();
		return complexTypes;
	}

	@Override
	public List<Class<?>> getIgnoredTypes() {
		final List<Class<?>> ignoredTypes = new LinkedList<Class<?>>();
		return ignoredTypes;
	}

	@Override
	public void customAssertEquals(final Object expected, final Object actual) {
		Assert.assertEquals(expected, actual);

	}

	public FabutObjectAssert getFabutObjectAssert() {
		return fabutObjectAssert;
	}

}
