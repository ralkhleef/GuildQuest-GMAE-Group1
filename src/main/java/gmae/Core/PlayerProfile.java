package gmae.core;

import guildquest.model.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerProfile {

    private Realm currentRealm;
    private String charName;
    private final Inventory inventory;
    private final List<String> questHistory;
    private final List<String> achievements;

    private int totalWins;
    private int totalRelicsCollected;
    private int totalRaidsCompleted;
    private int totalGamesPlayed;

    public PlayerProfile(String charName, Realm realmPreference) {
        this.charName = charName;
        this.currentRealm = realmPreference;
        this.inventory = new Inventory();
        this.questHistory = new ArrayList<>();
        this.achievements = new ArrayList<>();
    }

    public Realm getCurrentRealm() {
        return currentRealm;
    }

    public void setCurrentRealm(Realm r) {
        this.currentRealm = r;
    }

    // get characters name
    public String getCharName() {
        return charName;
    }

    // add item
    public void addItem(Item item) {
        inventory.addItem(item);
    }

    // changing characters name
    public void changeName(String newName) {
        this.charName = newName;
    }

    // get copy of inventory
    public List<Item> getInventory() {
        return inventory.getItems();
    }

    public int getInventorySize() {
        return inventory.size();
    }

    // snapshot of inventory
    public void printSnapshot() {
        inventory.displayInventory();
    }

    // get quest history
    public List<String> getQuestHistory() {
        return List.copyOf(questHistory);
    }

    // quest history
    public void printQuestHistory() {
        if (questHistory.isEmpty()) {
            System.out.println("  [Empty]");
            return;
        }
        for (int i = 0; i < questHistory.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + questHistory.get(i));
        }
    }

    // add quest history
    // no need for a remove, cant remove history
    public void addQuestHistory(String newEntry) {
        questHistory.add(newEntry);
    }

    // List of achievements
    public void printAchievements() {
        if (achievements.isEmpty()) {
            System.out.println("  [Empty]");
            return;
        }
        for (int i = 0; i < achievements.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + achievements.get(i));
        }
    }

    // no need for a remove since I assume achievements can't be taken back
    public void addAchievement(String newAchievement) {
        achievements.add(newAchievement);
    }

    public List<String> getAchievements() {
        return List.copyOf(achievements);
    }

    public int getTotalWins() { return totalWins; }
    public int getTotalRelicsCollected() { return totalRelicsCollected; }
    public int getTotalRaidsCompleted() { return totalRaidsCompleted; }
    public int getTotalGamesPlayed() { return totalGamesPlayed; }

    public void addWin() { totalWins++; }
    public void addRelicsCollected(int count) { totalRelicsCollected += count; }
    public void addRaidCompleted() { totalRaidsCompleted++; }
    public void addGamePlayed() { totalGamesPlayed++; }

    public List<String> checkAndAwardAchievements() {
        List<String> newlyAwarded = new ArrayList<>();

        String[][] milestones = {
            {"First Victory", "Win your first game", "wins", "1"},
            {"GuildQuest Veteran", "Win 5 games", "wins", "5"},
            {"Legend of the Realm", "Win 10 games", "wins", "10"},
            {"Relic Finder", "Collect 5 relics total", "relics", "5"},
            {"Relic Master", "Collect 15 relics total", "relics", "15"},
            {"Legendary Collector", "Collect 30 relics total", "relics", "30"},
            {"Raid Rookie", "Complete your first raid", "raids", "1"},
            {"Raid Veteran", "Complete 5 raids", "raids", "5"},
            {"Adventurer", "Play 3 games", "games", "3"},
            {"Seasoned Adventurer", "Play 10 games", "games", "10"},
        };

        for (String[] m : milestones) {
            String name = m[0];
            String desc = m[1];
            String type = m[2];
            int threshold = Integer.parseInt(m[3]);

            if (achievements.contains(name)) continue;

            boolean earned = switch (type) {
                case "wins" -> totalWins >= threshold;
                case "relics" -> totalRelicsCollected >= threshold;
                case "raids" -> totalRaidsCompleted >= threshold;
                case "games" -> totalGamesPlayed >= threshold;
                default -> false;
            };

            if (earned) {
                achievements.add(name);
                newlyAwarded.add(name + " — " + desc);
            }
        }

        return newlyAwarded;
    }

    @Override
    public String toString() {
        return charName + " (Realm: " + currentRealm + ")";
    }
}
