package gmae.core;

import gmae.adventures.timedraid.TimedRaidAdventure;
import gmae.adventures.relichunt.RelicHuntAdventure;
import gmae.adventures.relichunt.RelicHuntState;

import java.util.Scanner;

public class GmaeApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        AdventureRegistry registry = new AdventureRegistry();
        registry.register(new TimedRaidAdventure(5, 10, new int[]{2, 2, 2}));
        registry.register(new RelicHuntAdventure(4, 20, 3, RelicHuntState.Mode.COMPETITIVE));
        // registry.register(new RelicHuntAdventure(...));  // TODO: add when ready

        GameEngine engine = new GameEngine(scanner);
        MenuSystem menu = new MenuSystem(scanner, registry, engine);
        menu.show();
    }
}
