package com.graphhopper.routing;

import com.graphhopper.routing.util.WeightCalculation;
import com.graphhopper.storage.EdgeEntry;
import com.graphhopper.storage.Graph;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.GraphUtility;

/**
 * This class creates a DijkstraPath from two Edge's resulting from a
 * BidirectionalDijkstra
 *
 * @author Peter Karich,
 */
public class PathBidirRef extends Path {

    protected EdgeEntry edgeTo;
    private boolean switchWrapper = false;

    public PathBidirRef(Graph g) {
        super(g);
        weight = INIT_VALUE;
    }

    public PathBidirRef switchToFrom(boolean b) {
        switchWrapper = b;
        return this;
    }

    /**
     * Extracts path from two shortest-path-tree
     */
    @Override
    public Path extract() {
        weight = 0;
        if (edgeEntry == null || edgeTo == null)
            return this;

        int from = GraphUtility.getToNode(graph, edgeEntry.edge, edgeEntry.endNode);
        int to = GraphUtility.getToNode(graph, edgeTo.edge, edgeTo.endNode);
        if (from != to)
            throw new IllegalStateException("Locations of the 'to'- and 'from'-Edge has to be the same." + toString());

        if (switchWrapper) {
            EdgeEntry ee = edgeEntry;
            edgeEntry = edgeTo;
            edgeTo = ee;
        }

        EdgeEntry currEdge = edgeEntry;
        while (EdgeIterator.Edge.isValid(currEdge.edge)) {
            processWeight(currEdge.edge, currEdge.endNode);
            currEdge = currEdge.parent;
        }
        fromNode(currEdge.endNode);
        reverseOrder();
        currEdge = edgeTo;
        int tmpEdge = currEdge.edge;
        while (EdgeIterator.Edge.isValid(tmpEdge)) {
            currEdge = currEdge.parent;
            processWeight(tmpEdge, currEdge.endNode);
            tmpEdge = currEdge.edge;
        }
        return found(true);
    }
}