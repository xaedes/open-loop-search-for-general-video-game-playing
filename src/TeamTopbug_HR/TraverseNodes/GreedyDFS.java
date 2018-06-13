package TeamTopbug_HR.TraverseNodes;


import TeamTopbug_HR.HeuristicComparator;
import TeamTopbug_HR.Node;
import TeamTopbug_HR.NodeAdvance;
import TeamTopbug_HR.NodeHeuristic;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class GreedyDFS extends Base {

    private NodeHeuristic heuristic;

    public GreedyDFS(Node origin, NodeAdvance advance, TraverseCallback callback, NodeHeuristic heuristic) {
        super(origin, advance, callback);
        this.heuristic = heuristic;
    }

    @Override
    public void startTraverse() {
        Stack<Node> stack = new Stack<Node>();
        HeuristicComparator comparator = new HeuristicComparator(this.heuristic);
        stack.add(this.origin);
        while (!stack.isEmpty()) {
            Node current = stack.pop();
            if (callback.traverse(current)) {
                List<Node> advanced = this.advance.advance(current);
                Collections.sort(advanced, comparator);
                stack.addAll(advanced);
            }
        }
    }
}
