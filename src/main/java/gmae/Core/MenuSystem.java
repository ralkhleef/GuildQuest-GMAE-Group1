package gmae.core;

import java.util.List;
import java.util.Scanner;

public class MenuSystem {

    private final Scanner scanner;
    private final AdventureRegistry registry;
    private final GameEngine engine;

    public MenuSystem(Scanner scanner, AdventureRegistry registry, GameEngine engine) {
        this.scanner = scanner;
        this.registry = registry;
        this.engine = engine;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== GuildQuest Mini-Adventure Environment (GMAE) ===");
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

            MiniAdventure selected = parseChoice(choice, adventures);
            if (selected != null) {
                engine.run(selected);
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private MiniAdventure parseChoice(String choice, List<MiniAdventure> adventures) {
        try {
            int index = Integer.parseInt(choice) - 1;
            if (index >= 0 && index < adventures.size()) {
                return adventures.get(index);
            }
        } catch (NumberFormatException ignored) { }
        return null;
    }
}
