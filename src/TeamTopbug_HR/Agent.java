package TeamTopbug_HR;


import TeamTopbug_HR.TraverseNodes.GreedyBFS;
import TeamTopbug_HR.TraverseNodes.TraverseCallback;
import core.competition.CompetitionParameters;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Agent extends AbstractPlayer implements TraverseCallback {

    private static long maxTime         = CompetitionParameters.ACTION_TIME * 1000 * 1000;
    private static long actShutdownTime = 10 * 1000 * 1000;
    private static int  nAdvances       = 3;

    protected ElapsedCpuTimer elapsedTimer      = null;
    protected long            maxTimePerAdvance = 0;
    protected long            lastTime          = 0;
    protected int             nTraversed        = 0;
    protected int             maxDepth          = 0;
    protected Heuristic       heuristic         = null;
    protected NodeAdvance     nodeAdvance       = null;
    protected StateObservation current=null;

    protected float nsPerBFSDistances = 0;


    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.current = stateObs;
        ArrayList<Observation>[][] observationGrid = stateObs.getObservationGrid();
        GameInfo.width = observationGrid.length;
        GameInfo.height = observationGrid[0].length;
        GameInfo.blocksize = stateObs.getBlockSize();
        heuristic = new Heuristic(stateObs);
        nodeAdvance = new PessimisticNodeAdvance(heuristic, nAdvances);
        Node.heuristic = heuristic;
    }

    /**
     * Custom one step lookahead agent.
     *
     * @param stateObs     Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.current = stateObs;
        this.elapsedTimer = elapsedTimer;

        Node origin = NodePool.get().set(null, stateObs, Types.ACTIONS.ACTION_NIL);
        heuristic.visitedWeights = origin.visitedWeights;
        GreedyBFS dfs = new GreedyBFS(origin, nodeAdvance, this, heuristic);

        nTraversed = 0;
        maxDepth = 0;
        lastTime = elapsedTimer.elapsed();
        dfs.startTraverse();

//        System.out.println(nTraversed + " " + (maxTimePerAdvance / 1e6));
        double best = Double.NEGATIVE_INFINITY;
        List<Types.ACTIONS> bestActions = new ArrayList<Types.ACTIONS>();
        for (int i = 0; i < origin.children.size(); i++) {
            Node child = origin.children.get(i);
            if (child.bestChildHeuristicValue > best) {
                best = child.bestChildHeuristicValue;
                bestActions.clear();
                bestActions.add(child.action);
            } else if (child.bestChildHeuristicValue == best) {
                bestActions.add(child.action);
            }

//            System.out.println(child.action + " " + child.bestChildHeuristicValue);
        }
        if (bestActions.size() == 0)
            return Types.ACTIONS.ACTION_NIL;


        NodePool.releaseNode(origin);

        Types.ACTIONS bestAction = bestActions.get(new Random().nextInt(bestActions.size()));
//        System.out.println("AgentX " + stateObs.getAvatarPosition().x);
        System.out.println("maxDepth " + maxDepth);
//        System.out.println("best:");
//        System.out.println(bestAction + " " + best);
        return bestAction;
    }

    @Override
    public boolean traverse(Node current) {
        nTraversed++;
        maxDepth = Math.max(maxDepth, current.depth);

        // update maxTimePerAdvance
        maxTimePerAdvance = Math.max(maxTimePerAdvance, elapsedTimer.elapsed() - lastTime);
        maxTimePerAdvance = Math.min(maxTimePerAdvance, 20);
        lastTime = elapsedTimer.elapsed();

        // cancel traversing if time is over
        if (maxTime - elapsedTimer.elapsed() < maxTimePerAdvance + actShutdownTime) return false;

//        if(maxDepth > 2) return false;
        return true;
    }

    @Override
    public void draw(Graphics2D g) {
        int s=(int)(GameInfo.blocksize*0.5);
        double[][] arr = heuristic.visitedWeights.visitedWeights;
        double min = Utils.min(arr);
        double max = Utils.max(arr);

        int[][] d = null;
        if (current!=null) {
            if(WalkableMap.staticMap==null){
                WalkableMap.staticMap=WalkableMap.statics(current);
            }
            d=BFSDistances.distances(
                    WalkableMap.staticMap,
                    Utils.toTileCoord(current.getAvatarPosition()));
        }
        for (int i = 0; i < GameInfo.width; i++) {
            for (int j = 0; j < GameInfo.height; j++) {
                int x = i * GameInfo.blocksize;
                int y = j * GameInfo.blocksize;
                float here = 0;
                if((max - min)!=0)
                    here = (float) ((arr[i][j] - min) / (max - min));
                g.setColor(new Color(here,here,here));

                g.fillOval(x+GameInfo.blocksize-s,y+GameInfo.blocksize-s, s, s);

                if (d!=null) {
                    g.drawString(String.valueOf(d[i][j]),x+GameInfo.blocksize-s,y+GameInfo.blocksize-s);
                }
            }
        }

    }
}
