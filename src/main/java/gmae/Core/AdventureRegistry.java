package gmae.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdventureRegistry {

    private final Map<String, MiniAdventure> adventures = new LinkedHashMap<>();

    public void register(MiniAdventure adventure) {
        adventures.put(adventure.id(), adventure);
    }

    public MiniAdventure getById(String id) {
        return adventures.get(id);
    }

    public List<MiniAdventure> getAll() {
        return new ArrayList<>(adventures.values());
    }

    public int size() {
        return adventures.size();
    }
}
