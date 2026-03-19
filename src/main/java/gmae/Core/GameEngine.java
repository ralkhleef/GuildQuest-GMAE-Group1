package gmae.core;

import java.util.List;
import java.util.Scanner;

import gmae.adventures.timedraid.TimedRaidAdventure;

public class GameEngine {

    private final Scanner scanner;

    public GameEngine(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run(MiniAdventure adventure, PlayerProfile p1, PlayerProfile p2) {
        adventure.setPlayers(p1, p2);
        adventure.start();

        boolean abandoned = false;
        int realTimePlayer = 1;

        TimedRaidAdventure timedRaid = null;
        boolean isRealTimeRaid = false;

        if (adventure instanceof TimedRaidAdventure raid) {
            timedRaid = raid;
            isRealTimeRaid = raid.isRealTimeMode();
        }

        while (!adventure.isOver()) {
            System.out.println("\n--- STATUS ---");
            String status = adventure.getStatus();
            System.out.println(status);

            int playerIndex;

            if (isRealTimeRaid) {
                playerIndex = realTimePlayer;
            } else {
                playerIndex = 1;
                if (status.contains("Turn: Player 2") || status.contains("Current Turn: P2")) {
                    playerIndex = 2;
                }
            }

            System.out.println("\nPlayer " + playerIndex + " input:");
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("quit")) {
                System.out.println("Adventure abandoned.");
                abandoned = true;
                break;
            }

            adventure.handleInput(playerIndex, input);

            if (isRealTimeRaid && timedRaid != null && timedRaid.didConsumeLastInput()) {
                realTimePlayer = (realTimePlayer == 1) ? 2 : 1;
            }
        }

        if (abandoned) {
            return;
        }

        String result = adventure.getResult();
        System.out.println("\n" + result);

        String historyEntry = adventure.name() + " — " + result.split("\n")[0];
        p1.addQuestHistory(historyEntry);
        p2.addQuestHistory(historyEntry);

        p1.addGamePlayed();
        p2.addGamePlayed();

        String resultLine = result.split("\n")[0].toUpperCase();

        if (adventure.id().equals("relic_hunt")) {
                if (resultLine.contains("WINS!") && resultLine.contains(p1.getCharName().toUpperCase())) {
                    p1.addWin();
                } else if (resultLine.contains("WINS!") && resultLine.contains(p2.getCharName().toUpperCase())) {
                    p2.addWin();
                } else if (resultLine.contains("CO-OP WIN") || resultLine.contains("TIE")) {
                    p1.addWin();
                    p2.addWin();
                }
                p1.addRelicsCollected(countInventoryItems(result, p1.getCharName() + " Inventory:"));
                p2.addRelicsCollected(countInventoryItems(result, p2.getCharName() + " Inventory:"));
            } else if (adventure.id().equals("timed_raid")) {
                if (resultLine.contains("WIN")) {
                    p1.addWin();
                    p2.addWin();
                    p1.addRaidCompleted();
                    p2.addRaidCompleted();
                }
            }

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

    private int countInventoryItems(String result, String playerInventory) {
        int start = result.indexOf(playerInventory);
        if (start < 0) return 0;

        int count = 0;
        String[] lines = result.substring(start).split("\n");
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty() || line.contains("Inventory:")) break;
            if (line.startsWith("[Empty]")) break;
            count++;
        }
        return count;
    }
}