package gmae.core;

import guildquest.model.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerProfile {

    private String charName;
    private String realmPreference;
    private final Inventory inventory;
    private final List<String> questHistory;
    private final List<String> achievements;

    public PlayerProfile(String charName, String realmPreference) {
        this.charName = charName;
        this.realmPreference = realmPreference;
        this.inventory = new Inventory();
        this.questHistory = new ArrayList<>();
        this.achievements = new ArrayList<>();
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

    // get Realm Preference
    public String getRealmPreference() {
        return realmPreference;
    }

    // setting Realm Preference 
    // handle checking for input?
    public void setRealmPreference(String newRealmPreference) {
        this.realmPreference = newRealmPreference;
    }

    // get copy of inventory
    public List<Item> getInventory() {
        return inventory.getItems();
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

    @Override
    public String toString() {
        return charName + " (Realm: " + realmPreference + ")";
    }
}
