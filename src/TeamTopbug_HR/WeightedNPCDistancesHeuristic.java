package TeamTopbug_HR;

import controllers.Heuristics.StateHeuristic;
import core.game.Observation;
import core.game.StateObservation;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeightedNPCDistancesHeuristic extends StateHeuristic {

    Map<Integer,Double> npcTypeWeights;

    public WeightedNPCDistancesHeuristic(StateObservation stateObs) {
        // initialize and populate npc type weights
        this.npcTypeWeights = new HashMap<Integer, Double>();
        Vector2d avatarPosition = stateObs.getAvatarPosition();
        ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions(avatarPosition);
        for (ArrayList<Observation> npcPosition : npcPositions) {
            if (npcPosition.size() > 0) {
                this.npcTypeWeights.put(npcPosition.get(0).itype, (double) 0);
            }
        }
    }

    public double evaluateState(StateObservation stateObs) {
        // get npc observations relative to avatar
        Vector2d avatarPosition = stateObs.getAvatarPosition();
        ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions(avatarPosition);


        double score = 0;
        for (ArrayList<Observation> npcPosition : npcPositions) {
            int size = npcPosition.size();
            for (Observation npc : npcPosition) {
                double weight = this.npcTypeWeights.get(npc.itype);
                // subtract weighted distance from score
                // which means it is good to be near positive weighted npc types and vice versa
                // normalized (i don't know if it is really good to do this, but it kinda makes sense, not?)
                score = score - weight * Math.sqrt(npc.sqDist) / size;
            }
        }

        return score;
    }


}


