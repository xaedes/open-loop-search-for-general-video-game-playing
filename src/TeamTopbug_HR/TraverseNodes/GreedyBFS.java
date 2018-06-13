package TeamTopbug_HR.TraverseNodes;

import TeamTopbug_HR.HeuristicComparator;
import TeamTopbug_HR.Node;
import TeamTopbug_HR.NodeAdvance;
import TeamTopbug_HR.NodeHeuristic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class GreedyBFS extends Base {
    private NodeHeuristic heuristic;

    public GreedyBFS(Node origin, NodeAdvance advance, TraverseCallback callback, NodeHeuristic heuristic) {
        super(origin, advance, callback);
        this.heuristic = heuristic;
    }

    @Override
    public void startTraverse() {
        Queue<Node> queue = new LinkedList<Node>();
        HeuristicComparator comparator = new HeuristicComparator(this.heuristic);
        queue.add(this.origin);
        while (!queue.isEmpty()) {
            Node current = queue.remove();
            if (callback.traverse(current)) {
                List<Node> advanced = this.advance.advance(current);
                Collections.sort(advanced, comparator);
                Collections.reverse(advanced);
                queue.addAll(advanced);
            }
        }
    }
}
