package gmae.adventures.timedraid;

import guildquest.model.GlobalTime;
import guildquest.model.WorldClock;

public class TimedRaidTest {

    public static void main(String[] args) {
        WorldClock clock = new WorldClock(new GlobalTime(0));
        TimedRaidState state = new TimedRaidState(
                clock,
                7,
                10,
                new int[]{2, 1, 1}
        );

        System.out.println("=== Timed Raid Test ===");
        printState(state, clock);

        state.applyAction(TimedRaidState.PlayerId.P1, TimedRaidState.Action.MOVE_RIGHT);
        printStep("P1 move right", state, clock);

        state.applyAction(TimedRaidState.PlayerId.P2, TimedRaidState.Action.MOVE_RIGHT);
        printStep("P2 move right", state, clock);

        state.applyAction(TimedRaidState.PlayerId.P1, TimedRaidState.Action.MOVE_DOWN);
        printStep("P1 move down", state, clock);

        state.applyAction(TimedRaidState.PlayerId.P2, TimedRaidState.Action.MOVE_DOWN);
        printStep("P2 move down", state, clock);

        state.applyAction(TimedRaidState.PlayerId.P1, TimedRaidState.Action.WORK);
        printStep("P1 complete", state, clock);

        state.applyAction(TimedRaidState.PlayerId.P2, TimedRaidState.Action.MOVE_RIGHT);
        printStep("P2 move right", state, clock);

        state.applyAction(TimedRaidState.PlayerId.P1, TimedRaidState.Action.WORK);
        printStep("P1 complete", state, clock);

        state.applyAction(TimedRaidState.PlayerId.P2, TimedRaidState.Action.PASS);
        printStep("P2 pass", state, clock);

        System.out.println("FINAL RESULT: " + state.getResult());
    }

    private static void printStep(String actionText, TimedRaidState state, WorldClock clock) {
        System.out.println("Action: " + actionText);
        printState(state, clock);
        System.out.println("----------------------------");
    }

    private static void printState(TimedRaidState state, WorldClock clock) {
        int[] progress = state.getProgressCopy();
        int[] needed = state.getRequiredCopy();

        System.out.println("Turn: " + formatPlayer(state.getCurrentPlayer()));
        System.out.println("Time: " + clock.now() + "   Rounds Left: " + state.getRoundsRemaining());
        System.out.println("Player 1: (" + state.getPlayerX(TimedRaidState.PlayerId.P1) + "," + state.getPlayerY(TimedRaidState.PlayerId.P1) + ")");
        System.out.println("Player 2: (" + state.getPlayerX(TimedRaidState.PlayerId.P2) + "," + state.getPlayerY(TimedRaidState.PlayerId.P2) + ")");
        System.out.println("Objectives: Get Artifact " + progress[0] + "/" + needed[0]
                + "   Activate Gate " + progress[1] + "/" + needed[1]
                + "   Clear Enemies " + progress[2] + "/" + needed[2]);
    }

    private static String formatPlayer(TimedRaidState.PlayerId player) {
        return player == TimedRaidState.PlayerId.P1 ? "Player 1" : "Player 2";
    }
}