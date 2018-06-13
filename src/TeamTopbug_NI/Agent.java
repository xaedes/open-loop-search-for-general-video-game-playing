package TeamTopbug_NI;

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
    private static long   MAX_TIME       = CompetitionParameters.ACTION_TIME * 1000 * 1000;
    private static long   MAX_INIT_TIME  = CompetitionParameters.INITIALIZATION_TIME * 1000 * 1000;
    private static long   SHUTDOWN_TIME  = 3 * 1000 * 1000;
    private static double TIME_PESSIMISM = 1.5;
    public static  Random random         = new Random();

    protected double max_time_per_loop = 0;
    public static GA   ga;
    public        Node gameStart;
    public        Node origin;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        GameInfo.init(stateObs);
        gameStart = NodePool.get();
        gameStart.state = stateObs;
        ga = new GA(gameStart);
        gaUpdates(elapsedTimer, MAX_INIT_TIME);
        max_time_per_loop = 0;

        origin = gameStart;
//        ga.debugOutput();

//        elapsedTimer.reset();
    }

    public void gaUpdates(ElapsedCpuTimer elapsedTimer, long maxTime) {
        long last_time = elapsedTimer.elapsed();
        int n = 0;
        // repeat until no time is left
        double gain = 0.1;
        while (maxTime - elapsedTimer.elapsed() > TIME_PESSIMISM * (max_time_per_loop + SHUTDOWN_TIME)) {
            max_time_per_loop = (1 - gain) * max_time_per_loop + (gain) * (elapsedTimer.elapsed() - last_time);
            last_time = elapsedTimer.elapsed();
//            System.out.println(last_time);
//            System.out.println(max_time_per_loop);

//            ga.nextGeneration();
            ga.anyTimeGeneration();
            n++;
        }
//        System.out.println(n);
//        System.out.println(n);
//        System.out.println(max_time_per_loop / (1000.*1000.));
//        System.out.println(SHUTDOWN_TIME / (1000.*1000.));

    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        origin.state = stateObs;
        gaUpdates(elapsedTimer, MAX_TIME);



        //// roll tree
        Node selected = origin.select();


//        System.out.print(selected.action);
//        System.out.print(":");
//        for(Node child: origin.children.values()) {
//            if(child != selected) {
//                System.out.print(child.action);
//                System.out.print(", ");
//            }
//        }
//        System.out.println();

        // release siblings of selected
        selected.isDestroyable = false;
        origin.release();

        Types.ACTIONS action = selected.action;
        origin = selected;

        ga.roll(origin);

        // increase SHUTDOWN_TIME if time exceeded to avoid this the next time
//        if (elapsedTimer.elapsed() > MAX_TIME) {
//            SHUTDOWN_TIME += 1 * 1000 * 1000; // += 1ms
//        } else  if (MAX_TIME - elapsedTimer.elapsed() > 1 * 1000 * 1000 ) {
//            SHUTDOWN_TIME -= 1 * 1000 * 1000; // -= 1ms
//        }

//        elapsedTimer.reset();
        return action;
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
            if (current.prev != null) {
                Vector2d prevPoint = current.prev.avatarPos;
                Vector2d point = current.avatarPos;
                g.drawLine((int) (prevPoint.x + s + 0.5), (int) (prevPoint.y + s + 0.5), (int) (point.x + s + 0.5), (int) (point.y + s + 0.5));
//                g.drawString(String.valueOf(current.averageReward), (int) (point.x + s + 0.5), (int) (point.y + s + 0.5));
            }

            // traverse children
            if (current.children != null) {
                for (Node child : current.children.values()) {
                    stack.push(child);
                }
            }
        }

    }
}
