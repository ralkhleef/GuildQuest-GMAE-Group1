package guildquest.model;

/**
 * Reused from A3 
 * No logic changes — only package declaration added.
 */
public class Item {
    private String name;
    private String rarity;
    private String type;
    private String description;

    public Item(String name, String type, String rarity, String description) {
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.description = description;
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public void reclassify(String type) {
        this.type = type;
    }

    public void changeRarity(String rarity) {
        this.rarity = rarity;
    }

    public String describe() {
        return String.format("%s (%s, %s): %s", name, type, rarity, description);
    }

    public String getName()        { return name; }
    public String getRarity()      { return rarity; }
    public String getType()        { return type; }
    public String getDescription() { return description; }
}