package TeamTopbug_RL;

import core.competition.CompetitionParameters;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.awt.*;
import java.util.Random;
import java.util.Stack;


public class Agent extends AbstractPlayer {
    public Node gameStart;
    public Node origin;
    public static final int MAX_SELECTION_DEPTH = 20;
    public static final int MAX_PLAYOUT_DEPTH   = 0;

    public static final double UCB_EXPLORATION = 0.1;
    private static      long   MAX_TIME        = CompetitionParameters.ACTION_TIME * 1000 * 1000;
    private static      long   SHUTDOWN_TIME   = 10 * 1000 * 1000;
    private static      double TIME_PESSIMISM  = 1.5;
    private static      double DISCOUNT        = 1;
    public static       Random random          = new Random();

    protected long max_time_per_loop = 0;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        GameInfo.init(stateObs);
        gameStart = new Node();
        origin = gameStart;
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
    /*
        repeat until no time is left:
            explore tree starting from current
            use stateObs to generate the StateObservations for the nodes
        select best action
    */
        origin.state = stateObs;
        origin.depth = 0;
        if (origin.prev != null) {
            origin.prev.depth = -1;
        }
        origin.nVisits = 1;

        long last_time = elapsedTimer.elapsed();
        int n = 0;
        // repeat until no time is left
        while (MAX_TIME - elapsedTimer.elapsed() > TIME_PESSIMISM * (max_time_per_loop + SHUTDOWN_TIME)) {
            max_time_per_loop = Math.max(max_time_per_loop, elapsedTimer.elapsed() - last_time);
            max_time_per_loop = Math.min(max_time_per_loop, 20); // is this necessary?
            last_time = elapsedTimer.elapsed();

            // select from tree starting from origin until MAX_SELECTION_DEPTH is reached
            Node selected = origin;
            while (selected.depth < MAX_SELECTION_DEPTH) {
                if (selected.state.isGameOver()) {
//                    System.out.println("GAMEOVER BREAK");
                    break;
                }

                // if first time visit expand
                if (selected.unborn == null) {
                    selected.expand();
//                    System.out.println("EXPAND BREAK");
                    break;
                } else {
                    // select child
                    selected = selected.select();
                    selected.nVisits++;
                    // update current with actual stateObs
                    selected.update();
                    selected.updateAverageReward();
                }
            }

            if (selected.unborn != null && selected.unborn.size() > 0) {
                // fast incubation of all unborn
                for (Node baby : selected.unborn) {
                    selected.children.add(baby);
                    baby.nVisits++;
                }
                this.backpropagate(selected);
                selected.unborn.clear();
            } else if (selected.prev != null) {
                this.backpropagate(selected.prev);
            }

            n++;
        }
//        System.out.println(n);

        // select best node and release its siblings
        Node selected = origin.select();
        origin.children.clear();
        origin.children.add(selected);
        origin = selected;
//        System.out.println(origin.prev.action + " " + origin.action + " " + origin.bestReward);

        // increase SHUTDOWN_TIME if time exceeded to avoid this the next time
        if (elapsedTimer.elapsed() > MAX_TIME) {
            SHUTDOWN_TIME += 1 * 1000 * 1000; // += 1ms
        }

        return origin.action;
    }

    protected void backpropagate(Node fromParent) {
        // update bestRewards of selected and its ancestors
        Node parent = fromParent;
        do {
            double bestSibling = Double.NEGATIVE_INFINITY;
            for (Node sibling : parent.children) {
                if (sibling.children == null || sibling.children.size() == 0) {
                    sibling.bestReward = sibling.averageReward;
                    // update bounds
                    if (sibling.bestReward > Node.maxBestReward) {
                        Node.maxBestReward = sibling.bestReward;
                    }
                    if (sibling.bestReward < Node.minBestReward) {
                        Node.minBestReward = sibling.bestReward;
                    }
                }
                if (sibling.bestReward > bestSibling) {
                    bestSibling = sibling.bestReward;
                }
            }

            parent.bestReward =
                    bestSibling * parent.bestRewardWeight +
                            parent.averageReward * (1 - parent.bestRewardWeight);

            // update bounds
            if (parent.bestReward > Node.maxBestReward) {
                Node.maxBestReward = parent.bestReward;
            }
            if (parent.bestReward < Node.minBestReward) {
                Node.minBestReward = parent.bestReward;
            }

            parent = parent.prev;
        } while (parent != null && parent.depth >= 0);
    }

    @Override
    public void draw(Graphics2D g) {
        int s = (int) (GameInfo.blocksize * 0.5);
        double[][] arr = origin.pheromones.grid;
        double min = Utils.min(arr);
        double max = Utils.max(arr);

        for (int i = 0; i < GameInfo.width; i++) {
            for (int j = 0; j < GameInfo.height; j++) {
                int x = i * GameInfo.blocksize;
                int y = j * GameInfo.blocksize;
                float here = 0;
                if ((max - min) != 0)
                    here = (float) ((arr[i][j] - min) / (max - min));
                g.setColor(new Color(here, here, here));

                g.fillOval(x + GameInfo.blocksize - s, y + GameInfo.blocksize - s, s, s);

            }
        }

        // draw search space from origin
        Stack<Node> stack = new Stack<Node>();
        stack.push(origin);
        g.setColor(new Color(255, 0, 0));
        while (!stack.empty()) {
            Node current = stack.pop();

            // draw
            if (current.depth > 0) {
                Vector2d prevPoint = current.prev.state.getAvatarPosition();
                Vector2d point = current.state.getAvatarPosition();
                g.drawLine((int) (prevPoint.x + s + 0.5), (int) (prevPoint.y + s + 0.5), (int) (point.x + s + 0.5), (int) (point.y + s + 0.5));
//                g.drawString(String.valueOf(current.averageReward), (int) (point.x + s + 0.5), (int) (point.y + s + 0.5));
            }

            // traverse children
            if (current.children != null) {
                for (Node child : current.children) {
                    stack.push(child);
                }
            }
        }

    }
}
