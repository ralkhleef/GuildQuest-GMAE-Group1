package gmae.adventures.timedraid.gui;

import gmae.adventures.timedraid.TimedRaidState;

public class WorldTimeDisplay implements TimeDisplayStrategy {
    @Override
    public String name() { return "World Time"; }

    @Override
    public String format(TimedRaidState state) {
        return String.valueOf(state.getClock().now());
    }
}
