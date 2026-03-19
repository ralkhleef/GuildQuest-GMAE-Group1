package gmae.adventures.timedraid;

import gmae.core.MiniAdventure;
import guildquest.model.GlobalTime;
import guildquest.model.WorldClock;

import java.util.Arrays;

public class TimedRaidAdventure implements MiniAdventure {

    private final int maxRounds;
    private final int roundMinutes;
    private final int[] required;
    private final TimedRaidMode mode;

    private WorldClock clock;
    private TimedRaidState state;
    private String lastActionMessage = "Start the raid.";
    private boolean showInstructions = true;
    private String endReason = "";

    private long realTimeLimitMs;
    private long realTimeStartMs;
    private boolean lastInputConsumed;

    public TimedRaidAdventure(int maxRounds, int roundMinutes, int[] required) {
        this(maxRounds, roundMinutes, required, TimedRaidMode.TURN_BASED);
    }

    public TimedRaidAdventure(int maxRounds, int roundMinutes, int[] required, TimedRaidMode mode) {
        this.maxRounds = maxRounds;
        this.roundMinutes = roundMinutes;
        this.required = Arrays.copyOf(required, required.length);
        this.mode = mode;
    }

    public TimedRaidAdventure() {
        this(7, 10, new int[]{2, 1, 1}, TimedRaidMode.TURN_BASED);
    }

    public TimedRaidAdventure(TimedRaidMode mode) {
        this(7, 10, new int[]{2, 1, 1}, mode);
    }

    @Override
    public String id() {
        return "timed_raid";
    }

    @Override
    public String name() {
        return mode == TimedRaidMode.REAL_TIME
                ? "Timed Raid Window (Real-Time Mode)"
                : "Timed Raid Window";
    }

    @Override
    public void start() {
        reset();

        if (mode == TimedRaidMode.REAL_TIME) {
            this.realTimeLimitMs = 60_000L;
            this.realTimeStartMs = System.currentTimeMillis();
            this.endReason = "";
        }

        this.lastInputConsumed = false;
    }

    @Override
    public void handleInput(int playerIndex, String input) {
        lastInputConsumed = false;

        if (state == null) start();
        if (state.isOver()) return;

        if (mode == TimedRaidMode.REAL_TIME) {
            updateRealTimeStatus();
            if (state.isOver()) return;
        }

        TimedRaidState.PlayerId player =
                (playerIndex == 1) ? TimedRaidState.PlayerId.P1 : TimedRaidState.PlayerId.P2;

        TimedRaidState.Action action = parseAction(input);
        if (action == null) {
            lastActionMessage = "Invalid command. Use: up, down, left, right, complete, pass";
            return;
        }

        int beforeX = state.getPlayerX(player);
        int beforeY = state.getPlayerY(player);
        int[] beforeProgress = state.getProgressCopy();

        if (mode == TimedRaidMode.REAL_TIME) {
            state.applyActionRealTime(player, action);
            int afterX = state.getPlayerX(player);
            int afterY = state.getPlayerY(player);
            int[] afterProgress = state.getProgressCopy();
            lastActionMessage = buildActionMessage(player, action, beforeX, beforeY, afterX, afterY, beforeProgress, afterProgress);
            showInstructions = false;
            lastInputConsumed = true;
            updateRealTimeStatus();
            return;
        }

        state.applyAction(player, action);
        int afterX = state.getPlayerX(player);
        int afterY = state.getPlayerY(player);
        int[] afterProgress = state.getProgressCopy();
        lastActionMessage = buildActionMessage(player, action, beforeX, beforeY, afterX, afterY, beforeProgress, afterProgress);
        showInstructions = false;
        lastInputConsumed = true;
    }

    @Override
    public String getStatus() {
        if (state == null) return "Not started.";

        if (mode == TimedRaidMode.REAL_TIME) {
            updateRealTimeStatus();
        }

        int[] progress = state.getProgressCopy();
        int[] needed = state.getRequiredCopy();

        String status = "";

        if (mode == TimedRaidMode.REAL_TIME) {
            status += "=== Timed Raid Window (REAL-TIME MODE) ===\n";
            status += "Time Remaining: " + getRemainingSeconds() + " sec\n";
            status += "Players act under a shared live timer.\n";
        } else {
            status += "=== Timed Raid Window ===\n";
            status += "Turn: " + formatPlayer(state.getCurrentPlayer()) + "\n";
            status += "Time: " + clock.now() + "   Rounds Left: " + state.getRoundsRemaining() + "\n";
        }

        status += "\n";
        status += "Player 1: (" + state.getPlayerX(TimedRaidState.PlayerId.P1) + "," + state.getPlayerY(TimedRaidState.PlayerId.P1) + ")";
        status += "   Player 2: (" + state.getPlayerX(TimedRaidState.PlayerId.P2) + "," + state.getPlayerY(TimedRaidState.PlayerId.P2) + ")\n";

        if (mode == TimedRaidMode.REAL_TIME) {
            status += "Player 1 Tile: " + currentTileText(TimedRaidState.PlayerId.P1) + "\n";
            status += "Player 2 Tile: " + currentTileText(TimedRaidState.PlayerId.P2) + "\n";
        } else {
            status += "Current Player Tile: " + currentTileText(state.getCurrentPlayer()) + "\n";
        }

        status += "Last Action: " + lastActionMessage + "\n";
        status += "\n";
        status += state.buildMapString() + "\n";
        status += "\n";
        status += "Objectives:\n";
        status += "Get Artifact " + progress[0] + "/" + needed[0]
                + "   Activate Gate " + progress[1] + "/" + needed[1]
                + "   Clear Enemies " + progress[2] + "/" + needed[2] + "\n";

        if (showInstructions) {
            status += "\n";
            status += "How to play:\n";
            status += "1. Move with up, down, left, and right.\n";
            status += "2. Stand on an objective tile.\n";
            status += "3. Use complete on that objective.\n";
            if (mode == TimedRaidMode.REAL_TIME) {
                status += "4. Players act under a shared countdown.\n";
                status += "5. Complete all objectives before time runs out.\n";
            } else {
                status += "4. Players alternate turns.\n";
                status += "5. After both players act, time advances by one round.\n";
            }
            status += "\n";
            status += "Objective Tiles:\n";
            status += "Get Artifact = (1,1)   Activate Gate = (3,2)   Clear Enemies = (4,4)\n";
        }

        if (state.isOver() && !endReason.isEmpty()) {
            status += "\n";
            status += "End Reason: " + endReason + "\n";
        }

        status += "\n";
        status += "Commands:\n";
        status += "up, down, left, right, complete, pass\n";
        status += "Tip: complete only helps when you are standing on an objective tile.";

        return status;
    }

