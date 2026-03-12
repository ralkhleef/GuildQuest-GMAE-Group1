package gmae.adventures.relichunt;

import guildquest.model.Item;

/**
 * DESIGN PATTERN APPLIED: Factory Method Pattern
 *
 * Adapted from A3's CharacterFactory.
 * Instead of creating Characters, this factory creates relic Items
 * with validated fields and rarity-based defaults, exactly matching
 *
 * Reuse evidence:
 *   - A3 source: CharacterFactory.java (uploaded)
 *   - What changed: target object is Item (not Character); factory methods
 *     map to rarity tiers (common/rare/legendary) instead of character classes.
 *   - Where used: RelicHuntAdventure spawns relics via this factory.
 */
public class RelicFactory {

    // Base factory method — validates and creates any relic
    public static Item createRelic(String name, String rarity, String description) {
        if (name == null || name.trim().isEmpty()) {
            name = "Unknown Relic";
        }
        if (rarity == null || rarity.trim().isEmpty()) {
            rarity = "common";
        }
        return new Item(name, "Relic", rarity, description);
    }

    // Convenience factory methods by rarity tier

    public static Item createCommonRelic(String name) {
        return createRelic(name, "common", "A weathered relic of little note.");
    }

    public static Item createRareRelic(String name) {
        return createRelic(name, "rare", "A relic pulsing with faint magical energy.");
    }

    public static Item createLegendaryRelic(String name) {
        return createRelic(name, "legendary", "An ancient relic radiating immense power.");
    }

    // Effect helper: returns how many "stun turns" a relic's rarity can trigger when collected
    // (used by Relic Hunt hazard tile logic)
    public static int hazardStunTurns(String rarity) {
        return switch (rarity.toLowerCase()) {
            case "legendary" -> 0; // legendary relics are always safe to collect
            case "rare"      -> 1; // rare relic on hazard = 1 stun turn
            default          -> 2; // common relic on hazard = 2 stun turns
        };
    }
}