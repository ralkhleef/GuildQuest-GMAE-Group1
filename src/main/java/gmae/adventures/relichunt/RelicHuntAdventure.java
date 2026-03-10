package gmae.adventures.relichunt;

import gmae.core.MiniAdventure;

/**
 * Relic Hunt mini-adventure.
 *
 * Implements the GMAE MiniAdventure interface so it can be registered
 * in AdventureRegistry and run by GameEngine without any core changes.
 *
 * Reused subsystems
 *   - guildquest.model.Item that represents each relic
 *   - guildquest.model.Inventory that each player holds collected relics
 *   - RelicFactory (Factory Method) that spawns relics by rarity tier
 *
 * Game rules:
 *   - Grid-based realm. Players move and collect relics.
 *   - Hazard tiles may stun a player (skip turns).
 *   - COMPETITIVE: first to reach relicsToWin, or most relics when grid empties.
 *   - CO-OP: collect combined relicsToWin before turns run out.
 */
public class RelicHuntAdventure implements MiniAdventure {

    private final int gridSize;
    private final int maxTurns;
    private final int relicsToWin;
    private final RelicHuntState.Mode mode;

    private RelicHuntState state;

    public RelicHuntAdventure(int gridSize, int maxTurns, int relicsToWin, RelicHuntState.Mode mode) {
        this.gridSize   = gridSize;
        this.maxTurns   = maxTurns;
        this.relicsToWin = relicsToWin;
        this.mode       = mode;
    }

    @Override
    public String id() {
        return "relic_hunt";
    }

    @Override
    public String name() {
        return "Relic Hunt";
    }

    @Override
    public void start() {
        reset();
    }

    @Override
    public void handleInput(int playerIndex, String input) {
        if (state == null) start();
        if (state.isOver()) return;
        String feedback = state.applyAction(playerIndex, input);
        System.out.println("[ACTION] " + feedback);
    }

    @Override
    public String getStatus() {
        if (state == null) return "Not started.";
        return state.buildStatusString();
    }

    @Override
    public boolean isOver() {
        return state != null && state.isOver();
    }

    @Override
    public String getResult() {
        if (state == null) return "No game played.";
        RelicHuntState.Result r = state.getResult();
        String summary = switch (r) {
            case P1_WINS  -> "Player 1 WINS! Relics collected: " + state.getP1Inventory().size();
            case P2_WINS  -> "Player 2 WINS! Relics collected: " + state.getP2Inventory().size();
            case TIE      -> "It's a TIE! Both collected: " + state.getP1Inventory().size();
            case COOP_WIN -> "CO-OP WIN! Combined relics: "
                    + (state.getP1Inventory().size() + state.getP2Inventory().size());
            case COOP_LOSE -> "CO-OP LOSS. Not enough relics collected in time.";
            default        -> "Game in progress.";
        };
        return "FINAL RESULT: " + summary + "\n"
                + "P1 Inventory:\n" + inventoryLines(state.getP1Inventory())
                + "P2 Inventory:\n" + inventoryLines(state.getP2Inventory());
    }

    @Override
    public void reset() {
        this.state = new RelicHuntState(gridSize, maxTurns, relicsToWin, mode);
    }

    private String inventoryLines(guildquest.model.Inventory inv) {
        StringBuilder sb = new StringBuilder();
        if (inv.getItems().isEmpty()) {
            sb.append("  [Empty]\n");
        } else {
            for (int i = 0; i < inv.getItems().size(); i++) {
                sb.append("  ").append(i + 1).append(". ")
                  .append(inv.getItems().get(i).describe()).append("\n");
            }
        }
        return sb.toString();
    }
}