    @Override
    public boolean isOver() {
        if (mode == TimedRaidMode.REAL_TIME && state != null) {
            updateRealTimeStatus();
        }
        return state != null && state.isOver();
    }

    @Override
    public String getResult() {
        if (state == null) return "No game played.";

        if (mode == TimedRaidMode.REAL_TIME) {
            updateRealTimeStatus();
            return "FINAL RESULT: " + state.getResult()
                    + " | Real-Time Mode"
                    + " | Reason: " + endReason
                    + " | Progress: " + formatProgress();
        }

        String reason;
        if (state.getResult() == TimedRaidState.Result.WIN) {
            reason = "All objectives were completed within the allowed rounds.";
        } else if (state.getResult() == TimedRaidState.Result.LOSE) {
            reason = "The raid ran out of rounds before all objectives were completed.";
        } else {
            reason = "";
        }

        return "FINAL RESULT: " + state.getResult()
                + " | Final Time: " + clock.now()
                + " | Reason: " + reason
                + " | Progress: " + formatProgress();
    }

    @Override
    public void reset() {
        this.clock = new WorldClock(new GlobalTime(0));
        this.state = new TimedRaidState(clock, maxRounds, roundMinutes, required);
        this.lastActionMessage = "Start the raid.";
        this.showInstructions = true;
        this.realTimeLimitMs = 0L;
        this.realTimeStartMs = 0L;
        this.endReason = "";
        this.lastInputConsumed = false;
    }

    public TimedRaidState getState() {
        return state;
    }

    public boolean isRealTimeMode() {
        return mode == TimedRaidMode.REAL_TIME;
    }

    public boolean didConsumeLastInput() {
        return lastInputConsumed;
    }

    private TimedRaidState.Action parseAction(String input) {
        if (input == null) return null;
        String s = input.trim().toLowerCase();

        return switch (s) {
            case "move up", "up" -> TimedRaidState.Action.MOVE_UP;
            case "move down", "down" -> TimedRaidState.Action.MOVE_DOWN;
            case "move left", "left" -> TimedRaidState.Action.MOVE_LEFT;
            case "move right", "right" -> TimedRaidState.Action.MOVE_RIGHT;
            case "complete" -> TimedRaidState.Action.WORK;
            case "pass" -> TimedRaidState.Action.PASS;
            default -> null;
        };
    }

    private void updateRealTimeStatus() {
        if (mode != TimedRaidMode.REAL_TIME || state == null) return;

        if (realTimeLimitMs <= 0L || realTimeStartMs <= 0L) return;

        if (state.getResult() == TimedRaidState.Result.WIN) {
            if (endReason.isEmpty()) {
                endReason = "All objectives were completed before time ran out.";
            }
            return;
        }

        if (state.getResult() == TimedRaidState.Result.LOSE) {
            if (endReason.isEmpty()) {
                endReason = "Time ran out before all objectives were completed.";
            }
            return;
        }

        if (state.allObjectivesCompletePublic()) {
            state.forceWin();
            endReason = "All objectives were completed before time ran out.";
            lastActionMessage = "Raid cleared in time.";
            return;
        }

        if (System.currentTimeMillis() - realTimeStartMs >= realTimeLimitMs) {
            state.forceLose();
            endReason = "Time ran out before all objectives were completed.";
            lastActionMessage = "Time expired.";
        }
    }

    private long getRemainingSeconds() {
        if (realTimeLimitMs <= 0L || realTimeStartMs <= 0L) return 0L;
        long elapsed = System.currentTimeMillis() - realTimeStartMs;
        long remaining = Math.max(0L, realTimeLimitMs - elapsed);
        return (remaining + 999L) / 1000L;
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
                    yield playerName + " used complete on an empty tile.";
                }
                yield playerName + " completed " + changedObjective(beforeProgress, afterProgress) + ".";
            }
            case PASS -> playerName + " passed.";
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
        if (idx == 0) return "Get Artifact tile (use complete)";
        if (idx == 1) return "Activate Gate tile (use complete)";
        if (idx == 2) return "Clear Enemies tile (use complete)";

        int x = state.getPlayerX(player);
        int y = state.getPlayerY(player);
        return "Empty (" + x + "," + y + ") - move to an objective tile";
    }
}