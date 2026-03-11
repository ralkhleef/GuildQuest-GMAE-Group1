package gmae.core;

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

            adventure.handleInput(playerIndex, input);
        }

        String result = adventure.getResult();
        System.out.println("\n" + result);

        String historyEntry = adventure.name() + " — " + result.split("\n")[0];
        p1.addQuestHistory(historyEntry);
        p2.addQuestHistory(historyEntry);
    }
}
