package eu.execom.testutil.pair;

import eu.execom.testutil.enums.AssertableType;

/**
 * TODO comment. The Class AssertPair.
 */
public class AssertPair extends Pair {
    private AssertableType objectType;

    private boolean property;

    public AssertPair(final Object expected, final Object actual, final AssertableType objectType) {
        super(expected, actual);
        this.objectType = objectType;
        property = false;
    }

    public AssertPair(final Object expected, final Object actual, final AssertableType objectType,
            final boolean property) {
        super(expected, actual);
        this.objectType = objectType;
        this.property = property;
    }

    public AssertableType getObjectType() {
        return objectType;
    }

    public void setObjectType(final AssertableType objectType) {
        this.objectType = objectType;
    }

    public boolean isProperty() {
        return property;
    }

    public void setProperty(final boolean property) {
        this.property = property;
    }

}
