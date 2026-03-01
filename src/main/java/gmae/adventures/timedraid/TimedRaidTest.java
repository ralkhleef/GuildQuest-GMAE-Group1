package gmae.adventures.timedraid;

import guildquest.model.GlobalTime;
import guildquest.model.WorldClock;

import java.util.Arrays;

public class TimedRaidTest {

    public static void main(String[] args) {

        WorldClock clock = new WorldClock(new GlobalTime(0));
        TimedRaidState state = new TimedRaidState(
                clock,
                5,                      // maxRounds
                10,                     // minutes per round
                new int[]{2, 2, 2}      // required objectives
        );

        int step = 0;

        while (!state.isOver()) {
            System.out.println("Current Player: " + state.getCurrentPlayer());
            System.out.println("Rounds Remaining: " + state.getRoundsRemaining());
            System.out.println("World Time: " + clock.now());
            System.out.println("Progress: " + Arrays.toString(state.getProgressCopy()));
            System.out.println("Required: " + Arrays.toString(state.getRequiredCopy()));

            // simple rotation so objectives actually complete
            TimedRaidState.Action action;
            if (step % 3 == 0) action = TimedRaidState.Action.WORK_ON_OBJECTIVE_1;
            else if (step % 3 == 1) action = TimedRaidState.Action.WORK_ON_OBJECTIVE_2;
            else action = TimedRaidState.Action.WORK_ON_OBJECTIVE_3;

            state.applyAction(state.getCurrentPlayer(), action);

            System.out.println("----------------------------");
            step++;
        }

        System.out.println("FINAL RESULT: " + state.getResult());
        System.out.println("Final World Time: " + clock.now());
        System.out.println("Final Progress: " + Arrays.toString(state.getProgressCopy()));
    }
}