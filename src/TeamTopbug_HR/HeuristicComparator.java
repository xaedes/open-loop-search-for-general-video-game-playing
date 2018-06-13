package TeamTopbug_HR;

import java.util.Comparator;
import controllers.Heuristics.StateHeuristic;


public class HeuristicComparator implements Comparator<Node> {
    protected NodeHeuristic heuristic;
    public HeuristicComparator(NodeHeuristic heuristic){
        this.heuristic = heuristic;
    }
    @Override
    public int compare(Node o1, Node o2) {
        Double o1h = this.heuristic.evaluateState(o1);
        Double o2h = this.heuristic.evaluateState(o2);
        return o1h.compareTo(o2h);
    }
}
