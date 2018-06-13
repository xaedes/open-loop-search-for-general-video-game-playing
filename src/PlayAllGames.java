import core.ArcadeMachine;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by xaedes on 06.11.14.
 */
public class PlayAllGames {
    public static void main(String[] args) {
        int seed = new Random().nextInt();
        String gamesPath = "examples/gridphysics/";
        String games[] = new String[]{"aliens", "bait", "boloadventures", "bombuzal", "boulderdash", "brainman", "butterflies", "camelRace", "chase", "chipschallenge", "digdug", "eggomania", "firecaster", "firestorms", "frogs", "infection", "missilecommand", "modality", "overload", "pacman", "painter", "portals", "realsokoban", "seaquest", "sokoban", "solarfox", "survivezombies", "thecitadel", "whackamole", "zelda", "zenpuzzle"};
        int i = Arrays.asList(games).indexOf("frogs");
//        int i = 5;
        String game = gamesPath + games[i] + ".txt";
//        String game = gamesPath + games[i] + ".txt";
        String level = gamesPath + games[i] + "_lvl" + 0 + ".txt";
        ArcadeMachine.playOneGame(game, level, null, seed);
    }
}