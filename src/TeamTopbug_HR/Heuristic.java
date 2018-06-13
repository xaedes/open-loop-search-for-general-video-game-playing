package TeamTopbug_HR;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Heuristic extends NodeHeuristic {
    public static final double HUGE_NEGATIVE                      = -1e7;
    public static final double HUGE_POSITIVE                      = 1e7;
    public static final double MIN_PORTAL_WALKING_DISTANCE_WEIGHT = 0;
    public static final double MIN_PORTAL_DISTANCE_WEIGHT         = 0;
    public static final double TIME_WEIGHT                        = 0.1;
    public static final double VISITED_WEIGHT                     = 1;
    public static final double SCORE                              = 1;

    public double[][] shortestDistances;

    public VisitedWeights visitedWeights;


    public Heuristic(StateObservation stateObs) {
        ArrayList<Observation>[][] observationGrid = stateObs.getObservationGrid();

        shortestDistances = FloydWarshall.distances(walkableWeights(stateObs));


        visitedWeights = new VisitedWeights();
    }

    public double evaluateState(Node node) {
        double score = 0;

        StateObservation stateObs = node.stateObs;
        if (stateObs == null)
            return score;

        // first check win or lose
        boolean gameOver = stateObs.isGameOver();
        Types.WINNER win = stateObs.getGameWinner();

        if (gameOver && win == Types.WINNER.PLAYER_LOSES) {
            score += HUGE_NEGATIVE;
        }

        if (gameOver && win == Types.WINNER.PLAYER_WINS) {
            score += HUGE_POSITIVE;
        }

        // now add time
        int time = stateObs.getGameTick();
        score += -TIME_WEIGHT * time;

        // try to get to a portal
        Vector2d avatarPosition = stateObs.getAvatarPosition();
        ArrayList<Observation>[] portalPositions = stateObs.getPortalsPositions(avatarPosition);


        if (portalPositions != null) {
            double minWalkingDistancePortal = Double.POSITIVE_INFINITY;
            double minDistancePortal = Double.POSITIVE_INFINITY;
            Vector2d minWalkingPosPortal = null;
            Vector2d minPosPortal = null;
            for (ArrayList<Observation> portals : portalPositions) {
                for (int i = 0; i < portals.size(); i++) {
                    Observation portal = portals.get(i);
                    double walkingDist = shortestWalkingDistance(avatarPosition, portal.position);
                    if (walkingDist < minWalkingDistancePortal) {
                        minWalkingDistancePortal = walkingDist;
                        minWalkingPosPortal = portal.position;
                    }
                    double dist = Math.sqrt(portal.sqDist);
                    if (dist < minDistancePortal) {
                        minDistancePortal = dist;
                        minPosPortal = portal.position;
                    }
                }
            }
            if (minWalkingPosPortal != null) {
                // portal found
                score += -MIN_PORTAL_WALKING_DISTANCE_WEIGHT * minWalkingDistancePortal;
            }
            if (minPosPortal != null) {
                // portal found
                score += -MIN_PORTAL_DISTANCE_WEIGHT * minDistancePortal;
            }
        }

        // game score
        double gameScore = stateObs.getGameScore();
        score += SCORE * gameScore;

        // visited weights
        Point tilePos = Utils.toTileCoord(avatarPosition);
        score += -VISITED_WEIGHT * node.visitedWeights.visitedWeights[tilePos.x][tilePos.y];


        return score;
    }

    public double manhattanDistance(Vector2d a, Vector2d b) {
        Point d = Utils.toTileCoord(b.x - a.x,b.y - a.y);
        return d.x+d.y;
    }

    public double shortestWalkingDistance(Vector2d a, Vector2d b) {
        return shortestWalkingDistance(
                Utils.toTileCoord(a),
                Utils.toTileCoord(b));
    }

    public double shortestWalkingDistance(Observation a, Observation b) {
        return shortestWalkingDistance(
                Utils.toTileCoord(a.position),
                Utils.toTileCoord(b.position));
    }

    public double shortestWalkingDistance(Point a, Point b) {
        int k1 = a.x + a.y * GameInfo.width;
        int k2 = b.x + b.y * GameInfo.width;
        return shortestDistances[k1][k2];
    }

    // get walkable weights for use in floydwarshall, one for each tile to each tile
    public double[][] walkableWeights(StateObservation stateObs) {
        ArrayList<Observation>[][] observationGrid = stateObs.getObservationGrid();
        if (observationGrid.length == 0) {
            return new double[0][0];
        }

        int n = GameInfo.width * GameInfo.height;

        // build map
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

        // weights(=costs) from tile to tile
        double[][] w = new double[n][n];

        // nothing is walkable at first
        for (int k = 0; k < n; k++) {
            Arrays.fill(w[k], Double.POSITIVE_INFINITY);
        }
        for (int i = 0; i < GameInfo.width; i++) {
            for (int j = 0; j < GameInfo.height; j++) {
                int k = i + j * GameInfo.width; //row-major
                // left neighbor of this and vice versa
                if (i > 0 && m[i][j] && m[i - 1][j]) { // both tiles walkable
                    int k_left = k - 1;
                    w[k][k_left] = 1;
                    w[k_left][k] = 1;
                }

                // upper neighbor of this and vice versa
                if (j > 0 && m[i][j] && m[i][j - 1]) { // both tiles walkable
                    int k_up = k - GameInfo.width;
                    w[k][k_up] = 1;
                    w[k_up][k] = 1;
                }

            }
        }

        return w;
    }

}
