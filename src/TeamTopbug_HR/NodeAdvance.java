package TeamTopbug_HR;

import controllers.Heuristics.StateHeuristic;
import ontology.Types;

import java.util.List;

public abstract class NodeAdvance {
    protected NodeHeuristic heuristic;

    public NodeAdvance(NodeHeuristic heuristic) {
        this.heuristic = heuristic;
    }

    abstract public List<Node> advance(Node origin);
    abstract public Node advance(Node origin, Types.ACTIONS action);
}
