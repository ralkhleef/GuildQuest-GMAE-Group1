package gmae.adventures.timedraid;

import gmae.core.MiniAdventure;
import guildquest.model.GlobalTime;
import guildquest.model.WorldClock;

import java.util.Arrays;

public class TimedRaidAdventure implements MiniAdventure {

    private final int maxRounds;
    private final int roundMinutes;
    private final int[] required;

    private WorldClock clock;
    private TimedRaidState state;
    private String lastActionMessage = "Start the raid.";
    private boolean showInstructions = true;

    public TimedRaidAdventure(int maxRounds, int roundMinutes, int[] required) {
        this.maxRounds = maxRounds;
        this.roundMinutes = roundMinutes;
        this.required = Arrays.copyOf(required, required.length);
    }

<<<<<<< Updated upstream
=======
    public TimedRaidAdventure() {
        this(7, 10, new int[]{2, 1, 1});
    }

>>>>>>> Stashed changes
    @Override
    public String id() {
        return "timed_raid";
    }

    @Override
    public String name() {
        return "Timed Raid Window";
    }

    @Override
    public void start() {
        reset();
    }

    @Override
    public void handleInput(int playerIndex, String input) {
        if (state == null) start();
        if (state.isOver()) return;

        TimedRaidState.PlayerId player =
                (playerIndex == 1) ? TimedRaidState.PlayerId.P1 : TimedRaidState.PlayerId.P2;

        TimedRaidState.Action action = parseAction(input);
        if (action == null) {
            lastActionMessage = "Invalid command. Use: u d l r w p";
            return;
        }

        int beforeX = state.getPlayerX(player);
        int beforeY = state.getPlayerY(player);
        int[] beforeProgress = state.getProgressCopy();

        state.applyAction(player, action);

        int afterX = state.getPlayerX(player);
        int afterY = state.getPlayerY(player);
        int[] afterProgress = state.getProgressCopy();

        lastActionMessage = buildActionMessage(player, action, beforeX, beforeY, afterX, afterY, beforeProgress, afterProgress);
        showInstructions = false;
    }

    @Override
    public String getStatus() {
        if (state == null) return "Not started.";
<<<<<<< Updated upstream
        return ""
                + "Player Turn: " + state.getCurrentPlayer() + "\n"
                + "Rounds Remaining: " + state.getRoundsRemaining() + "\n"
                + "World Time: " + clock.now() + "\n"
                + "Progress: " + Arrays.toString(state.getProgressCopy()) + "\n"
                + "Required: " + Arrays.toString(state.getRequiredCopy()) + "\n"
                + "Commands: obj1, obj2, obj3, pass";
=======

        int[] progress = state.getProgressCopy();
        int[] needed = state.getRequiredCopy();

        String status = ""
                + "=== Timed Raid Window ===\n"
                + "Turn: " + formatPlayer(state.getCurrentPlayer()) + "\n"
                + "Time: " + clock.now() + "   Rounds Left: " + state.getRoundsRemaining() + "\n"
                + "\n"
                + "Player 1: (" + state.getPlayerX(TimedRaidState.PlayerId.P1) + "," + state.getPlayerY(TimedRaidState.PlayerId.P1) + ")"
                + "   Player 2: (" + state.getPlayerX(TimedRaidState.PlayerId.P2) + "," + state.getPlayerY(TimedRaidState.PlayerId.P2) + ")\n"
                + "Current Player Tile: " + currentTileText(state.getCurrentPlayer()) + "\n"
                + "Last Action: " + lastActionMessage + "\n"
                + "\n"
                + "Objectives:\n"
                + "Get Artifact " + progress[0] + "/" + needed[0]
                + "   Activate Gate " + progress[1] + "/" + needed[1]
                + "   Clear Enemies " + progress[2] + "/" + needed[2] + "\n";

        if (showInstructions) {
            status += "\n"
                    + "How to play:\n"
                    + "1. Move with u, d, l, r.\n"
                    + "2. Stand on an objective tile.\n"
                    + "3. Use w to work on that objective.\n"
                    + "4. Players alternate turns.\n"
                    + "5. After both players act, time advances by one round.\n"
                    + "\n"
                    + "Objective Tiles:\n"
                    + "Get Artifact = (1,1)   Activate Gate = (3,2)   Clear Enemies = (4,4)\n"
                    + "\n";
        }

        status += "\n"
                + "Commands:\n"
                + "u = up   d = down   l = left   r = right   w = work   p = pass\n"
                + "Tip: work only helps when you are standing on an objective tile.";

        return status;
>>>>>>> Stashed changes
    }

    @Override
    public boolean isOver() {
        return state != null && state.isOver();
    }

    @Override
    public String getResult() {
        if (state == null) return "No game played.";
        return "FINAL RESULT: " + state.getResult() + " | Final Time: " + clock.now()
                + " | Progress: " + formatProgress();
    }

