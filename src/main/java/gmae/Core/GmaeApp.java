package gmae.core;

import gmae.adventures.timedraid.TimedRaidAdventure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class GmaeApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Map<String, MiniAdventure> adventures = new LinkedHashMap<>();
        adventures.put("1", new TimedRaidAdventure(5, 10, new int[]{2, 2, 2}));

        System.out.println("=== GuildQuest Mini-Adventure Environment (GMAE) ===");
        System.out.println("Choose an adventure:");
        for (Map.Entry<String, MiniAdventure> e : adventures.entrySet()) {
            System.out.println(e.getKey() + ") " + e.getValue().name());
        }

        System.out.print("> ");
        String choice = sc.nextLine().trim();
        MiniAdventure adv = adventures.get(choice);

        if (adv == null) {
            System.out.println("Invalid choice.");
            return;
        }

        adv.start();

        while (!adv.isOver()) {
            System.out.println("\n--- STATUS ---");
            System.out.println(adv.getStatus());

            int playerIndex = adv.getStatus().contains("P2") ? 2 : 1;

            System.out.println("\nPlayer " + playerIndex + " input:");
            System.out.print("> ");
            String input = sc.nextLine();

            adv.handleInput(playerIndex, input);
        }

        System.out.println("\n" + adv.getResult());
    }
}