package guildquest.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Reused from A3 Added countByRarity() helper used by Relic Hunt win-condition
 * logic.
 */
public class Inventory {

    private final List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * Returns a defensive copy so callers cannot mutate internal state.
     */
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public int size() {
        return items.size();
    }

    /**
     * Added for Relic Hunt: count how many relics of a given rarity the player
     * holds. e.g. countByRarity("legendary")
     */
    public int countByRarity(String rarity) {
        int count = 0;
        for (Item item : items) {
            if (item.getRarity().equalsIgnoreCase(rarity)) {
                count++;
            }
        }
        return count;
    }

    public void displayInventory() {
        if (items.isEmpty()) {
            System.out.println("  [Empty]");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + items.get(i).describe());
        }
    }
}
