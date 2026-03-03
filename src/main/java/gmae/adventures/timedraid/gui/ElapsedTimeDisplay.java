package gmae.adventures.timedraid.gui;

import gmae.adventures.timedraid.TimedRaidState;

public class ElapsedTimeDisplay implements TimeDisplayStrategy {
    @Override
    public String name() { return "Elapsed"; }

    @Override
    public String format(TimedRaidState state) {
        return state.getElapsedMinutes() + " min elapsed";
    }
}
