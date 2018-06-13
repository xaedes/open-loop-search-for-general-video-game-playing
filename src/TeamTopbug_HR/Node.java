package TeamTopbug_HR;

import core.game.StateObservation;
import ontology.Types;

import java.util.LinkedList;
import java.util.List;


public class Node {
    public Node             prevNode;
    public StateObservation stateObs;
    public Types.ACTIONS    action;
    public List<Node>       children;
    public double           heuristicValue;
    public double           bestChildHeuristicValue;
    public int              depth;
    public Object           traversalMeta;
    public double[][]       shortestDistances;
    public VisitedWeights   visitedWeights;

    public static Heuristic heuristic = null;

    private boolean isReleased = false;

    public Node()
    {
    }

    public Node set(Node prevNode, StateObservation stateObs, Types.ACTIONS action) {
        this.prevNode = prevNode;
        this.stateObs = stateObs;
        this.action = action;
        this.traversalMeta = null;

        // update depth
        if (this.prevNode == null)
            this.depth = 0;
        else
            this.depth = this.prevNode.depth + 1;


        // update parent's children
        this.children = new LinkedList<Node>();
        if (this.prevNode != null) {
            this.prevNode.children.add(this);
        }

        // update visitedWeights
        this.visitedWeights =
                new VisitedWeights(stateObs, (prevNode != null ? prevNode.visitedWeights : heuristic.visitedWeights));


        // update this' and parent's heuristics
        if (heuristic != null) {
            this.heuristicValue = heuristic.evaluateState(this);
            this.bestChildHeuristicValue = heuristicValue;
            // update parent's heuristics
            Node current = this;
            while (current.prevNode != null) {
                current.prevNode.bestChildHeuristicValue =
                        Math.max(current.prevNode.bestChildHeuristicValue, current.bestChildHeuristicValue);
                current = current.prevNode;
            }
        }

        return this;
    }

    public void use()
    {
        isReleased = false;
    }

    public void release()
    {
        prevNode = null;
        stateObs = null;
        children = null;
        traversalMeta = null;
        shortestDistances = null;
        visitedWeights = null;

        isReleased = true;
    }
}
