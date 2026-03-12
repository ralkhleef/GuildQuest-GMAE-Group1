package gmae.core;

import gmae.adventures.timedraid.TimedRaidAdventure;
import gmae.adventures.timedraid.TimedRaidMode;
import gmae.adventures.relichunt.RelicHuntAdventure;
import gmae.adventures.relichunt.RelicHuntState;

import java.util.Scanner;

public class GmaeApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        AdventureRegistry registry = new AdventureRegistry();
        registry.register(new TimedRaidAdventure(5, 10, new int[]{2, 2, 2}, TimedRaidMode.TURN_BASED));
        registry.register(new RelicHuntAdventure(4, 20, 3, RelicHuntState.Mode.COMPETITIVE));
        // registry.register(new RelicHuntAdventure(...));  // TODO: add when ready

        ProfileManager profileManager = new ProfileManager();
        profileManager.createProfile("John Doe", "ELH 1200");
        profileManager.createProfile("Ghadeer", "None");
        profileManager.createProfile("Kun", "HIB 100");
        GameEngine engine = new GameEngine(scanner);
        MenuSystem menu = new MenuSystem(scanner, registry, engine, profileManager);
        menu.show();
    }
}
