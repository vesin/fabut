package eu.execom.testutil.graph;

import java.util.LinkedList;
import java.util.List;

import eu.execom.testutil.enums.NodeCheckType;

/**
 * Implementing class for {@link IsomorphicGraph} using {@link LinkedList} as container.
 * 
 * @author Dusko Vesin
 * @author Nikola Olah
 * @author Bojan Babic
 * @author Nikola Trkulja
 */
public class NodesList implements IsomorphicGraph {
    private final List<IsomorphicNodePair> isomorphicNodes;

    /**
     * Default constructor.
     */
    public NodesList() {
        isomorphicNodes = new LinkedList<IsomorphicNodePair>();
    }

    @Override
<<<<<<< HEAD
    public boolean containsPair(final Object expected, final Object actual) {
=======
    public <T> boolean containsPair(final T expected, final T actual) {
>>>>>>> origin/FABUT
        return isomorphicNodes.contains(new IsomorphicNodePair(expected, actual));
    }

    @Override
<<<<<<< HEAD
    public void addPair(final Object expected, final Object actual) {
=======
    public <T> void addPair(final T expected, final T actual) {
>>>>>>> origin/FABUT
        isomorphicNodes.add(new IsomorphicNodePair(expected, actual));
    }

    @SuppressWarnings("unchecked")
    @Override
<<<<<<< HEAD
    public Object getExpected(final Object actual) {
=======
    public <T> T getExpected(final T actual) {
>>>>>>> origin/FABUT
        for (final IsomorphicNodePair isomorphicNode : isomorphicNodes) {
            if (isomorphicNode.getActual() == actual) {
                return isomorphicNode.getExpected();
            }
        }
        return null;
    }

    @Override
    // TODO why is this always null, is this method in use
    public Object getActual(final Object expected) {
        return null;
    }

    @Override
<<<<<<< HEAD
    public boolean containsActual(final Object actual) {
=======
    public <T> boolean containsActual(final T actual) {
>>>>>>> origin/FABUT
        for (final IsomorphicNodePair isomorphicNode : isomorphicNodes) {
            if (isomorphicNode.getActual() == actual) {
                return true;
            }
        }
        return false;
    }

    @Override
<<<<<<< HEAD
    public boolean containsExpected(final Object expected) {
=======
    public <T> boolean containsExpected(final T expected) {
>>>>>>> origin/FABUT
        for (final IsomorphicNodePair isomorphicNode : isomorphicNodes) {
            if (isomorphicNode.getExpected() == expected) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NodeCheckType nodeCheck(final Object expected, final Object actual) {
        if (containsPair(expected, actual)) {
            return NodeCheckType.CONTAINS_PAIR;
        } else if (containsExpected(actual) || containsActual(expected)) {
            return NodeCheckType.SINGLE_NODE;
        }
        return NodeCheckType.NEW_PAIR;
    }

}