    @Override
    public void reset() {
        this.clock = new WorldClock(new GlobalTime(0));
        this.state = new TimedRaidState(clock, maxRounds, roundMinutes, required);
        this.lastActionMessage = "Start the raid.";
        this.showInstructions = true;
    }

    private TimedRaidState.Action parseAction(String input) {
        if (input == null) return null;
        String s = input.trim().toLowerCase();

        return switch (s) {
<<<<<<< Updated upstream
            case "obj1", "1", "o1" -> TimedRaidState.Action.WORK_ON_OBJECTIVE_1;
            case "obj2", "2", "o2" -> TimedRaidState.Action.WORK_ON_OBJECTIVE_2;
            case "obj3", "3", "o3" -> TimedRaidState.Action.WORK_ON_OBJECTIVE_3;
            default -> TimedRaidState.Action.PASS;
=======
            case "move up", "up", "u" -> TimedRaidState.Action.MOVE_UP;
            case "move down", "down", "d" -> TimedRaidState.Action.MOVE_DOWN;
            case "move left", "left", "l" -> TimedRaidState.Action.MOVE_LEFT;
            case "move right", "right", "r" -> TimedRaidState.Action.MOVE_RIGHT;

            case "work", "w" -> TimedRaidState.Action.WORK;
            case "pass", "p" -> TimedRaidState.Action.PASS;

            case "get artifact", "artifact", "obj1", "1", "o1", "work 1" -> TimedRaidState.Action.WORK_ON_OBJECTIVE_1;
            case "activate gate", "gate", "obj2", "2", "o2", "work 2" -> TimedRaidState.Action.WORK_ON_OBJECTIVE_2;
            case "clear enemies", "enemies", "obj3", "3", "o3", "work 3" -> TimedRaidState.Action.WORK_ON_OBJECTIVE_3;

            default -> null;
>>>>>>> Stashed changes
        };
    }

    private String buildActionMessage(TimedRaidState.PlayerId player,
                                      TimedRaidState.Action action,
                                      int beforeX,
                                      int beforeY,
                                      int afterX,
                                      int afterY,
                                      int[] beforeProgress,
                                      int[] afterProgress) {
        String playerName = formatPlayer(player);

        return switch (action) {
            case MOVE_UP -> (beforeX == afterX && beforeY == afterY)
                    ? playerName + " cannot move up."
                    : playerName + " moved up.";
            case MOVE_DOWN -> (beforeX == afterX && beforeY == afterY)
                    ? playerName + " cannot move down."
                    : playerName + " moved down.";
            case MOVE_LEFT -> (beforeX == afterX && beforeY == afterY)
                    ? playerName + " cannot move left."
                    : playerName + " moved left.";
            case MOVE_RIGHT -> (beforeX == afterX && beforeY == afterY)
                    ? playerName + " cannot move right."
                    : playerName + " moved right.";
            case WORK -> {
                if (Arrays.equals(beforeProgress, afterProgress)) {
                    yield playerName + " used work on an empty tile.";
                }
                yield playerName + " worked on " + changedObjective(beforeProgress, afterProgress) + ".";
            }
            case PASS -> playerName + " passed.";
            case WORK_ON_OBJECTIVE_1 -> playerName + " worked on Get Artifact.";
            case WORK_ON_OBJECTIVE_2 -> playerName + " worked on Activate Gate.";
            case WORK_ON_OBJECTIVE_3 -> playerName + " worked on Clear Enemies.";
        };
    }

    private String changedObjective(int[] beforeProgress, int[] afterProgress) {
        if (afterProgress[0] > beforeProgress[0]) return "Get Artifact";
        if (afterProgress[1] > beforeProgress[1]) return "Activate Gate";
        if (afterProgress[2] > beforeProgress[2]) return "Clear Enemies";
        return "an objective";
    }

    private String formatPlayer(TimedRaidState.PlayerId player) {
        return player == TimedRaidState.PlayerId.P1 ? "Player 1" : "Player 2";
    }

    private String formatProgress() {
        int[] progress = state.getProgressCopy();
        return "[Get Artifact=" + progress[0]
                + ", Activate Gate=" + progress[1]
                + ", Clear Enemies=" + progress[2] + "]";
    }

    private String currentTileText(TimedRaidState.PlayerId player) {
        int idx = state.getObjectiveIndexAtPlayer(player);
        if (idx == 0) return "Get Artifact tile (use w)";
        if (idx == 1) return "Activate Gate tile (use w)";
        if (idx == 2) return "Clear Enemies tile (use w)";

        int x = state.getPlayerX(player);
        int y = state.getPlayerY(player);
        return "Empty (" + x + "," + y + ") - move to an objective tile";
    }
}