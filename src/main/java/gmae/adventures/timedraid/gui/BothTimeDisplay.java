package gmae.adventures.timedraid.gui;

import gmae.adventures.timedraid.TimedRaidState;

public class BothTimeDisplay implements TimeDisplayStrategy {
    @Override
    public String name() { return "World + Elapsed"; }

    @Override
    public String format(TimedRaidState state) {
        return state.getClock().now() + " | " + state.getElapsedMinutes() + " min elapsed";
    }
}
