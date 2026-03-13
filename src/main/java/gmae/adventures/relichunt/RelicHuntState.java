package gmae.adventures.relichunt;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import guildquest.model.Inventory;
import guildquest.model.Item;
import guildquest.model.Location;

/**
 * Holds all mutable state for a Relic Hunt session.
 * Separated from RelicHuntAdventure to keep the MiniAdventure adapter thin.
 */
public class RelicHuntState {

    public enum Mode { COMPETITIVE, COOP }
    public enum Result { IN_PROGRESS, P1_WINS, P2_WINS, TIE, COOP_WIN, COOP_LOSE }

    // --- Grid ---
    private final int gridSize;

    // Player positions
    private Location p1Pos;
    private Location p2Pos;

    // Inventories (reused from A3)
    private final Inventory p1Inventory = new Inventory();
    private final Inventory p2Inventory = new Inventory();

    // Stun counters (skip-turn mechanic from hazard tiles)
    private int p1StunTurns = 0;
    private int p2StunTurns = 0;

    // Relics on the grid: location -> Item
    private final Map<Location, Item> relicsOnGrid = new HashMap<>();

    // Hazard tile locations
    private final Set<Location> hazardTiles = new HashSet<>();

    // Turn tracking
    private int currentPlayer = 1; // 1 or 2
    private int turnsRemaining;

    // Win config
    private final int relicsToWin; // per-player (competitive) or combined (coop)
    private final Mode mode;

    private Result result = Result.IN_PROGRESS;

    public RelicHuntState(int gridSize, int maxTurns, int relicsToWin, Mode mode) {
        this.gridSize = gridSize;
        this.turnsRemaining = maxTurns;
        this.relicsToWin = relicsToWin;
        this.mode = mode;

        p1Pos = new Location(0, 0);
        p2Pos = new Location(gridSize - 1, gridSize - 1);

        spawnRelics();
        spawnHazards();
    }

    // Setup

    private void spawnRelics() {
        // Spawn a mix of rarities using RelicFactory (Factory Method pattern)
        String[][] relicDefs = {
            {"Ancient Sword Shard",  "rare"},
            {"Crumbled Idol",        "common"},
            {"Glowing Orb",          "legendary"},
            {"Dusty Tome",           "common"},
            {"Crystal Fragment",     "rare"},
            {"Rusted Amulet",        "common"},
        };

        Random rng = new Random(42); // fixed seed for reproducibility
        Set<Location> used = new HashSet<>();
        used.add(p1Pos);
        used.add(p2Pos);

        for (String[] def : relicDefs) {
            Location loc = randomFreeLocation(rng, used);
            if (loc == null) break;
            used.add(loc);
            Item relic = switch (def[1]) {
                case "rare"      -> RelicFactory.createRareRelic(def[0]);
                case "legendary" -> RelicFactory.createLegendaryRelic(def[0]);
                default          -> RelicFactory.createCommonRelic(def[0]);
            };
            relicsOnGrid.put(loc, relic);
        }
    }

    private void spawnHazards() {
        Random rng = new Random(99);
        Set<Location> avoid = new HashSet<>(relicsOnGrid.keySet());
        avoid.add(p1Pos);
        avoid.add(p2Pos);

        for (int i = 0; i < 3; i++) {
            Location loc = randomFreeLocation(rng, avoid);
            if (loc != null) {
                hazardTiles.add(loc);
                avoid.add(loc);
            }
        }
    }

    private Location randomFreeLocation(Random rng, Set<Location> blocked) {
        for (int attempt = 0; attempt < 100; attempt++) {
            int x = rng.nextInt(gridSize);
            int y = rng.nextInt(gridSize);
            Location l = new Location(x, y);
            if (!blocked.contains(l)) return l;
        }
        return null;
    }

    // public API

    public int getCurrentPlayer()  { return currentPlayer; }
    public int getTurnsRemaining() { return turnsRemaining; }
    public Inventory getP1Inventory() { return p1Inventory; }
    public Inventory getP2Inventory() { return p2Inventory; }
    public Location getP1Pos()     { return p1Pos; }
    public Location getP2Pos()     { return p2Pos; }
    public Result getResult()      { return result; }
    public boolean isOver()        { return result != Result.IN_PROGRESS; }
    public Mode getMode()          { return mode; }
    public Map<Location, Item> getRelicsOnGrid() { return Collections.unmodifiableMap(relicsOnGrid); }
    public Set<Location> getHazardTiles()        { return Collections.unmodifiableSet(hazardTiles); }

    /**
     * Apply a player action. Valid actions: "up", "down", "left", "right", "collect", "pass"
     */
    public String applyAction(int player, String action) {
        if (isOver()) return "Game is already over.";
        if (player != currentPlayer) return "Not your turn.";

        // Handle stun
        if (player == 1 && p1StunTurns > 0) {
            p1StunTurns--;
            advanceTurn();
            return "P1 is stunned! Turn skipped. Stun turns left: " + p1StunTurns;
        }
        if (player == 2 && p2StunTurns > 0) {
            p2StunTurns--;
            advanceTurn();
            return "P2 is stunned! Turn skipped. Stun turns left: " + p2StunTurns;
        }

        String feedback;
        String cmd = (action == null) ? "pass" : action.trim().toLowerCase();

        switch (cmd) {
            case "up"    -> feedback = movePlayer(player,  0, -1);
            case "down"  -> feedback = movePlayer(player,  0,  1);
            case "left"  -> feedback = movePlayer(player, -1,  0);
            case "right" -> feedback = movePlayer(player,  1,  0);
            case "collect" -> feedback = collectRelic(player);
            case "pass"  -> feedback = "Player " + player + " passed.";
            default      -> { return "Unknown action: " + cmd + ". Use: up/down/left/right/collect/pass"; }
        }

        advanceTurn();
        updateResult();
        return feedback;
    }

