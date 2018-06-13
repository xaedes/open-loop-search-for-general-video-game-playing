package TeamTopbug_RL;

import tools.StatSummary;

import java.util.Random;

public class Evaluate {
    public static void main(String[] args) {
        String gamesPath = "examples/gridphysics/";
        String gameName = args[0];
        int level = Integer.valueOf(args[1]);
        int M = Integer.valueOf(args[2]);

        // Controller to evaluate
        String controller = "TeamTopbug_RL.Agent";

        // Random Seed for repeatability
        int seed = new Random().nextInt();
        System.out.println("seed: " + seed);

        // This plays all games, in the first level-levels, M times each.
        String[] levels = new String[1];
        levels[0] = gamesPath + gameName + "_lvl" + level + ".txt";
        String game = gamesPath + gameName + ".txt";
        StatSummary statSummary = null;

        statSummary = ArcadeMachine.runGames(game, levels, M, controller, null, seed, false);
        System.out.println("Game " + gameName + " | Won: " + ((int) statSummary.sum()) + " of " + M);

    }
}