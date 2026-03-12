package guildquest.model;

import java.util.ArrayList;
import java.util.List;

public class RealmManager {

    private final List<Realm> realms;

    public RealmManager() {
        this.realms = new ArrayList<>();
    }

    public Realm createRealm(String id, String inpName) {
        Realm newRealm = new Realm(id, inpName);
        realms.add(newRealm);
        return newRealm;
    }

    public Realm getByName(String name) {
        for (Realm realms : realms) {
            if (realms.getName().equalsIgnoreCase(name)) {
                return realms;
            }
        }
        return null;
    }

    public List<Realm> getRealms() {
        return List.copyOf(realms);
    }

    public int size() {
        return realms.size();
    }

}
