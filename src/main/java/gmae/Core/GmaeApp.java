package gmae.core;

import gmae.adventures.timedraid.TimedRaidAdventure;
import gmae.adventures.relichunt.RelicHuntAdventure;
import gmae.adventures.relichunt.RelicHuntState;
import guildquest.model.*;
import java.util.ArrayList;

import java.util.Scanner;

public class GmaeApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        AdventureRegistry registry = new AdventureRegistry();
        registry.register(new TimedRaidAdventure(5, 10, new int[]{2, 2, 2}));
        registry.register(new RelicHuntAdventure(4, 20, 3, RelicHuntState.Mode.COMPETITIVE));
        // registry.register(new RelicHuntAdventure(...));  // TODO: add when ready

        RealmManager realmManager = new RealmManager();
        Realm r1 = realmManager.createRealm("R1", "Science Building");
        Realm r2 = realmManager.createRealm("R2", "ICS Building");

        ProfileManager profileManager = new ProfileManager();

        profileManager.createProfile("John Doe", r1);
        profileManager.createProfile("Ghadeer", r2);
        profileManager.createProfile("Kun", null);
        GameEngine engine = new GameEngine(scanner);
        MenuSystem menu = new MenuSystem(scanner, registry, engine, profileManager, realmManager);
        menu.show();
    }
}
