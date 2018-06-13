package TeamTopbug_NI;

import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import java.awt.*;
import java.util.HashMap;

public class Node {
    public static double maxAverageReward = Double.NEGATIVE_INFINITY;
    public static double minAverageReward = 0;

    // open loop
    public Types.ACTIONS                action        = null;
    public double                       averageReward = 0;
    public int                          nVisits       = 0;
    public int                          depth         = 0;
    public Node                         prev          = null;
    public HashMap<Types.ACTIONS, Node> children      = new HashMap<Types.ACTIONS, Node>();

    public Vector2d avatarPos = null;

    public double bestRewardWeight = 0.5;
    public double bestReward       = Double.NEGATIVE_INFINITY;

    // temporary
    public StateObservation state      = null;
    public Pheromones       pheromones = null;

    public boolean isDestroyable = true;

    // node at gamestart
    public Node() {
        pheromones = new Pheromones();
    }

    // node expand
    public Node(Types.ACTIONS action, Node prev) {
        init(action, prev);
    }

    public Node init(Types.ACTIONS action, Node prev) {
        this.averageReward = 0;
        this.nVisits = 0;
        this.prev = null;
        this.children = new HashMap<Types.ACTIONS, Node>();

        this.bestRewardWeight = 0.5;
        this.bestReward = Double.NEGATIVE_INFINITY;


        this.state = null;
        this.pheromones = null;

        this.isDestroyable = true;

        this.action = action;
        this.prev = prev;

        update();
        updateAverageReward();
        updateBestReward();

        return this;
    }

    public void update() {
        depth = prev.depth + 1;
//        state = prev.state.copy();
        state = prev.state;
        state.advance(action);
        avatarPos = state.getAvatarPosition().copy();
        pheromones = new Pheromones(state, prev.pheromones);
    }

    /**
     * Marks this Node as visited
     */
    public void updateAverageReward() {

        double reward = Heuristic.evaluate(this);
        if (this.nVisits == 0) { // this happens for recently expanded childs
            this.averageReward = reward;
            this.nVisits = 1;
        } else {
            this.nVisits++;
            this.averageReward = (this.averageReward * (this.nVisits - 1) + reward) / this.nVisits;
        }

        // update bestReward of parent
        this.prev.updateBestReward();

        // update bounds
        if (this.averageReward > Node.maxAverageReward) {
            Node.maxAverageReward = this.averageReward;
        }
        if (this.averageReward < Node.minAverageReward) {
            Node.minAverageReward = this.averageReward;
        }

    }

    public void updateBestReward() {
        if (!children.values().isEmpty()) {
            double bestChild = Double.NEGATIVE_INFINITY;
            for (Node child : children.values()) {
                if (child.averageReward > bestChild) {
                    bestChild = child.averageReward;
                }
            }

            bestReward = bestRewardWeight * bestChild + (1 - bestRewardWeight) * averageReward;
        } else {
            bestReward = averageReward;
        }
//        System.out.println(bestReward);
    }

    public Node select() {
        Double bestValue = Double.NEGATIVE_INFINITY;
        Node best = null;
        for (Node child : children.values()) {

            if (child.bestReward > bestValue) {
                bestValue = child.bestReward;
                best = child;
            }
        }
        return best;
    }

    public void release() {
        prev = null;
        state = null;
        children = null;
        pheromones = null;
    }
}
