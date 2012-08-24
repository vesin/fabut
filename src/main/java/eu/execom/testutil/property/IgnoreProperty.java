package eu.execom.testutil.property;

/**
 * The Class IgnoreProperty. {@link Property} extension with limited functionality only to mark property as ignored for
 * testing.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class IgnoreProperty extends Property {

    /**
     * Ignore property default constructor.
     * 
     * @param path
     *            property path
     */
    protected IgnoreProperty(final String path) {
        super(path);
    }

    @Override
    public IProperty getCopy() {
        return new IgnoreProperty(getPath());
    }

}
