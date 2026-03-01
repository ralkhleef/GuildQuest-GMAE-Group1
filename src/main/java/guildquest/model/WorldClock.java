package guildquest.model;

import java.util.ArrayList;
import java.util.List;

public class WorldClock {

    private GlobalTime currentTime;

    public interface ClockListener {
        void onTimeChanged(GlobalTime newTime);
    }

    private final List<ClockListener> listeners = new ArrayList<>();

    public WorldClock(GlobalTime start) {
        if (start == null) throw new IllegalArgumentException("start time cannot be null");
        this.currentTime = start;
    }

    public GlobalTime now() {
        return currentTime;
    }

    public void addListener(ClockListener l) {
        if (l != null && !listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void removeListener(ClockListener l) {
        listeners.remove(l);
    }

    public void advance(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("minutes must be >= 0");
        if (minutes == 0) return;

        currentTime = currentTime.plus(minutes);
        notifyListeners();
    }

    private void notifyListeners() {
        for (ClockListener l : List.copyOf(listeners)) {
            l.onTimeChanged(currentTime);
        }
    }
}