    private String movePlayer(int player, int dx, int dy) {
        Location cur = (player == 1) ? p1Pos : p2Pos;
        int nx = cur.x() + dx;
        int ny = cur.y() + dy;

        if (nx < 0 || nx >= gridSize || ny < 0 || ny >= gridSize) {
            return "P" + player + " can't move outside the grid.";
        }

        Location next = new Location(nx, ny);

        if (player == 1) p1Pos = next;
        else             p2Pos = next;

        StringBuilder msg = new StringBuilder("P" + player + " moved to " + next);

        // Hazard tile check
        boolean stunned = false;
        if (hazardTiles.contains(next)) {
            Item atLocation = relicsOnGrid.get(next);
            int stun = (atLocation != null)
                    ? RelicFactory.hazardStunTurns(atLocation.getRarity())
                    : 2;
            if (player == 1) p1StunTurns += stun;
            else             p2StunTurns += stun;
            msg.append(" — HAZARD! Stunned for ").append(stun).append(" turn(s).");
            stunned = true;
        }

        // Auto-collect relic if not stunned
        if (!stunned && relicsOnGrid.containsKey(next)) {
            Item relic = relicsOnGrid.remove(next);
            Inventory inv = (player == 1) ? p1Inventory : p2Inventory;
            inv.addItem(relic);
            msg.append(" — Found ").append(relic.describe()).append("!");
        }

        return msg.toString();
    }

    private String collectRelic(int player) {
        Location pos = (player == 1) ? p1Pos : p2Pos;
        Item relic = relicsOnGrid.get(pos);

        if (relic == null) return "No relic here to collect.";

        relicsOnGrid.remove(pos);
        Inventory inv = (player == 1) ? p1Inventory : p2Inventory;
        inv.addItem(relic);

        return "P" + player + " collected: " + relic.describe();
    }

    private void advanceTurn() {
        turnsRemaining--;
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    private void updateResult() {
        if (mode == Mode.COMPETITIVE) {
            boolean p1Done = p1Inventory.size() >= relicsToWin;
            boolean p2Done = p2Inventory.size() >= relicsToWin;
            boolean noMore = relicsOnGrid.isEmpty() || turnsRemaining <= 0;

            if (p1Done && p2Done) result = Result.TIE;
            else if (p1Done)      result = Result.P1_WINS;
            else if (p2Done)      result = Result.P2_WINS;
            else if (noMore) {
                int c1 = p1Inventory.size(), c2 = p2Inventory.size();
                if (c1 > c2)      result = Result.P1_WINS;
                else if (c2 > c1) result = Result.P2_WINS;
                else              result = Result.TIE;
            }
        } else { // COOP
            int combined = p1Inventory.size() + p2Inventory.size();
            if (combined >= relicsToWin)  result = Result.COOP_WIN;
            else if (turnsRemaining <= 0) result = Result.COOP_LOSE;
        }
    }

    public String buildMapString() {
        StringBuilder sb = new StringBuilder();
        // column headers
        sb.append("  ");
        for (int x = 0; x < gridSize; x++) {
            sb.append(x).append(" ");
        }
        sb.append("\n");

        for (int y = 0; y < gridSize; y++) {
            sb.append(y).append(" ");
            for (int x = 0; x < gridSize; x++) {
                Location loc = new Location(x, y);
                boolean isP1 = p1Pos.x() == x && p1Pos.y() == y;
                boolean isP2 = p2Pos.x() == x && p2Pos.y() == y;

                if (isP1 && isP2) {
                    sb.append("X ");  // both players on same tile
                } else if (isP1) {
                    sb.append("1 ");
                } else if (isP2) {
                    sb.append("2 ");
                } else if (relicsOnGrid.containsKey(loc)) {
                    sb.append("R ");
                } else if (hazardTiles.contains(loc)) {
                    sb.append("H ");
                } else {
                    sb.append("_ ");
                }
            }
            sb.append("\n");
        }
        sb.append("1=P1  2=P2  R=Relic  H=Hazard  X=Both Players");
        return sb.toString();
    }

    public String buildStatusString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mode: ").append(mode).append("\n");
        sb.append("Turns Remaining: ").append(turnsRemaining).append("\n");
        sb.append("Current Turn: P").append(currentPlayer);
        if (currentPlayer == 1 && p1StunTurns > 0)
            sb.append(" [STUNNED x").append(p1StunTurns).append("]");
        if (currentPlayer == 2 && p2StunTurns > 0)
            sb.append(" [STUNNED x").append(p2StunTurns).append("]");
        sb.append("\n");
        sb.append("P1 @ ").append(p1Pos).append(" | Relics: ").append(p1Inventory.size()).append("\n");
        sb.append("P2 @ ").append(p2Pos).append(" | Relics: ").append(p2Inventory.size()).append("\n");
        sb.append("Relics on grid: ").append(relicsOnGrid.size()).append("\n");
        sb.append("Hazard tiles: ").append(hazardTiles).append("\n");
        sb.append("\n").append(buildMapString()).append("\n");
        sb.append("\nCommands: up, down, left, right, collect, pass");
        return sb.toString();
    }
}
