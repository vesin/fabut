package eu.execom.testutil.pair;

import eu.execom.testutil.enums.ObjectType;

//TODO comments
public class AssertPair extends Pair {
    private ObjectType objectType;

    private boolean property;

    public AssertPair(final Object expected, final Object actual, final ObjectType objectType) {
        super(expected, actual);
        this.objectType = objectType;
        property = false;
    }

    public AssertPair(final Object expected, final Object actual, final ObjectType objectType, final boolean property) {
        super(expected, actual);
        this.objectType = objectType;
        this.property = property;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(final ObjectType objectType) {
        this.objectType = objectType;
    }

    public boolean isProperty() {
        return property;
    }

    public void setProperty(final boolean property) {
        this.property = property;
    }

}
