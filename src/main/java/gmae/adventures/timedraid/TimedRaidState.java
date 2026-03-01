package gmae.adventures.timedraid;

import guildquest.model.WorldClock;
import java.util.Arrays;

public class TimedRaidState {

    public enum PlayerId { P1, P2 }
    public enum Result { IN_PROGRESS, WIN, LOSE }

    public enum Action {
        WORK_ON_OBJECTIVE_1,
        WORK_ON_OBJECTIVE_2,
        WORK_ON_OBJECTIVE_3,
        PASS
    }

    private final WorldClock clock;

    private final int roundMinutes;
    private int roundsRemaining;

    private PlayerId currentPlayer;
    private int actionsThisRound; // 0 or 1 (after 2 actions -> end round)

    private final int[] progress = new int[3];
    private final int[] required = new int[3];

    private Result result = Result.IN_PROGRESS;

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

    public void applyAction(PlayerId player, Action action) {
        if (isOver()) return;

        if (player != currentPlayer) {
            throw new IllegalStateException("Not " + player + "'s turn. Current: " + currentPlayer);
        }

        // 1) Apply action
        if (action != null) {
            switch (action) {
                case WORK_ON_OBJECTIVE_1 -> incObjective(0);
                case WORK_ON_OBJECTIVE_2 -> incObjective(1);
                case WORK_ON_OBJECTIVE_3 -> incObjective(2);
                case PASS -> { /* do nothing */ }
            }
        }

        // 2) Advance turn structure
        actionsThisRound++;

        if (actionsThisRound == 1) {
            // P1 acted -> now P2
            currentPlayer = PlayerId.P2;
        } else if (actionsThisRound == 2) {
            // End of round, consume time after BOTH players act
            actionsThisRound = 0;
            currentPlayer = PlayerId.P1;

            roundsRemaining = Math.max(0, roundsRemaining - 1);
            clock.advance(roundMinutes);
        } else {
            throw new IllegalStateException("actionsThisRound invalid: " + actionsThisRound);
        }

        // 3) Check win/lose
        updateResult();
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