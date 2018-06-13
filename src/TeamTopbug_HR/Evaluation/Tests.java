package TeamTopbug_HR.Evaluation;

import tools.StatSummary;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Random;

import static org.junit.Assert.*;

import org.junit.Test;


public class Tests {
    private static String      gamesPath   = "examples/gridphysics/";
    private static String      controller  = "TeamTopbug_HR.Agent";
    // prepare disabling of System.out
    private static PrintStream sysout      = System.out;
    private static PrintStream disabledOut = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
        }
    });
    //CIG 2014 Validation Set Games
    private static String games[] = new String[]{"camelRace", "digdug", "firestorms", "infection", "firecaster",
            "overload", "pacman", "seaquest", "whackamole", "eggomania"};


    @Test
    public void testCamelRace() {
        int seed = new Random().nextInt();
        assertEquals(1, runGamesLvl("camelRace", 0, 1, seed).sum(),1e-3);
        assertEquals(1, runGamesLvl("camelRace", 1, 1, seed).sum(),1e-3);
        assertEquals(1, runGamesLvl("camelRace", 2, 1, seed).sum(),1e-3);
        assertEquals(1, runGamesLvl("camelRace", 3, 1, seed).sum(),1e-3);
        assertEquals(1, runGamesLvl("camelRace", 4, 1, seed).sum(),1e-3);
    }

    protected static StatSummary runGames(String game, String[] levels, int M, int seed) {
        StatSummary statSummary = null;

        // disable System.out while calling ArcadeMachine.runGames
        System.setOut(disabledOut);
        try {
            statSummary = ArcadeMachine.runGames(gamesPath + game + ".txt", levels, M, controller, null, seed, false);
        } finally {
            System.setOut(sysout);
        }

        return statSummary;
    }

    protected static StatSummary runGamesUpToLvl(String game, int L, int M, int seed) {
        String[] levels = new String[L];
        for (int j = 0; j < L; ++j) {
            levels[j] = gamesPath + game + "_lvl" + j + ".txt";
        }
        return runGames(game, levels, M, seed);
    }
    protected static StatSummary runGamesLvl(String game, int lvl, int M, int seed) {
        String[] levels = new String[1];
        levels[0] = gamesPath + game + "_lvl" + lvl + ".txt";
        return runGames(game, levels, M, seed);
    }
}