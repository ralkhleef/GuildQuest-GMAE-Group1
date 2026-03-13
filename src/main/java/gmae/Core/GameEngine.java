package gmae.core;

import java.util.List;
import java.util.Scanner;

public class GameEngine {

    private final Scanner scanner;

    public GameEngine(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run(MiniAdventure adventure, PlayerProfile p1, PlayerProfile p2) {
        adventure.setPlayers(p1, p2);
        adventure.start();

        while (!adventure.isOver()) {
            System.out.println("\n--- STATUS ---");
            String status = adventure.getStatus();
            System.out.println(status);

            int playerIndex = 1;
            if (status.contains("Turn: Player 2") || status.contains("Current Turn: P2")) {
                playerIndex = 2;
            }

            System.out.println("\nPlayer " + playerIndex + " input:");
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("quit")) {
                System.out.println("Adventure abandoned.");
                break;
            }

            adventure.handleInput(playerIndex, input);
        }

        String result = adventure.getResult();
        System.out.println("\n" + result);

        String historyEntry = adventure.name() + " — " + result.split("\n")[0];
        p1.addQuestHistory(historyEntry);
        p2.addQuestHistory(historyEntry);

        // Track stats
        p1.addGamePlayed();
        p2.addGamePlayed();

        String resultLine = result.split("\n")[0].toUpperCase();

        if (adventure.id().equals("relic_hunt")) {
            if (resultLine.contains("PLAYER 1 WINS")) {
                p1.addWin();
            } else if (resultLine.contains("PLAYER 2 WINS")) {
                p2.addWin();
            } else if (resultLine.contains("CO-OP WIN")) {
                p1.addWin();
                p2.addWin();
            } else if (resultLine.contains("TIE")) {
                p1.addWin();
                p2.addWin();
            }
            // Count relics from inventory size reported in result
            p1.addRelicsCollected(p1.getInventorySize());
            p2.addRelicsCollected(p2.getInventorySize());
        } else if (adventure.id().equals("timed_raid")) {
            if (resultLine.contains("WIN")) {
                p1.addWin();
                p2.addWin();
            }
            p1.addRaidCompleted();
            p2.addRaidCompleted();
        }

        // Check and announce achievements
        announceAchievements(p1);
        announceAchievements(p2);
    }

    private void announceAchievements(PlayerProfile player) {
        List<String> newAchievements = player.checkAndAwardAchievements();
        if (!newAchievements.isEmpty()) {
            System.out.println("\n*** " + player.getCharName() + " earned new achievements! ***");
            for (String a : newAchievements) {
                System.out.println("  >> " + a);
            }
        }
    }
}
