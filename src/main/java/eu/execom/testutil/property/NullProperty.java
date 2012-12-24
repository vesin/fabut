package eu.execom.testutil.property;

/**
 * {@link AbstractProperty} extension with limited checking is property only different then null.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class NullProperty extends AbstractProperty {

    /**
     * Null property default constructor.
     * 
     * @param path
     *            property path
     */
    protected NullProperty(final String path) {
        super(path);
    }

    @Override
    public ISingleProperty getCopy() {
        return new NullProperty(getPath());
    }

}
