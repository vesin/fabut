package eu.execom.testutil;

public class SnapshotPair extends Pair {
    private boolean asserted;

    public SnapshotPair(final Object expected, final Object actual) {
        super(expected, actual);
        asserted = false;
    }

    public boolean isAsserted() {
        return asserted;
    }

    public void setAsserted(final boolean asserted) {
        this.asserted = asserted;
    }

}
