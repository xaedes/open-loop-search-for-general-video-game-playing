package TeamTopbug_HR;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;

import java.util.ArrayList;

public class WalkableMap {
    static boolean[][] staticMap = null;
    static public boolean[][] statics(StateObservation stateObs) {
        ArrayList<Observation>[][] observationGrid = stateObs.getObservationGrid();
        assert observationGrid.length == GameInfo.width;
        assert observationGrid[0].length == GameInfo.height;

        boolean[][] m = new boolean[GameInfo.width][GameInfo.height];

        for (int i = 0; i < GameInfo.width; i++) {
            for (int j = 0; j < GameInfo.height; j++) {
                ArrayList<Observation> observations = observationGrid[i][j];

                // no observations -> empty
                if (observations.size() == 0) {
                    m[i][j] = true;  // walkable
                } else {
                    // find STATIC observations (=wall)
                    boolean foundWall = false;
                    for (int k = 0; k < observations.size(); k++) {
                        if (observations.get(k).category == Types.TYPE_STATIC) {
                            m[i][j] = false; // wall
                            foundWall = true;
                            break;
                        }
                    }

                    if (!foundWall) {
                        // lets assume its walkable, for now
                        m[i][j] = true;
                    }

                }
            }
        }
        return m;
    }
}
