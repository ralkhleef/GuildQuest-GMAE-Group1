package gmae.core;

import gmae.adventures.timedraid.TimedRaidAdventure;
import gmae.adventures.timedraid.TimedRaidMode;
import gmae.adventures.relichunt.RelicHuntAdventure;
import gmae.adventures.relichunt.RelicHuntState;
import guildquest.model.*;
import java.util.ArrayList;

import java.util.Scanner;

public class GmaeApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        AdventureRegistry registry = new AdventureRegistry();
        registry.register(new TimedRaidAdventure(5, 10, new int[]{2, 2, 2}, TimedRaidMode.TURN_BASED));
        registry.register(new RelicHuntAdventure(4, 20, 3, RelicHuntState.Mode.COMPETITIVE));
        // registry.register(new RelicHuntAdventure(...));  // TODO: add when ready

        RealmManager realmManager = new RealmManager();
        Realm r1 = realmManager.createRealm("R1", "Science Building");
        Realm r2 = realmManager.createRealm("R2", "ICS Building");

        ProfileManager profileManager = new ProfileManager();

        PlayerProfile john = profileManager.createProfile("John Doe", r1);
        PlayerProfile ghadeer = profileManager.createProfile("Ghadeer", r2);
        PlayerProfile kun = profileManager.createProfile("Kun", null);

        john.addItem(new Item("Sword", "Weapon", "rare", "Sturdy blade"));
        john.addItem(new Item("Meat", "Consumable", "common", "Food"));
        ghadeer.addItem(new Item("Compass", "Map", "common", "Navigation"));

        GameEngine engine = new GameEngine(scanner);
        MenuSystem menu = new MenuSystem(scanner, registry, engine, profileManager, realmManager);
        menu.show();
    }
}
