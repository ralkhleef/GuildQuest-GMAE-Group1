package gmae.adventures.timedraid.gui;

import gmae.adventures.timedraid.TimedRaidState;

public interface TimeDisplayStrategy {
    String name();
    String format(TimedRaidState state);
}
