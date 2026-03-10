package guildquest.model;

import java.util.*;
import java.util.stream.Stream;

public class Realm {

    private final Map<Location, List<Locatable>> atPosition = new HashMap<>();
    private final Set<Locatable> all = new HashSet<>();

    public void place(Locatable locatable) {
        if (locatable == null) return;
        Location loc = locatable.getLocation();
        all.add(locatable);
        atPosition.computeIfAbsent(loc, k -> new ArrayList<>()).add(locatable);
    }
    
    public void remove(Locatable locatable) {
        if (locatable == null) return;
        all.remove(locatable);
        Location loc = locatable.getLocation();
        List<Locatable> list = atPosition.get(loc);
        if (list != null) {
            list.remove(locatable);
            if (list.isEmpty()) atPosition.remove(loc);
        }
    }

    public void updatePosition(Locatable locatable, Location from, Location to) {
        if (locatable == null) return;
        List<Locatable> oldList = atPosition.get(from);
        if (oldList != null) {
            oldList.remove(locatable);
            if (oldList.isEmpty()) atPosition.remove(from);
        }
        atPosition.computeIfAbsent(to, k -> new ArrayList<>()).add(locatable);
    }

    public List<Locatable> getAt(Location loc) {
        List<Locatable> list = atPosition.get(loc);
        return list == null ? List.of() : List.copyOf(list);
    }
    
    public Stream<Locatable> allLocatables() {
        return all.stream();
    }
}
