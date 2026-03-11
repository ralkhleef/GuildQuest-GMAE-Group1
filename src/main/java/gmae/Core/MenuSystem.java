package gmae.core;

import java.util.List;
import java.util.Scanner;

public class MenuSystem {

    private final Scanner scanner;
    private final AdventureRegistry registry;
    private final GameEngine engine;
    private final ProfileManager profileManager;

    private PlayerProfile player1;
    private PlayerProfile player2;

    public MenuSystem(Scanner scanner, AdventureRegistry registry, GameEngine engine, ProfileManager profileManager) {
        this.scanner = scanner;
        this.registry = registry;
        this.engine = engine;
        this.profileManager = profileManager;
    }

    public void show() {
        player1 = playerSetup("Player 1", null);
        player2 = playerSetup("Player 2", player1);

        while (true) {
            System.out.println("\n=== GuildQuest Mini-Adventure Environment (GMAE) ===");
            System.out.println("Current P1: " + player1);
            System.out.println("Current P2: " + player2);

            System.out.println("Enter 'S' to change players");

            System.out.println("Choose an adventure:");

            List<MiniAdventure> adventures = registry.getAll();
            for (int i = 0; i < adventures.size(); i++) {
                System.out.println((i + 1) + ") " + adventures.get(i).name());
            }
            System.out.println("0) Quit");

            System.out.print("> ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("0") || choice.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (choice.equalsIgnoreCase("S")) {
                swapPlayer();
                continue;
            }

            MiniAdventure selected = parseChoice(choice, adventures);
            if (selected != null) {
                engine.run(selected, player1, player2);
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    // P1 and P2 setup
    private PlayerProfile playerSetup(String playerLabel, PlayerProfile exclude) {
        System.out.println("\n=== " + playerLabel + " Setup ===");
        while (true) {
            PlayerProfile selectedProfile = null;

            System.out.println("1) Create new profile");
            System.out.println("2) Select existing profile");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) {
                selectedProfile = createNewProfile();
            } else if (choice.equals("2") && profileManager.size() > 0) {
                selectedProfile = selectExistingProfile();
            } else {
                if (choice.equals("2") && profileManager.size() <= 0) {
                    System.out.println("\nInvalid! Zero existing profiles");
                    continue;
                } else {
                    System.out.println("\nInvalid choice! Try again");
                    continue;
                }
            }

            if (exclude != null && selectedProfile.equals(exclude)) {
                System.out.println("\nError! Profile currently in use.");
                System.out.println("Try again");
            } else {
                return selectedProfile;
            }
        }
    }

    // Creating a new profile with preference
    private PlayerProfile createNewProfile() {
        String name = "";
        while (name.isEmpty()) {
            System.out.print("Enter character name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Error!: Name cannot be empty.");
            }
        }

        System.out.print("Enter realm preference (or press Enter for none): ");
        String realmChoice = scanner.nextLine().trim();
        if (realmChoice.isEmpty()) {
            realmChoice = "None";
        }

        PlayerProfile profile = profileManager.createProfile(name, realmChoice);
        System.out.println("Created: " + profile);
        return profile;
    }

    // Select player
    private PlayerProfile selectExistingProfile() {
        List<PlayerProfile> profiles = profileManager.getProfiles();
        System.out.println("\nSelect from existing profiles below: ");
        for (int i = 0; i < profiles.size(); i++) {
            System.out.println((i + 1) + ") " + profiles.get(i));
        }

        System.out.print("> ");
        String input = scanner.nextLine().trim();

        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < profiles.size()) {
                System.out.println("Selected: " + profiles.get(index));
                return profiles.get(index);
            }
        } catch (Exception e) {
            System.out.println("Error!");
        }

        System.out.println("Invalid Choice!");
        return createNewProfile();
    }

    // P1 and P2 switching profiles
    private void swapPlayer() {
        while (true) {
            System.out.println("\nSelect Player1 or Player2");
            System.out.println("1) Player1");
            System.out.println("2) player2");
            System.out.println("0) Quit");
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) {
                break;
            } else if (input.equals("1")) {
                player1 = playerSetup("Player1", player2);
                break;
            } else if (input.equals("2")) {
                player2 = playerSetup("Player2", player1);
                break;
            } else {
                System.out.println("Invalid input! Must select player 1 or 2");
            }
        }
    }

    private MiniAdventure parseChoice(String choice, List<MiniAdventure> adventures) {
        try {
            int index = Integer.parseInt(choice) - 1;
            if (index >= 0 && index < adventures.size()) {
                return adventures.get(index);
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }
}
