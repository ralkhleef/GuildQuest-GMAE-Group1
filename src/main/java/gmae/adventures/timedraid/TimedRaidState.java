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
        WORK_ON_OBJECTIVE_1,
        WORK_ON_OBJECTIVE_2,
        WORK_ON_OBJECTIVE_3,
        PASS
    }

    private final WorldClock clock;

    private final int roundMinutes;
    private int roundsRemaining;

    private PlayerId currentPlayer;
    private int actionsThisRound;

    private final int[] progress = new int[3];
    private final int[] required = new int[3];

    private Result result = Result.IN_PROGRESS;

    private long elapsedMinutes = 0;

    private int p1x = 0, p1y = 0;
    private int p2x = 0, p2y = 0;

    private final int width = 5;
    private final int height = 5;

    private final int[] objX = new int[]{1, 3, 4};
    private final int[] objY = new int[]{1, 2, 4};

    public TimedRaidState(WorldClock clock, int maxRounds, int roundMinutes, int[] requiredObjectives) {
        if (clock == null) throw new IllegalArgumentException("clock cannot be null");
        if (maxRounds <= 0) throw new IllegalArgumentException("maxRounds must be > 0");
        if (roundMinutes <= 0) throw new IllegalArgumentException("roundMinutes must be > 0");
        if (requiredObjectives == null || requiredObjectives.length != 3)
            throw new IllegalArgumentException("requiredObjectives must be length 3");
        for (int r : requiredObjectives) {
            if (r <= 0) throw new IllegalArgumentException("required objective values must be > 0");
        }

        this.clock = clock;
        this.roundsRemaining = maxRounds;
        this.roundMinutes = roundMinutes;
        System.arraycopy(requiredObjectives, 0, this.required, 0, 3);

        this.currentPlayer = PlayerId.P1;
        this.actionsThisRound = 0;

        this.p1x = 0;
        this.p1y = 0;
        this.p2x = 0;
        this.p2y = height - 1;
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

    public long getElapsedMinutes() {
        return elapsedMinutes;
    }

    public int getObjectivesCompleted() {
        int sum = 0;
        for (int i = 0; i < 3; i++) sum += progress[i];
        return sum;
    }

    public int getObjectivesRequired() {
        int sum = 0;
        for (int i = 0; i < 3; i++) sum += required[i];
        return sum;
    }

    public int getPlayerX(PlayerId p) {
        return (p == PlayerId.P1) ? p1x : p2x;
    }

    public int getPlayerY(PlayerId p) {
        return (p == PlayerId.P1) ? p1y : p2y;
    }

    public boolean isPlayerOnObjective(PlayerId p) {
        return objectiveAt(p) != -1;
    }

    public int getObjectiveIndexAtPlayer(PlayerId p) {
        return objectiveAt(p);
    }

    public void applyAction(PlayerId player, Action action) {
        if (isOver()) return;

        if (player != currentPlayer) {
            throw new IllegalStateException("Not " + player + "'s turn. Current: " + currentPlayer);
        }

        if (action != null) {
            switch (action) {
                case MOVE_UP -> move(player, 0, -1);
                case MOVE_DOWN -> move(player, 0, 1);
                case MOVE_LEFT -> move(player, -1, 0);
                case MOVE_RIGHT -> move(player, 1, 0);

                case WORK -> {
                    int idx = objectiveAt(player);
                    if (idx != -1) incObjective(idx);
                }

                case WORK_ON_OBJECTIVE_1 -> incObjective(0);
                case WORK_ON_OBJECTIVE_2 -> incObjective(1);
                case WORK_ON_OBJECTIVE_3 -> incObjective(2);

                case PASS -> { }
            }
        }

        actionsThisRound++;

        if (actionsThisRound == 1) {
            currentPlayer = PlayerId.P2;
        } else if (actionsThisRound == 2) {
            actionsThisRound = 0;
            currentPlayer = PlayerId.P1;

            roundsRemaining = Math.max(0, roundsRemaining - 1);
            clock.advance(roundMinutes);
            elapsedMinutes += roundMinutes;
        } else {
            throw new IllegalStateException("actionsThisRound invalid: " + actionsThisRound);
        }

        updateResult();
    }

    private void move(PlayerId p, int dx, int dy) {
        if (p == PlayerId.P1) {
            p1x = clamp(p1x + dx, 0, width - 1);
            p1y = clamp(p1y + dy, 0, height - 1);
        } else {
            p2x = clamp(p2x + dx, 0, width - 1);
            p2y = clamp(p2y + dy, 0, height - 1);
        }
    }

    private int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private int objectiveAt(PlayerId p) {
        int x = getPlayerX(p);
        int y = getPlayerY(p);
        for (int i = 0; i < 3; i++) {
            if (objX[i] == x && objY[i] == y) return i;
        }
        return -1;
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
}