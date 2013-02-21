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
    private final List<IsomorphicNodePair<?>> isomorphicNodes;

    /**
     * Default constructor.
     */
    public NodesList() {
        isomorphicNodes = new LinkedList<IsomorphicNodePair<?>>();
    }

    @Override
    public <T> boolean containsPair(final T expected, final T actual) {
        return isomorphicNodes.contains(new IsomorphicNodePair<T>(expected, actual));
    }

    @Override
    public <T> void addPair(final T expected, final T actual) {
        isomorphicNodes.add(new IsomorphicNodePair<T>(expected, actual));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getExpected(final T actual) {
        for (final IsomorphicNodePair<?> isomorphicNode : isomorphicNodes) {
            if (isomorphicNode.getActual() == actual) {
                return (T) isomorphicNode.getExpected();
            }
        }
        return null;
    }

    @Override
    // TODO why is this always null, is this method in use
    public <T> T getActual(final T expected) {
        return null;
    }

    @Override
    public <T> boolean containsActual(final T actual) {
        for (final IsomorphicNodePair<?> isomorphicNode : isomorphicNodes) {
            if (isomorphicNode.getActual() == actual) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> boolean containsExpected(final T expected) {
        for (final IsomorphicNodePair<?> isomorphicNode : isomorphicNodes) {
            if (isomorphicNode.getExpected() == expected) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> NodeCheckType nodeCheck(final T expected, final T actual) {
        if (containsPair(expected, actual)) {
            return NodeCheckType.CONTAINS_PAIR;
        } else if (containsExpected(actual) || containsActual(expected)) {
            return NodeCheckType.SINGLE_NODE;
        }
        return NodeCheckType.NEW_PAIR;
    }

}
