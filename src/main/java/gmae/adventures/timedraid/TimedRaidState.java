package gmae.adventures.timedraid;

import guildquest.model.WorldClock;
import java.util.Arrays;

public class TimedRaidState {

    public enum PlayerId { P1, P2 }
    public enum Result { IN_PROGRESS, WIN, LOSE }

    public enum Action {
        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT,
        WORK,
        PASS
    }

    private final WorldClock clock;

    private final int roundMinutes;
    private final int maxRounds;
    private int roundsRemaining;

    private PlayerId currentPlayer;
    private int actionsThisRound;

    private final int[] progress = new int[3];
    private final int[] required = new int[3];

    private Result result = Result.IN_PROGRESS;

    private int p1x;
    private int p1y;
    private int p2x;
    private int p2y;

    public TimedRaidState(WorldClock clock, int maxRounds, int roundMinutes, int[] requiredObjectives) {
        if (clock == null) throw new IllegalArgumentException("clock cannot be null");
        if (maxRounds <= 0) throw new IllegalArgumentException("maxRounds must be > 0");
        if (roundMinutes <= 0) throw new IllegalArgumentException("roundMinutes must be > 0");
        if (requiredObjectives == null || requiredObjectives.length != 3) {
            throw new IllegalArgumentException("requiredObjectives must be length 3");
        }
        for (int r : requiredObjectives) {
            if (r <= 0) throw new IllegalArgumentException("required objective values must be > 0");
        }

        this.clock = clock;
        this.maxRounds = maxRounds;
        this.roundsRemaining = maxRounds;
        this.roundMinutes = roundMinutes;
        System.arraycopy(requiredObjectives, 0, this.required, 0, 3);

        this.currentPlayer = PlayerId.P1;
        this.actionsThisRound = 0;

        this.p1x = 0;
        this.p1y = 0;
        this.p2x = 2;
        this.p2y = 3;
    }

    public PlayerId getCurrentPlayer() {
        return currentPlayer;
    }

    public int getRoundsRemaining() {
        return roundsRemaining;
    }

    public int[] getProgressCopy() {
        return Arrays.copyOf(progress, progress.length);
    }

    public int[] getRequiredCopy() {
        return Arrays.copyOf(required, required.length);
    }

    public Result getResult() {
        return result;
    }

    public boolean isOver() {
        return result != Result.IN_PROGRESS;
    }

    public WorldClock getClock() {
        return clock;
    }

    public int getElapsedMinutes() {
        return (maxRounds - roundsRemaining) * roundMinutes;
    }

    public int getPlayerX(PlayerId player) {
        return player == PlayerId.P1 ? p1x : p2x;
    }

    public int getPlayerY(PlayerId player) {
        return player == PlayerId.P1 ? p1y : p2y;
    }

    public int getObjectiveIndexAtPlayer(PlayerId player) {
        int x = getPlayerX(player);
        int y = getPlayerY(player);

        if (x == 1 && y == 1) return 0;
        if (x == 3 && y == 2) return 1;
        if (x == 4 && y == 4) return 2;
        return -1;
    }

    public boolean isPlayerOnObjective(PlayerId player) {
        return getObjectiveIndexAtPlayer(player) >= 0;
    }

    public int getObjectivesCompleted() {
        return progress[0] + progress[1] + progress[2];
    }

    public int getObjectivesRequired() {
        return required[0] + required[1] + required[2];
    }

    public void applyAction(PlayerId player, Action action) {
        if (isOver()) return;

        if (player != currentPlayer) {
            throw new IllegalStateException("Not " + player + "'s turn. Current: " + currentPlayer);
        }

        applyActionInternal(player, action);

        actionsThisRound++;

        if (actionsThisRound == 1) {
            currentPlayer = PlayerId.P2;
        } else if (actionsThisRound == 2) {
            actionsThisRound = 0;
            currentPlayer = PlayerId.P1;
            roundsRemaining = Math.max(0, roundsRemaining - 1);
            clock.advance(roundMinutes);
        } else {
            throw new IllegalStateException("actionsThisRound invalid: " + actionsThisRound);
        }

        updateResult();
    }

    public void applyActionRealTime(PlayerId player, Action action) {
        if (isOver()) return;
        applyActionInternal(player, action);
        updateResultRealTime();
    }

    public void forceWin() {
        result = Result.WIN;
    }

    public void forceLose() {
        result = Result.LOSE;
    }

    public boolean allObjectivesCompletePublic() {
        return allObjectivesComplete();
    }

    private void applyActionInternal(PlayerId player, Action action) {
        if (action == null) return;

        switch (action) {
            case MOVE_UP -> movePlayer(player, 0, -1);
            case MOVE_DOWN -> movePlayer(player, 0, 1);
            case MOVE_LEFT -> movePlayer(player, -1, 0);
            case MOVE_RIGHT -> movePlayer(player, 1, 0);
            case WORK -> workOnCurrentTile(player);
            case PASS -> { }
        }
    }

    private void movePlayer(PlayerId player, int dx, int dy) {
        int x = getPlayerX(player);
        int y = getPlayerY(player);

        int nx = Math.max(0, Math.min(4, x + dx));
        int ny = Math.max(0, Math.min(4, y + dy));

        if (player == PlayerId.P1) {
            p1x = nx;
            p1y = ny;
        } else {
            p2x = nx;
            p2y = ny;
        }
    }

    private void workOnCurrentTile(PlayerId player) {
        int idx = getObjectiveIndexAtPlayer(player);
        if (idx >= 0) {
            incObjective(idx);
        }
    }

    private void incObjective(int idx) {
        if (progress[idx] < required[idx]) {
            progress[idx]++;
        }
    }

    private boolean allObjectivesComplete() {
        for (int i = 0; i < 3; i++) {
            if (progress[i] < required[i]) return false;
        }
        return true;
    }

    private void updateResult() {
        if (allObjectivesComplete()) {
            result = Result.WIN;
        } else if (roundsRemaining <= 0) {
            result = Result.LOSE;
        } else {
            result = Result.IN_PROGRESS;
        }
    }

    private void updateResultRealTime() {
        if (allObjectivesComplete()) {
            result = Result.WIN;
        } else {
            result = Result.IN_PROGRESS;
        }
    }
}