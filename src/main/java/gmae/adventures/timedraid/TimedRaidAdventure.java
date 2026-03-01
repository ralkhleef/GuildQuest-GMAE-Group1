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

    public TimedRaidAdventure(int maxRounds, int roundMinutes, int[] required) {
        this.maxRounds = maxRounds;
        this.roundMinutes = roundMinutes;
        this.required = Arrays.copyOf(required, required.length);
    }

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
        state.applyAction(player, action);
    }

    @Override
    public String getStatus() {
        if (state == null) return "Not started.";
        return ""
                + "Player Turn: " + state.getCurrentPlayer() + "\n"
                + "Rounds Remaining: " + state.getRoundsRemaining() + "\n"
                + "World Time: " + clock.now() + "\n"
                + "Progress: " + Arrays.toString(state.getProgressCopy()) + "\n"
                + "Required: " + Arrays.toString(state.getRequiredCopy()) + "\n"
                + "Commands: obj1, obj2, obj3, pass";
    }

    @Override
    public boolean isOver() {
        return state != null && state.isOver();
    }

    @Override
    public String getResult() {
        if (state == null) return "No game played.";
        return "FINAL RESULT: " + state.getResult() + " | Final Time: " + clock.now()
                + " | Progress: " + Arrays.toString(state.getProgressCopy());
    }

    @Override
    public void reset() {
        this.clock = new WorldClock(new GlobalTime(0));
        this.state = new TimedRaidState(clock, maxRounds, roundMinutes, required);
    }

    private TimedRaidState.Action parseAction(String input) {
        if (input == null) return TimedRaidState.Action.PASS;
        String s = input.trim().toLowerCase();

        return switch (s) {
            case "obj1", "1", "o1" -> TimedRaidState.Action.WORK_ON_OBJECTIVE_1;
            case "obj2", "2", "o2" -> TimedRaidState.Action.WORK_ON_OBJECTIVE_2;
            case "obj3", "3", "o3" -> TimedRaidState.Action.WORK_ON_OBJECTIVE_3;
            default -> TimedRaidState.Action.PASS;
        };
    }
}