package gmae.adventures.timedraid;

import guildquest.model.GlobalTime;
import guildquest.model.WorldClock;

import java.util.Arrays;

public class TimedRaidTest {

    public static void main(String[] args) {

        WorldClock clock = new WorldClock(new GlobalTime(0));
        TimedRaidState state = new TimedRaidState(
                clock,
                7,
                10,
                new int[]{2, 1, 1}
        );

        int step = 0;

        while (!state.isOver()) {
            int[] progress = state.getProgressCopy();
            int[] needed = state.getRequiredCopy();

            System.out.println("=== Timed Raid Test ===");
            System.out.println("Turn: " + formatPlayer(state.getCurrentPlayer()));
            System.out.println("Time: " + clock.now() + "   Rounds Left: " + state.getRoundsRemaining());
            System.out.println("Objectives: Get Artifact " + progress[0] + "/" + needed[0]
                    + "   Activate Gate " + progress[1] + "/" + needed[1]
                    + "   Clear Enemies " + progress[2] + "/" + needed[2]);

            TimedRaidState.Action action;
            String actionText;
            if (step % 3 == 0) {
                action = TimedRaidState.Action.WORK_ON_OBJECTIVE_1;
                actionText = "Get Artifact";
            } else if (step % 3 == 1) {
                action = TimedRaidState.Action.WORK_ON_OBJECTIVE_2;
                actionText = "Activate Gate";
            } else {
                action = TimedRaidState.Action.WORK_ON_OBJECTIVE_3;
                actionText = "Clear Enemies";
            }

            System.out.println("Action: " + actionText);
            state.applyAction(state.getCurrentPlayer(), action);
            System.out.println("----------------------------");
            step++;
        }

        System.out.println("FINAL RESULT: " + state.getResult());
        System.out.println("Final World Time: " + clock.now());
        System.out.println("Final Progress: " + formatProgress(state.getProgressCopy()));
    }

    private static String formatPlayer(TimedRaidState.PlayerId player) {
        return player == TimedRaidState.PlayerId.P1 ? "Player 1" : "Player 2";
    }

    private static String formatProgress(int[] progress) {
        return "[Get Artifact=" + progress[0]
                + ", Activate Gate=" + progress[1]
                + ", Clear Enemies=" + progress[2] + "]";
    }
}