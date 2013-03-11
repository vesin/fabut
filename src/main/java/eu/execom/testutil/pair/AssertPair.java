package eu.execom.testutil.pair;

import eu.execom.testutil.enums.AssertableType;

/**
 * TODO comment. The Class AssertPair.
 */
public class AssertPair extends Pair {
    private AssertableType assertableType;

    private boolean property;

    public AssertPair(final Object expected, final Object actual, final AssertableType objectType) {
        super(expected, actual);
        assertableType = objectType;
        property = false;
    }

    public AssertPair(final Object expected, final Object actual, final AssertableType assertableType,
            final boolean property) {
        super(expected, actual);
        this.assertableType = assertableType;
        this.property = property;
    }

    public AssertableType getObjectType() {
        return assertableType;
    }

    public void setObjectType(final AssertableType objectType) {
        assertableType = objectType;
    }

    public boolean isProperty() {
        return property;
    }

    public void setProperty(final boolean property) {
        this.property = property;
    }

}
