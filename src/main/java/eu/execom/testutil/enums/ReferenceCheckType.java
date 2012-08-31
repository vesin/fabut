package eu.execom.testutil.enums;

/**
 * Return result for reference check.
 * 
 * TODO add comment, for what we need this entity
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public enum ReferenceCheckType {

    EQUAL_REFERENCE(true),

    EXCLUSIVE_NULL(false),

    COMPLEX_ASSERT(true);

    private boolean assertResult;

    private ReferenceCheckType(final boolean assertResult) {
        this.assertResult = assertResult;
    }

    public boolean getAssertResult() {
        return assertResult;
    }
}
