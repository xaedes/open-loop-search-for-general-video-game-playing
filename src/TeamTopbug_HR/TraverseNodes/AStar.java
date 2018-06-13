package TeamTopbug_HR.TraverseNodes;

import TeamTopbug_HR.HeuristicComparator;
import TeamTopbug_HR.Node;
import TeamTopbug_HR.NodeAdvance;
import controllers.Heuristics.StateHeuristic;
import core.game.StateObservation;

import java.util.*;

public abstract class AStar extends Base {
    protected StateHeuristic heuristic;

    public AStar(Node origin, NodeAdvance advance, TraverseCallback callback, StateHeuristic heuristic) {
        super(origin, advance, callback);
        this.heuristic = heuristic;
    }

    class OpenListComparator implements Comparator<Node> {
        private StateHeuristic h;

        public OpenListComparator(StateHeuristic h) {
            this.h = h;
        }
        @Override
        public int compare(Node o1, Node o2) {
            Double g1 = ((Double)o1.traversalMeta);
            Double g2 = ((Double)o2.traversalMeta);
            Double f1 = g1 + h.evaluateState(o1.stateObs);
            Double f2 = g2 + h.evaluateState(o2.stateObs);
            return f1.compareTo(f2);
        }

    }

    @Override
    public void startTraverse() {
        PriorityQueue<Node> open = new PriorityQueue<Node>(10,new OpenListComparator(heuristic));
        Set<Node> closed = new HashSet<Node>();
        open.add(origin);
        origin.traversalMeta = new Double(0);
        do {
            Node current = open.remove();
            if (callback.traverse(current)) {
                closed.add(current);
                List<Node> advanced = advance.advance(current);
                for (Node node : advanced) {
                    if(closed.contains(node))
                        continue;
                    Double g = ((Double)current.traversalMeta) + cost(node);
                    if(open.contains(node) && g > ((Double)node.traversalMeta))
                        continue;
                    current.traversalMeta = g;
                    node.traversalMeta = (Double)(g + heuristic.evaluateState(node.stateObs));
                    if(open.contains(node)){
                        open.remove(node);
                    }
                    open.add(node);
                }
            }
        } while (!open.isEmpty());
    }

    abstract protected double cost(Node node);
}
