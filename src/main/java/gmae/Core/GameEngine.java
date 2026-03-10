package gmae.core;

import java.util.Scanner;

public class GameEngine {

    private final Scanner scanner;

    public GameEngine(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run(MiniAdventure adventure) {
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

        System.out.println("\n" + adventure.getResult());
    }
}
