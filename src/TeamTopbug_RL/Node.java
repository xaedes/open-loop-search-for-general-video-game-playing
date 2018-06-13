package TeamTopbug_RL;

import core.game.StateObservation;
import ontology.Types;

import java.util.LinkedList;
import java.util.List;

public class Node {
    public static double maxAverageReward = Double.NEGATIVE_INFINITY;
    public static double minAverageReward = 0;
    public static double maxBestReward = Double.NEGATIVE_INFINITY;
    public static double minBestReward = 0;

    // open loop
    public Types.ACTIONS action        = null;
    public double        averageReward = 0;
    public double        bestRewardWeight    = 0.5;
    public double        bestChildReward    = Double.NEGATIVE_INFINITY;
    public double        bestReward    = Double.NEGATIVE_INFINITY;
    public int           nVisits       = 0;
    public int           depth         = 0;
    public Node          prev          = null;
    public List<Node>    children      = null;
    public List<Node>    unborn        = null;

    // temporary
    public StateObservation state      = null;
    public Pheromones       pheromones = null;

    // node at gamestart
    public Node() {
        pheromones = new Pheromones();
    }

    // node expand
    public Node(Types.ACTIONS action, Node prev) {
        this.action = action;
        this.prev = prev;

        update();
        updateAverageReward();
    }

    public void expand() {
        unborn = new LinkedList<Node>();
        children = new LinkedList<Node>();
        for (Types.ACTIONS action : GameInfo.actions) {
            Node child = new Node(action, this);
            unborn.add(child);
        }
    }

    public void update() {
        depth = prev.depth + 1;
        state = prev.state.copy();
        state.advance(action);
        pheromones = new Pheromones(state, prev.pheromones);
    }


    public void updateAverageReward() {
        double reward = Heuristic.evaluate(this);
//        double discount = Math.pow(DISCOUNT, current.depth);
        if (this.nVisits == 0) { // this happens for recently expanded childs
            this.averageReward = reward;
        } else {

            this.averageReward = (this.averageReward * (this.nVisits - 1) + reward) / this.nVisits;
        }
        // update bounds
        if (this.averageReward>Node.maxAverageReward){
            Node.maxAverageReward = this.averageReward;
        }
        if (this.averageReward<Node.minAverageReward){
            Node.minAverageReward = this.averageReward;
        }
    }

    public Node select() {
        Double bestValue = null;
        Node best = null;
        for (Node child : children) {
            double normalized = (Node.maxBestReward - Node.minBestReward== 0)
                    ? 0
                    : (child.bestReward - Node.minBestReward) / (Node.maxBestReward- Node.minBestReward);
//            double normalized = (Node.maxAverageReward - Node.minAverageReward == 0)
//                    ? 0
//                    : (child.averageReward - Node.minAverageReward) / (Node.maxAverageReward - Node.minAverageReward);
            double value = normalized + Agent.UCB_EXPLORATION * Math.sqrt(Math.log(this.nVisits) / child.nVisits);
            if (bestValue == null || value > bestValue) {
                bestValue = value;
                best = child;
            }
        }
        return best;
    }
}
