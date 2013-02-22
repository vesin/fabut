package eu.execom.testutil;

import eu.execom.testutil.enums.ObjectType;

public class AssertPair extends Pair {
    private ObjectType objectType;

    public AssertPair(final Object expected, final Object actual, final ObjectType objectType) {
        super(expected, actual);
        this.objectType = objectType;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(final ObjectType objectType) {
        this.objectType = objectType;
    }

}
