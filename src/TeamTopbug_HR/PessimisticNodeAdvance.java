package TeamTopbug_HR;
import controllers.Heuristics.StateHeuristic;
import core.game.StateObservation;
import ontology.Types;

import java.util.ArrayList;
import java.util.List;

public class PessimisticNodeAdvance extends NodeAdvance {
    protected int nAdvances;
    public PessimisticNodeAdvance(NodeHeuristic heuristic, int nAdvances) {
        super(heuristic);
        this.nAdvances = nAdvances;
    }

    @Override
    public List<Node> advance(Node origin) {
        List<Node> advanced = new ArrayList<Node>();

        if(origin.stateObs == null)
            return advanced;

        for (Types.ACTIONS action : origin.stateObs.getAvailableActions()) {
            advanced.add(this.advance(origin, action));
        }

        advanced.add(this.advance(origin, Types.ACTIONS.ACTION_NIL));

        return advanced;
    }

    @Override
    public Node advance(Node origin, Types.ACTIONS action) {
        // advance this.nAdvances times and choose the worst outcome

        double worstQ = Double.POSITIVE_INFINITY;
        StateObservation worst = null;

        for (int i = 0; i < this.nAdvances; i++) {
            StateObservation next = origin.stateObs.copy();
            next.advance(action);
            double Q = heuristic.evaluateState(NodePool.get().set(origin, next, action));
            if( Q < worstQ ){
                worstQ = Q;
                worst = next;
            }
        }

        return NodePool.get().set(origin, worst, action); // todo reuse
    }
}
