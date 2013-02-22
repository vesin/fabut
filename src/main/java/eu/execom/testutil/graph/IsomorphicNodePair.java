package eu.execom.testutil.graph;

import eu.execom.testutil.Pair;

/**
 * Class representing object pair from {@link IsomorphicGraph}.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class IsomorphicNodePair extends Pair {

    public IsomorphicNodePair(final Object expected, final Object actual) {
        super(expected, actual);
    }

    @Override
    public boolean equals(final Object arg0) {
        try {
            final IsomorphicNodePair node = (IsomorphicNodePair) arg0;
            return node.getActual() == getActual() && node.getExpected() == getExpected();
        } catch (final Exception e) {
            return false;
        }
    }

}
