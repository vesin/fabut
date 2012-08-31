package eu.execom.testutil.graph;

/**
 * Class representing object pair from {@link IsomorphicGraph}.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 * @param <T>
 */
public class IsomorphicNodePair<T> {

    private final T actual, expected;

    /**
     * Isomorphic node pair constructor with..
     * 
     * @param expected
     *            object
     * @param actual
     *            object
     */
    public IsomorphicNodePair(final T expected, final T actual) {
        this.actual = actual;
        this.expected = expected;
    }

    /**
     * Get actual.
     * 
     * @return - actual object
     */
    public T getActual() {
        return actual;
    }

    /**
     * Get expected.
     * 
     * @return - expected object
     */
    public T getExpected() {
        return expected;
    }

    @Override
    public boolean equals(final Object arg0) {
        try {
            final IsomorphicNodePair<?> node = (IsomorphicNodePair<?>) arg0;
            return node.getActual() == actual && node.getExpected() == expected;
        } catch (final Exception e) {
            return false;
        }
    }

}
