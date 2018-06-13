package TeamTopbug_HR.TraverseNodes;

import TeamTopbug_HR.HeuristicComparator;
import TeamTopbug_HR.Node;
import TeamTopbug_HR.NodeAdvance;
import TeamTopbug_HR.NodeHeuristic;
import TeamTopbug_HR.TraverseNodes.Base;
import TeamTopbug_HR.TraverseNodes.TraverseCallback;
import controllers.Heuristics.StateHeuristic;

import java.util.*;


public class BestFirstSearch extends Base {
    private NodeHeuristic heuristic;

    public BestFirstSearch(Node origin, NodeAdvance advance, TraverseCallback callback, NodeHeuristic heuristic) {
        super(origin, advance, callback);
        this.heuristic = heuristic;
    }

    @Override
    public void startTraverse() {
        HeuristicComparator comparator = new HeuristicComparator(this.heuristic);
        PriorityQueue<Node> pqueue= new PriorityQueue<Node>(10,Collections.reverseOrder(comparator));
        pqueue.add(this.origin);
        while (!pqueue.isEmpty()) {
            Node current = pqueue.remove();
            if(callback.traverse(current)) {
                List<Node> advanced = this.advance.advance(current);
                Collections.sort(advanced, comparator);
                Collections.reverse(advanced);
                pqueue.addAll(advanced);
            }
        }
    }
}
