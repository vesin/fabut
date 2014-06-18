package eu.execom.fabut.property;

/**
 * Represents property with {@link scala.Some} value.
 * 
 * @author nolah
 * 
 */
public class SomeProperty extends AbstractSingleProperty {

	public SomeProperty(String path) {
		super(path);
	}

	@Override
	public ISingleProperty getCopy() {
		return new SomeProperty(getPath());
	}
}
