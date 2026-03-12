package gmae.core;

import guildquest.model.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileManager {

    private final List<PlayerProfile> profiles;

    public ProfileManager() {
        this.profiles = new ArrayList<>();
    }

    // Create new profile
    public PlayerProfile createProfile(String charName, String realmPreference) {
        PlayerProfile profile = new PlayerProfile(charName, realmPreference);
        profiles.add(profile);
        return profile;
    }

    public List<PlayerProfile> getProfiles() {
        return List.copyOf(profiles);
    }

    // returning profiles
    public PlayerProfile getByName(String name) {
        for (PlayerProfile profile : profiles) {
            if (profile.getCharName().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        return null;
    }

    public int size() {
        return profiles.size();
    }
}
