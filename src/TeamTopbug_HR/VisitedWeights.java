package TeamTopbug_HR;

import core.game.Game;
import core.game.Observation;
import core.game.StateObservation;
import tools.Vector2d;

import java.awt.*;
import java.util.ArrayList;

public class VisitedWeights {
    public  double[][] visitedWeights;
    public static final double VISITED_INCREASE = 1;
    public static final double VISITED_DECAY    = 0.99;
    public static final double VISITED_DIFFUSE  = 0.4;


    public VisitedWeights() {
        visitedWeights = new double[GameInfo.width][GameInfo.height];
    }

    public VisitedWeights(StateObservation stateObs, VisitedWeights old) {
        visitedWeights = new double[GameInfo.width][GameInfo.height];
        Vector2d avatarPosition = stateObs.getAvatarPosition();
        Point tilePos = Utils.toTileCoord(avatarPosition);
        double[][] buffered = Utils.copy2DArray(old.visitedWeights);
        buffered[tilePos.x][tilePos.y] = VISITED_INCREASE;
        for (int i = 0; i < GameInfo.width; i++) {
            for (int j = 0; j < GameInfo.height; j++) {
                int n = 0;
                double d = 0;
                if (i > 0) {
                    n++;
                    d += buffered[i - 1][j];
                }
                if (i < GameInfo.width - 1) {
                    n++;
                    d += buffered[i + 1][j];
                }
                if (j > 0) {
                    n++;
                    d += buffered[i][j - 1];
                }
                if (j < GameInfo.height - 1) {
                    n++;
                    d += buffered[i][j + 1];
                }
                if(n==0){
                    n=1;
                    d=0;
                }
                this.visitedWeights[i][j] = VISITED_DIFFUSE * (d / n)
                        + (1-VISITED_DIFFUSE)*VISITED_DECAY*buffered[i][j];

            }
        }
    }


}
